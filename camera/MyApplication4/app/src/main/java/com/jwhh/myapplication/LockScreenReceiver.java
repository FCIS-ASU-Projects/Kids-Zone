package com.jwhh.myapplication;

import static android.content.Context.POWER_SERVICE;


import static androidx.core.content.ContextCompat.getSystemService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

public class LockScreenReceiver extends BroadcastReceiver {

    public static boolean screenOn=false;

    @Override
    public void onReceive(Context context, Intent intent) {
        //if(TimerService.screenOn==true)
        //Toast.makeText(context, "screen on", Toast.LENGTH_LONG).show();
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            screenOn=false;
            TimerService.counterCanceled=true;
            TimerService.countDownTimer. cancel();
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            TimerService.counterCanceled=false;
            screenOn=true;
            TimerService.countDownTimer.start();

        }
    }
}

