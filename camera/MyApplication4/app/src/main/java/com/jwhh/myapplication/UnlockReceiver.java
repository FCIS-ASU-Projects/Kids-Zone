package com.jwhh.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class UnlockReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        //desired app here using an Intent
        //Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.jwhh.myapplication");
        //context.startActivity(launchIntent);
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            // Launch your app here
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.jwhh.myapplication");
            if (launchIntent != null) {
                context.startActivity(launchIntent);
            }
        }
    }
}