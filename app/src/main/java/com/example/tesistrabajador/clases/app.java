package com.example.tesistrabajador.clases;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class app extends Application {

    public static final String CHANNERL_1_ID = "CHANNEL1";
    public static final String CHANNERL_2_ID = "CHANNEL2";

    @Override
    public void onCreate() {
        super.onCreate();


        createnotificationsChannels();

    }


    private  void  createnotificationsChannels(){

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){

            NotificationChannel channel1 = new NotificationChannel(
                    CHANNERL_1_ID,
                    "channel1",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel1.enableLights(true);
            channel1.enableVibration(true);


            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }

    }
}
