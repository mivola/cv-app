package de.voigt.cometvisu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

    final Activity activity = this;

    private WebView webView;

    private ImageButton reloadBtn;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int orientation = this.getRequestedOrientation();
        int rotation = ((WindowManager) this.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                System.out.println("rotation 0");
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                System.out.println("rotation 90");
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                System.out.println("rotation 180");
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                System.out.println("rotation default");
                break;
        }

        this.setRequestedOrientation(orientation);

        reloadBtn = (ImageButton) findViewById(R.id.reloadBtn);
        reloadBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 pd = ProgressDialog.show(activity, "", LOADING_MESSAGE,true);
                webView.reload();
             }
        });

        reloadBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
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
        webView.loadUrl("http://wiregate302/visu-svn");
        //webView.loadUrl("https://mivola.dyndns.org/visu-svn");
        //webView.loadUrl("http://www.google.com");

    }



/**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
**/
}
