package de.voigt.cometvisu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends Activity {
    protected CometVisuApp app;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (CometVisuApp) this.getApplicationContext();
    }

    protected void onResume() {
        super.onResume();
        app.setCurrentActivity(this);
    }

    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences() {
        Activity currActivity = app.getCurrentActivity();
        if (this.equals(currActivity)) {
            app.setCurrentActivity(null);
        }
    }

    public void showDialog(Bundle extras) {
        if (extras != null && extras.size() > 0 
                && extras.containsKey(PushNotificationDialogFragment.TITLE) 
                && extras.containsKey(PushNotificationDialogFragment.MESSAGE)) {
            PushNotificationDialogFragment dialog = new PushNotificationDialogFragment();
            dialog.setArguments(extras);
            dialog.show(getFragmentManager(), null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showDialog(intent.getExtras());
    }
}