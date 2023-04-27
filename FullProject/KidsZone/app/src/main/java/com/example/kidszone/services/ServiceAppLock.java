package com.example.kidszone.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.kidszone.broadcast.ReceiverApplock;

public class ServiceAppLock extends IntentService { // NOT USED TIMER SARA FREEZE CASE


    public ServiceAppLock() {
        super("ServiceApplock");
    }

    private void runAppLock() {
//        long endTime = System.currentTimeMillis() + 210;
        while(true) { //while (System.currentTimeMillis() < endTime)
            synchronized (this) {
                try {
                    Intent intent = new Intent(this, ReceiverApplock.class);
                    sendBroadcast(intent);
//                    wait(endTime - System.currentTimeMillis());
                    wait(210);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        runAppLock();
        // return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        BackgroundManager.getInstance().init(this).startService();
//        BackgroundManager.getInstance().init(this).startAlarmManager();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {
        BackgroundManager.getInstance().init(this).startService();
//        BackgroundManager.getInstance().init(this).startAlarmManager();
        super.onDestroy();
    }
}
