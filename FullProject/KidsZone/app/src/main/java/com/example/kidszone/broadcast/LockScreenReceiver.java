package com.example.kidszone.broadcast;

import static android.content.Context.POWER_SERVICE;


import static androidx.core.content.ContextCompat.getSystemService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.kidszone.activites.TimerActivity;
import com.example.kidszone.services.TimerService;

public class LockScreenReceiver extends BroadcastReceiver {

    public static boolean isScreenClosedWhileTimerIsRunning=false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_ON.equals(action) && isScreenClosedWhileTimerIsRunning) {
            TimerActivity.binding.startTimerButton.performClick();
            isScreenClosedWhileTimerIsRunning = false;
        }
        else if (Intent.ACTION_SCREEN_OFF.equals(action) && TimerActivity.mTimerRunning) {
            TimerActivity.binding.startTimerButton.performClick();
            isScreenClosedWhileTimerIsRunning = true;
        }

    }
}

