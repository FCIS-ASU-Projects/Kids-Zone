package com.example.kidszone.broadcast;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.kidszone.HomeActivity;
import com.example.kidszone.activites.ScreenBlocker;
import com.example.kidszone.services.FreezeService;
import com.example.kidszone.shared.SharedPrefUtil;
import com.example.kidszone.utils.BlockAppsUtils;

import java.util.List;

public class ReceiverApplock extends BroadcastReceiver {

    public static void killThisPackageIfRunning(final Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(startMain);
        activityManager.killBackgroundProcesses(packageName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        BlockAppsUtils utils = new BlockAppsUtils(context);
        SharedPrefUtil prefUtil = new SharedPrefUtil(context);
        List<String> lockedApps = prefUtil.getLockedAppsList();
        String appRunning = utils.getLauncherTopApp();

        Log.d("ReceiverApplock: onReceive --> appRunning", appRunning);

        // TODO CHECK IF CURRENT APP FROM BLOCKED APPS & THE CAMERA IS RUNNING
        if(HomeActivity.IMAGE_CURRENT_AGE_CLASS != -1 && HomeActivity.classFromAge.get(HomeActivity.AGE_TO_BE_BLOCKED_FOR) >= HomeActivity.IMAGE_CURRENT_AGE_CLASS && HomeActivity.IS_CAMERA_RUNNING)
        {
            Log.d("ReceiverAppLock: onReceive --> THIS IS BLOCKED APP", appRunning);
            HomeActivity.IS_BLOCK_ON=true;

            // TODO CHECK IF CURRENT APP SHOULD BE BLOCKED FOR CURRENT USER
            if (lockedApps.contains(appRunning)){
                // TODO BLOCK THIS APP
                Log.d("ReceiverAppLock: onReceive --> ", "USER IS SMALLER THAN THRESHOLD");
                prefUtil.clearLastApp();
                prefUtil.setLastApp(appRunning);
                killThisPackageIfRunning(context, appRunning);
                Intent i = new Intent(context, ScreenBlocker.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("broadcast_receiver", "broadcast_receiver");
                context.startActivity(i);
            }
            else{
                // TODO DO NOT BLOCK THE APP
                Log.d("ReceiverAppLock: onReceive --> ", "USER IS BIGGER THAN THRESHOLD");
            }
        }
        else{
            // TODO DO NOT BLOCK THE APP
            HomeActivity.IS_BLOCK_ON=false;
            Log.d("ReceiverAppLock: onReceive --> ", "USER IS BIGGER THAN THRESHOLD");
        }

    }
}
