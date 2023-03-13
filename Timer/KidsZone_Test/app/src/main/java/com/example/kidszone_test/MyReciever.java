package com.example.kidszone_test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyReciever extends BroadcastReceiver {

    public static Boolean alarmReceived=false;
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Alarm", Toast.LENGTH_LONG).show();
        alarmReceived=true;
        //if(TimerService.counterCanceled==false)
        TimerService.countDownTimer.onFinish();


    }
}
