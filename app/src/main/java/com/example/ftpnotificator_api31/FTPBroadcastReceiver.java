package com.example.ftpnotificator_api31;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


public class FTPBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);



        if(action.equals("filefound")) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.Notification_channel_id))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("New file")
                    .setContentText("A new uploaded file has been found!")
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true);


            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

                /*
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (pm.isInteractive() == false) {
                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "app:FTPNotificator");
                    wl.acquire(3000);
                }
                 */

                nm.notify(2,builder.build());






            }
        }

    }



}
