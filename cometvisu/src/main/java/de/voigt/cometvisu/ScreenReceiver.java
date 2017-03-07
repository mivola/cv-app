package de.voigt.cometvisu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by mvoigt on 06.10.2014.
 */
public class ScreenReceiver extends BroadcastReceiver {

    private static final String JAVASCRIPT_CALL = "javascript:templateEngine.visu.restart();";

    public static boolean wasScreenOn = true;

    private final WebView webView;

    public ScreenReceiver(final WebView webView){
        this.webView=webView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // nothing to do
            wasScreenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            wasScreenOn = true;
            Log.d(MainActivity.APP_NAME, "trying to call javascript: " + JAVASCRIPT_CALL);
            //webView.loadUrl("javascript:alert('test')");
            webView.loadUrl(JAVASCRIPT_CALL);        }
    }
}
