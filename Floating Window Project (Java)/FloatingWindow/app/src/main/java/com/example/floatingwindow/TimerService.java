package com.example.floatingwindow;

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

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}
    @Override
    public void onCreate() {
        super.onCreate();

//        TimerActivity.mEndTime = System.currentTimeMillis() + TimerActivity.mTimeLeftInMillis;

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
            }
        };
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        TimerActivity.mCountDownTimer.start();
//                    }
//                }
//
//        ).start();
//        TimerActivity.mTimerRunning = true;
//        TimerActivity.updateButtons();
///////////////////////////////////////////////////////////////////
//        if(TimerActivity.mTimerRunning)
//            countDown();
//        else
//            TimerActivity.mCountDownTimer.cancel();

///////////////////////////////////////////////////////////////////

//        TimerActivity.mEndTime = System.currentTimeMillis() + TimerActivity.mTimeLeftInMillis;

//        TimerActivity.mTimerRunning = true;
//        TimerActivity.updateButtons();
        /////////////////////////////////////////////////////////////////////////////////////////////////
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
                    .setSmallIcon(R.drawable.timericon);
        }

        assert notification != null;
        startForeground(1001,notification.build());
//        timeRemainingText=Timer.timeRemainingText;
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
//        TimerActivity.mCountDownTimer.cancel();
//        TimerActivity.mTimerRunning = false;
//        TimerActivity.updateButtons();
        Log.d("TimerService", "Called onDestroy Method");
        Toast.makeText(getApplicationContext(), "TimerService is Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}

//package com.example.floatingwindow;
//
//        import android.app.Service;
//        import android.content.Intent;
//        import android.os.CountDownTimer;
//        import android.os.IBinder;
//        import android.util.Log;
//        import android.widget.Toast;
//
//        import androidx.annotation.Nullable;
//
//        import java.util.concurrent.TimeUnit;
//
//public class TimerService extends Service {
//
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        FreezeTimerActivity.mEndTime = System.currentTimeMillis() + FreezeTimerActivity.mTimeLeftInMillis;
//
//        FreezeTimerActivity.mCountDownTimer = new CountDownTimer(FreezeTimerActivity.mTimeLeftInMillis, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                FreezeTimerActivity.mTimeLeftInMillis = millisUntilFinished;
////                FreezeTimerActivity.updateCountDownText();
//                Log.d("TimerService", "DECREASED SECOND");
//            }
//
//            @Override
//            public void onFinish() {
//                FreezeTimerActivity.mTimerRunning = false;
////                FreezeTimerActivity.updateButtons();
//                Toast.makeText(getApplicationContext(), "TIMER IS FINISHED", Toast.LENGTH_LONG).show();
//            }
//        }.start();
//
//        FreezeTimerActivity.mTimerRunning = true;
////        FreezeTimerActivity.updateButtons();
//
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Toast.makeText(getApplicationContext(), "TimerService is Destroyed", Toast.LENGTH_LONG).show();
//    }
//}
//
