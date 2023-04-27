package com.example.kidszone.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.kidszone.broadcast.ReceiverApplock;

public class ServiceAppLockJobIntent extends JobIntentService { // SUBCLASS FROM SERVICE
    private static final int JOB_ID = 15462;

    public static void enqueueWork(Context ctx, Intent work) {
        enqueueWork(ctx, ServiceAppLockJobIntent.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        runAppLock();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        BackgroundManager.getInstance().init(this).startService();
//        BackgroundManager.getInstance().init(this).startAlarmManager();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        BackgroundManager.getInstance().init(this).startService();
//        BackgroundManager.getInstance().init(this).startAlarmManager();
        Log.d("ServiceApplockJobIntent --> ", "OOOOOOOPPPPPPSS: DESTROYED");
        super.onDestroy();
    }

    private void runAppLock() {
//        long endTime = System.currentTimeMillis() + 210;
        while (true) { //while (System.currentTimeMillis() < endTime)
            synchronized (this) {
                try {
                    Intent intent = new Intent(this, ReceiverApplock.class);
                    sendBroadcast(intent);
//                    wait(endTime - System.currentTimeMillis());
                    wait(210);
                    Log.d("ServiceAppLockJobIntent --> ", "WAAAAAAAAAAAAAAIIIIIITTT");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
