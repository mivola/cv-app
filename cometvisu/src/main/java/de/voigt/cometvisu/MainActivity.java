package de.voigt.cometvisu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
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
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    static final String APP_NAME = "CV-App";

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

        Log.e(APP_NAME, "==== onCreate: " + (savedInstanceState == null ? "null" : "NOT null"));

        if (savedInstanceState != null) {

            Log.e(APP_NAME, "==== savedInstanceState != null 1");
            webView = (WebView) findViewById(R.id.webView1);
            Log.e(APP_NAME, "==== savedInstanceState != null: " +webView);
            webView.restoreState(savedInstanceState);
            Log.e(APP_NAME, "==== savedInstanceState != null 2");

        }else {




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
                    Log.e(APP_NAME, message + " -- From line "
                            + lineNumber + " of "
                            + sourceID);
                }

                public boolean onConsoleMessage(ConsoleMessage cm) {
                    Log.e(APP_NAME, cm.message() + " -- From line "
                            + cm.lineNumber() + " of "
                            + cm.sourceId());
                    return true;
                }
            });

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (pd != null && pd.isShowing()) {
                        try {
                            pd.dismiss();
                        } catch (Throwable t) {
                            // this happens on 4.4.4 in landscape orientation... Log.d("MyApplication", "error while dismissing pd", t);
                        }
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    Log.e(APP_NAME, description + " -- From line; errorCode: " + errorCode);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
                    String currentlySelectedUrl = getCurrentlySelectedUrl();
//                if (Uri.parse(url).getHost().equals(Uri.parse(currentlySelectedUrl).getHost()) &&
                    //                      (Uri.parse(url).getPort() == Uri.parse(currentlySelectedUrl).getPort())) {
                    if (newUrl.startsWith(currentlySelectedUrl)) {
                        // this belongs to the currently selected page
                        return false;
                    }
                    // let the system handle all other links
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newUrl));
                    startActivity(intent);
                    return true;
                }

            });
            webView.getSettings().setJavaScriptEnabled(true);

            loadSelectedURL();

            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            ScreenReceiver screenReceiver = new ScreenReceiver(webView);
            registerReceiver(screenReceiver, filter);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(APP_NAME, "==== onSaveInstanceState");
        webView.saveState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(APP_NAME, "==== onRestoreInstanceState");
  //      webView.restoreState(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e(APP_NAME, "==== onWindowFocusChanged: "+ hasFocus);
        if (hasFocus) {
//            loadSelectedURL();
        }
    }

    private void loadSelectedURL() {
        String currentlySelectedUrl = getCurrentlySelectedUrl();
        if (!visuUrl.equals(currentlySelectedUrl)) {
            setOrientation();
            visuUrl = currentlySelectedUrl;
            pd.show();

            Map<String, String> noCacheHeaders = new HashMap<String, String>(2);
            noCacheHeaders.put("Pragma", "no-cache");
            noCacheHeaders.put("Cache-Control", "no-cache");

            webView.loadUrl(visuUrl, noCacheHeaders);
        }
    }

    private String getCurrentlySelectedUrl() {
        return getApplicationContext().getSharedPreferences(UrlsListActivity.class.getName(), Context.MODE_PRIVATE).getString(UrlsListActivity.VISU_SELECTED_URL_KEY, DEFAULT_VISU_URL);
    }

    private void setOrientation() {
        int orientation;

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(UrlsListActivity.class.getName(), Context.MODE_PRIVATE);
        int selectedOrientation = (int) prefs.getLong(UrlsListActivity.VISU_ORIENTATION_KEY, Orientation.Landscape.getValue());

        if (selectedOrientation == Orientation.Portrait.getValue()) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else if (selectedOrientation == Orientation.Landscape.getValue()) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else if (selectedOrientation == Orientation.ReversePortrait.getValue()) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        } else if (selectedOrientation == Orientation.ReverseLandscape.getValue()) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        } else {
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
        }
        this.setRequestedOrientation(orientation);
    }

    @Override
    protected void onPause() {
        // WHEN THE SCREEN IS ABOUT TO TURN OFF
        if (ScreenReceiver.wasScreenOn) {
            // THIS IS THE CASE WHEN ONPAUSE() IS CALLED BY THE SYSTEM DUE TO A SCREEN STATE CHANGE
            System.out.println("SCREEN TURNED OFF");
        } else {
            // THIS IS WHEN ONPAUSE() IS CALLED WHEN THE SCREEN STATE HAS NOT CHANGED
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        // ONLY WHEN SCREEN TURNS ON
        if (!ScreenReceiver.wasScreenOn) {
            // THIS IS WHEN ONRESUME() IS CALLED DUE TO A SCREEN STATE CHANGE
            System.out.println("SCREEN TURNED ON");
        } else {
            // THIS IS WHEN ONRESUME() IS CALLED WHEN THE SCREEN STATE HAS NOT CHANGED
        }
        super.onResume();
    }

}
