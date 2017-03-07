package de.voigt.cometvisu;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

public class CometVisuApp extends Application {

    private BaseActivity currentActivity = null;

    public void onCreate() {
        super.onCreate();
    }

    public BaseActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(BaseActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void showDialog(Bundle extras) {
        if (currentActivity != null){
            currentActivity.showDialog(extras);
        }else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }
}