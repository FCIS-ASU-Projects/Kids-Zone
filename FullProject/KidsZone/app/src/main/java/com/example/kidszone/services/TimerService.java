package com.example.kidszone.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.kidszone.HomeActivity;
import com.example.kidszone.R;
import com.example.kidszone.activites.ScreenTimerActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}
    @Override
    public void onCreate() {
        super.onCreate();

        ScreenTimerActivity.mCountDownTimer = new CountDownTimer(ScreenTimerActivity.mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                ScreenTimerActivity.mTimerRunning = true;
                ScreenTimerActivity.mTimeLeftInMillis = millisUntilFinished;
                if(ScreenTimerActivity.binding != null){
                    ScreenTimerActivity.updateCountDownText();
                    //TimerActivity.updateButtons();
                }

                notificationService();
                Log.d("TimerService: Info", "DECREASED SECOND");
            }

            @Override
            public void onFinish() {
                ScreenTimerActivity.mTimerRunning = false;
                ScreenTimerActivity.mTimeLeftInMillis = 0;
                HomeActivity.IS_TIMER_FOR_TODAY_FINISHED = true;
//                if(TimerActivity.binding != null)
//                    TimerActivity.updateButtons();

                notificationService();
//                Toast.makeText(getApplicationContext(), "TIMER IS FINISHED", Toast.LENGTH_LONG).show();

                // TODO Freeze the mobile
                HomeActivity.IS_FREEZE_ON = true;
                startService(new Intent(getApplicationContext(), FreezeService.class));
            }
        };
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(
                () -> ScreenTimerActivity.mCountDownTimer.start()
        ).start();

        return START_STICKY;
    }
    public void notificationService(){

        String TAG = "TimerService";
        Log.i(TAG,"Notification");
        String timerState="";
        if(!ScreenTimerActivity.mTimerRunning) timerState="Timer Stopped";
        else timerState="Timer Running";

        final String CHANNELID = "Foreground Service Id";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Notification.Builder notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this,CHANNELID)
                    .setContentText(getTimeLeftInHoursMinsSecs())//TimerActivity.timeRemainingText
                    .setContentTitle(timerState) //"Timer enabled"
                    .setSmallIcon(R.drawable.timer_icon);
        }

        assert notification != null;
        startForeground(1001,notification.build());
    }
    private String getTimeLeftInHoursMinsSecs(){
        final long hr = TimeUnit.MILLISECONDS.toHours(ScreenTimerActivity.mTimeLeftInMillis)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(ScreenTimerActivity.mTimeLeftInMillis));
        final long min = TimeUnit.MILLISECONDS.toMinutes(ScreenTimerActivity.mTimeLeftInMillis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ScreenTimerActivity.mTimeLeftInMillis));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(ScreenTimerActivity.mTimeLeftInMillis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ScreenTimerActivity.mTimeLeftInMillis));

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hr, min, sec);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}