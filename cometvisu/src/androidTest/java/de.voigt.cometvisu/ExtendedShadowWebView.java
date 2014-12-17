package de.voigt.cometvisu;

import android.webkit.WebSettings;
import android.webkit.WebView;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowWebView;

@Implements(value = WebView.class, inheritImplementationMethods = true)
public class ExtendedShadowWebView extends ShadowWebView {

    private WebSettings webSettings = new de.voigt.cometvisu.ExtendedTestWebSettings();
    @Implementation
    public WebSettings getSettings() {
        return webSettings;
    }

}
