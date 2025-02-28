package com.example.testinsapp.activities;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.testinsapp.R;
import com.example.testinsapp.utils.FirebaseUtil;
import com.google.firebase.firestore.DocumentReference;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
documentReference= FirebaseUtil.allUserCollectionRef().document(FirebaseUtil.getCurrentUserId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update("availability", 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update("availability", 1);
    }

    public void makeNotif(Context context, String title, String content){
        String chanelId="CHANNEL_ID_NOTIF";
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,chanelId);
        builder.setSmallIcon(R.drawable.notif)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent=new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel=notificationManager.getNotificationChannel(chanelId);
            if(notificationChannel==null){
                int importance=NotificationManager.IMPORTANCE_HIGH;
                notificationChannel=new NotificationChannel(chanelId,"hi tro",importance);
                notificationChannel.setLightColor(R.color.secondary);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0,builder.build());
    }
}
