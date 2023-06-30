package com.example.kidszone.services;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BackgroundManager {
    private static BackgroundManager instance;
    private Context context;

    public static BackgroundManager getInstance() {
        if (instance == null) {
            instance = new BackgroundManager();
        }
        return instance;
    }

    public BackgroundManager init(Context ctx) {
        context = ctx;
        return this;
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isServiceRunning(ServiceAppLockJobIntent.class)) {
                Intent intent = new Intent(context, ServiceAppLockJobIntent.class);
                ServiceAppLockJobIntent.enqueueWork(context, intent);
            }
        } else {
            if (!isServiceRunning(ServiceAppLock.class)) {
                context.startService(new Intent(context, ServiceAppLock.class));
            }
        }
    }
}
