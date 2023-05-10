package com.example.kidszone.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.kidszone.HomeActivity;
import com.example.kidszone.activites.TimerActivity;
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
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        BackgroundManager.getInstance().init(this).startService();
        Log.d("ServiceAppLockJobIntent --> ", "OOOOOOOPPPPPPSS: DESTROYED");
        super.onDestroy();
    }

    private void runAppLock() {

        while (true) {
            synchronized (this) {
                try {
                    Log.d("ServiceAppLockJobIntent --> ", "===========================================================");
                    if (HomeActivity.IMAGE_CURRENT_AGE_CLASS!=-1)
                    {
                        Log.d("IMAGE_CURRENT_AGE_CLASS --> ", Integer.toString(HomeActivity.IMAGE_CURRENT_AGE_CLASS));
                        Log.d("AGE_TO_BE_BLOCKED_FOR --> ", Integer.toString(HomeActivity.classFromAge.get(HomeActivity.AGE_TO_BE_BLOCKED_FOR)));
                        Log.d("IS_BLOCK_ON --> ", Boolean.toString(HomeActivity.IS_BLOCK_ON));
                        Log.d("IS_TIMER_RUNNING --> ", Boolean.toString(TimerActivity.mTimerRunning));
                    }
                    Log.d("ServiceAppLockJobIntent --> ", "===========================================================");

                    Intent intent = new Intent(this, ReceiverApplock.class);
                    sendBroadcast(intent);

                    if(!HomeActivity.IS_BLOCK_ON && HomeActivity.IS_FREEZE_ON)
                    {
                        // TODO FREE THE MOBILE
                        HomeActivity.IS_FREEZE_ON = false;
                        stopService(new Intent(getApplicationContext(), FreezeService.class));
                    }

                    if(HomeActivity.IS_BLOCK_ON && !TimerActivity.mTimerRunning)
                    {
                        // TODO START TIMER
                        Log.d("ServiceAppLockJobIntent --> ", "+++++++++++++++++++++++++++++++++++++++++++++++++++");
                        Log.d("START TIMER", "START TIMER");
                        startService(new Intent(getApplicationContext(), TimerService.class));
                        Log.d("ServiceAppLockJobIntent --> ", "+++++++++++++++++++++++++++++++++++++++++++++++++++");
                    }

                    else if(!HomeActivity.IS_BLOCK_ON && TimerActivity.mTimerRunning)
                    {
                        // TODO PAUSE TIMER
                        Log.d("ServiceAppLockJobIntent --> ", "+++++++++++++++++++++++++++++++++++++++++++++++++++");
                        Log.d("PAUSE TIMER", "PAUSE TIMER");
                        TimerActivity.mCountDownTimer.cancel();
                        TimerActivity.mTimerRunning = false;
                        TimerActivity.updateButtons();

                        stopService(new Intent(getApplicationContext(), TimerService.class));
                        Log.d("ServiceAppLockJobIntent --> ", "+++++++++++++++++++++++++++++++++++++++++++++++++++");
                    }

                    wait(210);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
