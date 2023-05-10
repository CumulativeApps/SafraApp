package com.safra.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.safra.Dashboard;
import com.safra.R;
import com.safra.SignIn;
import com.safra.utilities.UserSessionManager;

import java.util.Map;

import static com.safra.utilities.Common.NOTIFICATION_CHANNEL_ID;
import static com.safra.utilities.Common.NOTIFICATION_CHANNEL_NAME;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class NotificationService extends FirebaseMessagingService {

    public static final String TAG = "notification_service";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "onMessageReceived: data -> " + remoteMessage.getData().toString());

        showNotification(remoteMessage.getData());

    }

    private void showNotification(Map<String, String> data) {
        String title = data.get("noti_title");
        String body = data.get("noti_body");
        String type = data.get("noti_type");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        long notificationId = System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        Log.e(TAG, "showNotification: showing notification...");

        notificationBuilder
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_icon)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(getRespectiveActivityPendingIntent(data, type));

        notificationManager.notify((int) notificationId, notificationBuilder.build());

    }

    private PendingIntent getRespectiveActivityPendingIntent(Map<String, String> data, String type) {
        Intent intent;

        boolean isRemembered = UserSessionManager.getInstance().isRemembered();
        if (!isRemembered || userSessionManager.getUserId() <= 0) {
            intent = new Intent(getApplicationContext(), SignIn.class);
        } else {
            intent = new Intent(getApplicationContext(), Dashboard.class);
            if(type != null && (type.equals("form") || type.equals("task"))) {
                intent.putExtra("fragment_to_launch", Long.parseLong(data.get("noti_module")));
                intent.putExtra("refere", Long.parseLong(data.get("noti_reference_id")));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Log.e(TAG, "showNotification: is logged in");
        }

        return PendingIntent.getActivity(this,
                1, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED)
                .build();

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.setDescription("Complaint Notification Channel");
        notificationChannel.setLightColor(Color.GREEN);

        notificationChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes);
//            notificationChannel.setVibrationPattern(new long[]{0, 200});
        notificationChannel.enableVibration(true);
        notificationChannel.enableLights(true);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.e(TAG, "onNewToken: " + s);
        UserSessionManager.getInstance().updateFcmToken(s);
    }
}
