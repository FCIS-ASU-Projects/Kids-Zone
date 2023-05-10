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

    public static boolean isScreenClosedWhileTimerIsRunning=false;
    public static boolean isScreenClosedWhileCameraIsRunning=false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            if(isScreenClosedWhileTimerIsRunning)
            {
                TimerActivity.binding.startTimerButton.performClick();
                isScreenClosedWhileTimerIsRunning = false;
            }
            if(isScreenClosedWhileCameraIsRunning)
            {
                HomeActivity.binding.appSwitch.performClick();
                isScreenClosedWhileCameraIsRunning = false;
            }
        }
        else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            if(TimerActivity.mTimerRunning)
            {
                TimerActivity.binding.startTimerButton.performClick();
                isScreenClosedWhileTimerIsRunning = true;
            }
            if(HomeActivity.IS_CAMERA_RUNNING)
            {
                HomeActivity.binding.appSwitch.performClick();
                isScreenClosedWhileCameraIsRunning = true;
            }
        }

    }
}

