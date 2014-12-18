package de.voigt.cometvisu;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;
import de.voigt.cometvisu.ExtendedShadowWebView;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Config(emulateSdk=18, manifest="src/main/AndroidManifest.xml",shadows=ExtendedShadowWebView.class)
@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {


    @Test
    public void createActivity() throws Exception {

        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        controller.create().start();
        Activity activity = controller.get();

        assertThat(activity, notNullValue());
        View webView = activity.findViewById(R.id.webView1);
        assertThat(webView, notNullValue());
    }


    @Test
    public void createActivity2() throws Exception {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().visible().get();
        assertThat(activity, notNullValue());
        View webView = activity.findViewById(R.id.webView1);
        assertThat(webView, notNullValue());
    }

    @Test
    public void createActivityWithIntent() throws Exception {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Activity activity = Robolectric.buildActivity(UrlsListActivity.class).withIntent(intent).create().get();
        assertThat(activity, notNullValue());
    }

    @Test
    public void shouldBeTrue() throws Exception {
        assertThat("test", equalTo("test"));
    }

}