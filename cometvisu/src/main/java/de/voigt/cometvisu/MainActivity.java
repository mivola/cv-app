package de.voigt.cometvisu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private static final String LOADING_MESSAGE = "Lade Seite...";

    private static final String DEFAULT_VISU_URL = "http://wiregate/visu-svn";

    private String visuUrl = "";

    private final Activity activity = this;

    private WebView webView;

    private ImageButton reloadBtn;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = new ProgressDialog(activity);
        pd.setMessage(LOADING_MESSAGE);
        pd.setCancelable(true);
        pd.setIndeterminate(true);

        setOrientation();

        reloadBtn = (ImageButton) findViewById(R.id.reloadBtn);
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOrientation();
                visuUrl = "";
                loadSelectedURL();
            }
        });

        reloadBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent myIntent = new Intent(activity, UrlsListActivity.class);
                activity.startActivity(myIntent);

                return true;
            }
        });

        webView = (WebView) findViewById(R.id.webView1);
        webView.clearCache(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setAppCacheMaxSize(1);
        getApplicationContext().deleteDatabase("webview.db");
        getApplicationContext().deleteDatabase("webviewCache.db");

        webView.setWebChromeClient(new WebChromeClient() {
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.e("MyApplication", message + " -- From line "
                        + lineNumber + " of "
                        + sourceID);
            }
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.e("MyApplication", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (pd != null && pd.isShowing()) {
                    try {
                        pd.dismiss();
                    }catch (Throwable t){
                        // this happens on 4.4.4 in landscape orientation... Log.d("MyApplication", "error while dismissing pd", t);
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("MyApplication", description + " -- From line; errorCode: " + errorCode);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        loadSelectedURL();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            loadSelectedURL();
        }
    }

    private void loadSelectedURL() {
        String currentlySelectedUrl= getApplicationContext().getSharedPreferences(UrlsListActivity.class.getName(), Context.MODE_PRIVATE).getString(UrlsListActivity.VISU_SELECTED_URL_KEY, DEFAULT_VISU_URL);
        if (!visuUrl.equals(currentlySelectedUrl)) {
            visuUrl=currentlySelectedUrl;
            pd.show();

            Map<String, String> noCacheHeaders = new HashMap<String, String>(2);
            noCacheHeaders.put("Pragma", "no-cache");
            noCacheHeaders.put("Cache-Control", "no-cache");

            webView.loadUrl(visuUrl, noCacheHeaders);
        }
    }

    private void setOrientation() {
        int orientation;
        int rotation = ((WindowManager) this.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }
        this.setRequestedOrientation(orientation);
    }

}
