package com.example.kidszone.services;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.kidszone.activites.TimerActivity;

import java.time.LocalDateTime;

public class CurrentTimerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TimerActivity.mCountDownTimer = new CountDownTimer(TimerActivity.mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LocalDateTime now = LocalDateTime.now();
                int hour = now.getHour();
                int minute = now.getMinute();
            }

            @Override
            public void onFinish() {
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                () -> TimerActivity.mCountDownTimer.start()
        ).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
