package de.voigt.cometvisu;

import android.webkit.TestWebSettings;

import org.robolectric.annotation.Implementation;

public class ExtendedTestWebSettings extends TestWebSettings {
    @Implementation
    public void setAppCacheMaxSize(long appCacheMaxSize) {
    }
}
