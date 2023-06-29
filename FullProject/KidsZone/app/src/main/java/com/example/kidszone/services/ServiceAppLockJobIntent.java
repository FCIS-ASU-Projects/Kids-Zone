package com.example.kidszone.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.kidszone.HomeActivity;
import com.example.kidszone.activites.TimerActivity;
import com.example.kidszone.broadcast.ReceiverApplock;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceAppLockJobIntent extends JobIntentService { // SUBCLASS FROM SERVICE
    private static final int JOB_ID = 15462;
    Date currentDate;
    SimpleDateFormat dateFormat;
    SimpleDateFormat hoursDateFormat;

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
        Log.d("ServiceAppLockJobIntent --> ", "DESTROYED");
        super.onDestroy();
    }

    private void runAppLock() {

        while (true) {
            synchronized (this) {
                try {

                    // TODO PRINT SOME LOGS
                    logCurrentAgeInfo();

                    // TODO CHECK IF CURRENT APP SHOULD BE BLOCKED OR NOT
                    Intent intent = new Intent(this, ReceiverApplock.class);
                    sendBroadcast(intent);

                    // TODO GET THE CURRENT LIVE TIME
                    getCurrentLiveTime();

                    // TODO RESTART TIMER AT SPECIFIC TIME
                    restartTimerAtSpecificTime();

                    // TODO FREEZE THE MOBILE IF THE TIMER IS FINISHED
                    if(HomeActivity.IS_BLOCK_ON && HomeActivity.IS_TIMER_FOR_TODAY_FINISHED && !HomeActivity.IS_FREEZE_ON){
                        HomeActivity.IS_FREEZE_ON=true;
                        startService(new Intent(getApplicationContext(), FreezeService.class));
                    }

                    // TODO FREE THE MOBILE
                    if(!HomeActivity.IS_BLOCK_ON && HomeActivity.IS_FREEZE_ON)
                    {
                        HomeActivity.IS_FREEZE_ON = false;
                        stopService(new Intent(getApplicationContext(), FreezeService.class));
                    }

                    // TODO SET TIMER (START OR PAUSE)
                    setTime();

                    // TODO WAIT
                    wait(210);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void logCurrentAgeInfo(){

        Log.d("ServiceAppLockJobIntent --> ", "===========================================================");

        if(HomeActivity.IS_FREEZE_ON)
            Log.d("ServiceAppLockJobIntent --> ", "Freeze on");
        if(!HomeActivity.IS_FREEZE_ON)
            Log.d("ServiceAppLockJobIntent --> ", "Freeze off");

        Log.d("IS_BLOCK_ON --> ", Boolean.toString(HomeActivity.IS_BLOCK_ON));

        if(HomeActivity.AGE_TO_BE_BLOCKED_FOR==null)
            Log.d("HomeActivity.AGE_TO_BE_BLOCKED_FOR ", "NULL");
        if(HomeActivity.classFromAge==null)
            Log.d("HomeActivity.classFromAge ", "NULL");

        if (HomeActivity.IMAGE_CURRENT_AGE_CLASS!=-1)
        {
            Log.d("IMAGE_CURRENT_AGE_CLASS --> ", Integer.toString(HomeActivity.IMAGE_CURRENT_AGE_CLASS));
            Log.d("AGE_TO_BE_BLOCKED_FOR --> ", Integer.toString(HomeActivity.classFromAge.get(HomeActivity.AGE_TO_BE_BLOCKED_FOR)));
            Log.d("IS_TIMER_RUNNING --> ", Boolean.toString(TimerActivity.mTimerRunning));
        }
        Log.d("ServiceAppLockJobIntent --> ", "===========================================================");
    }
    @SuppressLint("SimpleDateFormat")
    private void getCurrentLiveTime(){
        // TODO GET THE CURRENT LIVE TIME
        currentDate = new Date();
        dateFormat = new SimpleDateFormat("kk:mm:ss");
        Log.d("ServiceAppLockJobIntent --> Current Time in 24 hr format = ", dateFormat.format(currentDate));
        hoursDateFormat = new SimpleDateFormat("kk");
        Log.d("ServiceAppLockJobIntent --> Current hour in 24 hr format = ", hoursDateFormat.format(currentDate));
    }
    private void restartTimerAtSpecificTime(){
        if(!TimerActivity.mTimerRunning && HomeActivity.IS_TIMER_FOR_TODAY_FINISHED && Integer.parseInt(hoursDateFormat.format(currentDate)) == HomeActivity.TIMER_RESTARTS_AT)
        {
            Log.d(Integer.toString(HomeActivity.TIMER_RESTARTS_AT), Integer.toString(HomeActivity.TIMER_RESTARTS_AT));
            HomeActivity.IS_TIMER_FOR_TODAY_FINISHED = false;
            TimerActivity.mTimeLeftInMillis = TimerActivity.START_TIME_IN_MILLIS;
        }
    }
    private void setTime(){

        // TODO START TIMER
        if(HomeActivity.IS_CAMERA_RUNNING && !HomeActivity.IS_FREEZE_ON && HomeActivity.IS_BLOCK_ON && !TimerActivity.mTimerRunning)
        {
            Log.d("ServiceAppLockJobIntent --> ", "+++++++++++++++++++++++++++++++++++++++++++++++++++");
            Log.d("START TIMER", "START TIMER");
            startService(new Intent(getApplicationContext(), TimerService.class));
            Log.d("ServiceAppLockJobIntent --> ", "+++++++++++++++++++++++++++++++++++++++++++++++++++");
        }

        // TODO PAUSE TIMER
        else if(!HomeActivity.IS_BLOCK_ON && TimerActivity.mTimerRunning)
        {
            Log.d("ServiceAppLockJobIntent --> ", "+++++++++++++++++++++++++++++++++++++++++++++++++++");
            Log.d("PAUSE TIMER", "PAUSE TIMER");
            TimerActivity.mCountDownTimer.cancel();
            TimerActivity.mTimerRunning = false;
            //TimerActivity.updateButtons();

            stopService(new Intent(getApplicationContext(), TimerService.class));
            Log.d("ServiceAppLockJobIntent --> ", "+++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
    }
}
