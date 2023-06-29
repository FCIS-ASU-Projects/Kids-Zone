package com.example.kidszone.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.kidszone.activites.ScreenTimerActivity;

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
                ScreenTimerActivity.binding.startTimerButton.performClick();
                isScreenClosedWhileTimerIsRunning = false;
            }
        }
        else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            isScreenLocked = true;
            if(ScreenTimerActivity.mTimerRunning)
            {
                ScreenTimerActivity.binding.startTimerButton.performClick();
                isScreenClosedWhileTimerIsRunning = true;
            }
        }
    }
}

