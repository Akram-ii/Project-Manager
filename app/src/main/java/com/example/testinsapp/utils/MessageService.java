package com.example.testinsapp.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.testinsapp.R;
import com.example.testinsapp.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessageService extends FirebaseMessagingService {

private NotificationManager notificationManager;
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
updateNewToken(token);

    }

    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Vibrator vibrator=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
long[] pattern={0,10,100,200};
vibrator.vibrate(pattern,-1);
        NotificationCompat.Builder builder =new NotificationCompat.Builder(this,"Notification");
        Intent resultIntent=new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        pendingIntent=PendingIntent.getActivity(this,1,resultIntent,PendingIntent.FLAG_MUTABLE);
        builder.setContentTitle(message.getNotification().getTitle());
        builder.setContentText(message.getNotification().getBody());
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.logosmall);
        builder.setVibrate(pattern);
        builder.setPriority(Notification.PRIORITY_MAX);

        notificationManager =(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId="Notification";
            NotificationChannel channel=new NotificationChannel(
                    channelId,"Coding",NotificationManager.IMPORTANCE_HIGH
            );
channel.enableLights(true);
channel.enableVibration(true);
channel.setVibrationPattern(pattern);
channel.canBypassDnd();


notificationManager.createNotificationChannel(channel);
builder.setChannelId(channelId);
        }
        notificationManager.notify(100,builder.build());
    }

    private void updateNewToken(String token){


    }
}
