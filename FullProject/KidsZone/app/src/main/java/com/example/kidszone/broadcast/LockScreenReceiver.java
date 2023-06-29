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

import com.example.kidszone.HomeActivity;
import com.example.kidszone.activites.TimerActivity;
import com.example.kidszone.services.TimerService;

public class LockScreenReceiver extends BroadcastReceiver {

    private boolean isScreenClosedWhileTimerIsRunning=false;
    public static boolean isScreenLocked=false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            isScreenLocked = false;
            if(isScreenClosedWhileTimerIsRunning)
            {
                TimerActivity.binding.startTimerButton.performClick();
                isScreenClosedWhileTimerIsRunning = false;
            }
        }
        else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            isScreenLocked = true;
            if(TimerActivity.mTimerRunning)
            {
                TimerActivity.binding.startTimerButton.performClick();
                isScreenClosedWhileTimerIsRunning = true;
            }
        }
    }
}

