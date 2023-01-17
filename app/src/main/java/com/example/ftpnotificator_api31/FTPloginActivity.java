package com.example.ftpnotificator_api31;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.*;

public class FTPloginActivity extends AppCompatActivity {
    final ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1);
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.Notification_channel_name);
            String description = getString(R.string.Notification_channel_description);
            NotificationChannel channel = new NotificationChannel(getString(R.string.Notification_channel_id), name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            channel.setLightColor(Color.BLUE);
            channel.setBypassDnd(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftplogin);
        createNotificationChannel();


        //Other code:
        Button cncl_btn = (Button) findViewById(R.id.cancel_btn);
        Button connect_btn = (Button) findViewById(R.id.connect_btn);
        Button notif_btn = (Button) findViewById(R.id.notif_testbtn);
        cncl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText i_hostname = (EditText) (findViewById(R.id.hostname_input));
                EditText i_username = (EditText) (findViewById(R.id.username_input));
                EditText i_password = (EditText) (findViewById(R.id.password_input));
                EditText i_path = (EditText) (findViewById(R.id.path_input));

                String hostname = i_hostname.getText().toString();
                String username = i_username.getText().toString();
                String password = i_password.getText().toString();
                String path = i_path.getText().toString();



                /*
                ftpParams is a String array, which consists of data gained from user input.
                [0] = hostname,
                [1] = user name
                [2] = password,
                [3] = path.
                 */

                String[] ftpParams = {hostname,username,password,path}; //

                FTPloginActivity.this.moveTaskToBack(true);
                ScheduleFtpChecking(ftpParams);

            }
        });


        notif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar sn = Snackbar.make(view, "Asddsa", Snackbar.LENGTH_SHORT);
                Teszt();
                sn.show();
            }
        });

    }

    /* After a succesful connection has been made, the app minimizes itself,
       and a push-up notification will appear: */
    public void ScheduleFtpChecking(String[] ftpParams) {
        Teszt();
        createNotificationChannel();
        scheduleService.scheduleAtFixedRate(new FTPPeriodicTask(ftpParams, this), 0, 5, TimeUnit.SECONDS);

            /*  After the creation of scheduledTask, the program will keep a seperate thread for checking on
                new files on the ftp connection periodically (currently set to 20 seconds.)
                Meanwhile, the user is free to do whatever he/she wants.
             */

    }

    public void Teszt() {

        Intent onTapIntent = new Intent(this, FTPloginActivity.class);

        PendingIntent onTapPendingIntent = PendingIntent.getActivity(this, 0, onTapIntent, PendingIntent.FLAG_IMMUTABLE); //Intent on user tapping the notification

        NotificationCompat.Builder builder = new NotificationCompat.Builder(FTPloginActivity.this, getString(R.string.Notification_channel_id))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("FTP Notificator")
                .setContentText("The connection is up and running. Listening on incoming files...")
                .setContentIntent(onTapPendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(FTPloginActivity.this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            managerCompat.notify(1,builder.build());
        }


    }


}