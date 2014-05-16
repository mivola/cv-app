package de.voigt.cometvisu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    private static final String LOADING_MESSAGE = "Lade Seite...";

    private static final String DEFAULT_VISU_URL = "http://wiregate302/visu-svn";

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
                pd.show();
                setOrientation();
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
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (pd.isShowing() && pd != null) {
                    pd.dismiss();
                }
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
            webView.loadUrl(visuUrl);
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
