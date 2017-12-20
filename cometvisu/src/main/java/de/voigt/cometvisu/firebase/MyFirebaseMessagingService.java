package de.voigt.cometvisu.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import de.voigt.cometvisu.CometVisuApp;
import de.voigt.cometvisu.MainActivity;
import de.voigt.cometvisu.PushNotificationDialogFragment;
import de.voigt.cometvisu.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static int id = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Bundle extras = createExtrasFromMessage(remoteMessage);

        CometVisuApp app = (CometVisuApp) this.getApplicationContext();
        if (app.getCurrentActivity() != null && MainActivity.class.getName().equals(app.getCurrentActivity().getClass().getName())){
            app.showDialog(extras);
        }else{
            NotificationCompat.Builder notificationBuilder = createNotificationBuilder(remoteMessage,extras);
            Notification notification = notificationBuilder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
        }
    }

    @NonNull
    private NotificationCompat.Builder createNotificationBuilder(RemoteMessage remoteMessage, Bundle extras) {
        
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtras(extras);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.cv_icon);
        notificationBuilder.setContentText(extras.getString(PushNotificationDialogFragment.MESSAGE));
        notificationBuilder.setContentTitle(extras.getString(PushNotificationDialogFragment.TITLE));
        notificationBuilder.setUsesChronometer(true);
        notificationBuilder.setAutoCancel(true);

        // TODO
        //notificationBuilder.setVibrate()
        //notificationBuilder.setPriority()
        //notificationBuilder.setLights()
        //notificationBuilder.setStyle()
        //notificationBuilder.setColor()

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        return notificationBuilder;
    }

    @NonNull
    private Bundle createExtrasFromMessage(RemoteMessage remoteMessage) {
        Bundle extras = new Bundle();

        String title = null;
        String message = null;

        Map<String, String> data = remoteMessage.getData();
        if (data != null && data.size() > 0) {
            title = data.get(PushNotificationDialogFragment.TITLE_KEY);
            message = data.get(PushNotificationDialogFragment.MESSAGE_KEY);
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            if (title == null) {
                title = notification.getTitle();
            }
            if (message == null) {
                message = notification.getBody();
            }
        }

        extras.putString(PushNotificationDialogFragment.TITLE, title);
        extras.putString(PushNotificationDialogFragment.MESSAGE, message);
        return extras;
    }

}
