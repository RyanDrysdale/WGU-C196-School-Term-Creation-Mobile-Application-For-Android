package com.example.c196ryandrysdale.Util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


import static android.content.Context.NOTIFICATION_SERVICE;

import com.example.c196ryandrysdale.R;

public class Receiver extends BroadcastReceiver {
    static int notificationID;
    String channel_id="Course Alerts";
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,intent.getStringExtra("courseAlert"),Toast.LENGTH_LONG).show();

        createNotificationChannel(context,channel_id);
        Notification n= new NotificationCompat.Builder(context, channel_id)

                .setChannelId(channel_id)
                .setContentText("Scheduler Alert")
                .setContentTitle(intent.getStringExtra("courseAlert") + " : Notification ID "+Integer.toString(notificationID)).build();

        NotificationManager notificationManager=(NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++,n);
    }
    private void createNotificationChannel(Context context, String CHANNEL_ID) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}