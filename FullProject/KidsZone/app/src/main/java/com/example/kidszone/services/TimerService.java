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
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.kidszone.R;
import com.example.kidszone.activites.TimerActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}
    @Override
    public void onCreate() {
        super.onCreate();

        TimerActivity.mCountDownTimer = new CountDownTimer(TimerActivity.mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                notificationService();
                TimerActivity.mTimeLeftInMillis = millisUntilFinished;
                TimerActivity.updateCountDownText();
                Log.d("TimerService: Info", "DECREASED SECOND");
                Log.d("TimerService: TimerActivity.mTimeLeftInMillis", Long.toString(TimerActivity.mTimeLeftInMillis));
            }

            @Override
            public void onFinish() {
                TimerActivity.mTimerRunning = false;
                TimerActivity.updateButtons();
                notificationService();
                Toast.makeText(getApplicationContext(), "TIMER IS FINISHED", Toast.LENGTH_LONG).show();

                // TODO Freeze the mobile
                startService(new Intent(getApplicationContext(), FreezeService.class));
            }
        };
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        TimerActivity.mEndTime = System.currentTimeMillis() + TimerActivity.mTimeLeftInMillis;

        new Thread(
                () -> TimerActivity.mCountDownTimer.start()
        ).start();

        TimerActivity.mTimerRunning = true;
        TimerActivity.updateButtons();

        return START_STICKY;
    }
    public void notificationService(){

        String TAG = "TimerService";
        Log.i(TAG,"Notification");
        String timerState="";
        if(!TimerActivity.mTimerRunning) timerState="Timer Stopped";
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
        final long hr = TimeUnit.MILLISECONDS.toHours(TimerActivity.mTimeLeftInMillis)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(TimerActivity.mTimeLeftInMillis));
        final long min = TimeUnit.MILLISECONDS.toMinutes(TimerActivity.mTimeLeftInMillis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(TimerActivity.mTimeLeftInMillis));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(TimerActivity.mTimeLeftInMillis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(TimerActivity.mTimeLeftInMillis));

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hr, min, sec);
    }
    @Override
    public void onDestroy() {
        Log.d("TimerService", "Called onDestroy Method");
        Toast.makeText(getApplicationContext(), "TimerService is Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}