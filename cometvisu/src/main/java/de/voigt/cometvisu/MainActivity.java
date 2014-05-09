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

    public static final String LOADING_MESSAGE = "Lade Seite...";

    public static final String VISU_URL = "http://wiregate302/visu-svn";

    final Activity activity = this;

    private WebView webView;

    private ImageButton reloadBtn;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setOrientation();

        reloadBtn = (ImageButton) findViewById(R.id.reloadBtn);
        reloadBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 pd = ProgressDialog.show(activity, "", LOADING_MESSAGE,true);
                 setOrientation();
                 webView.loadUrl(VISU_URL);
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

        pd = ProgressDialog.show(this, "", LOADING_MESSAGE, true);

        webView = (WebView) findViewById(R.id.webView1);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if(pd.isShowing()&&pd!=null)
                {
                    pd.dismiss();
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(VISU_URL);

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
