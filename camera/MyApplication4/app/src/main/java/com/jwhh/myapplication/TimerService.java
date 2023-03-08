package com.jwhh.myapplication;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.security.Provider;
import java.util.Calendar;
import java.util.List;

public class TimerService extends Service {

    private String TAG = "TimerService";
    public static final String COUNTDOWN_TS = "com.example.kidszone";
    Intent intent = new Intent(COUNTDOWN_TS);
    IntentFilter filter;
    BroadcastReceiver mReceiver;

    public static CountDownTimer countDownTimer = null;
    public static long milliSeconds=MainActivity.milliSeconds;
    int checkCurrentTimeHours,checkCurrentTimeMinutes;

    boolean restart=false;
    boolean AlarmStopped=false;
    public static boolean counterCanceled=false;

    public static String timeRemainingText="00:00";

    SharedPreferences sharedPreferences;

    PendingIntent pi;
    AlarmManager am;


    @Override
    public void onCreate() {
        super.onCreate();

        AlarmStopped=true;
        Log.i(TAG,"Starting timer...");
        counterCanceled=false;
        sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        long millis = sharedPreferences.getLong("time", milliSeconds);
        if(millis/1000==0){
            millis+=1000;
        }

        countDownTimer=createCounter(MainActivity.milliSeconds);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG,"Server on start command");

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        //countDownTimer=createCounter(MainActivity.milliSeconds);
                        countDownTimer.start();
                    }
                }

        ).start();

        filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new LockScreenReceiver();
        registerReceiver(mReceiver, filter);

        setAlarm(0);

        notificitonService();


        return super.START_STICKY;
    }
    @Override
    public void onDestroy() {
        counterCanceled=true;
        countDownTimer.cancel();
        //unregisterReceiver(mReceiver,filter); error
        super.onDestroy();
    }

    public CountDownTimer createCounter(long milliSeconds){

        countDownTimer = new CountDownTimer(milliSeconds+1000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                notificitonService();

                //Log.i("Timer Server "," Calender time hours: "+checkCurrentTimeHours+"  minutes: "+checkCurrentTimeMinutes);

                //counterCanceled=false;
                Log.i(TAG,"Countdown seconds remaining:"+millisUntilFinished);

                getCurrentTime();
                if(restart&&MyReciever.alarmReceived)
                {
                    restart=false;
                    MyReciever.alarmReceived=false;
                }

                Log.i(TAG,"sendBroadcast");
                intent.putExtra("countdown",millisUntilFinished);
                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {

                if(MyReciever.alarmReceived) {
                    Log.i(TAG,"Alarm");
                    counterCanceled=false;
                    MyReciever.alarmReceived=false;
                    //Timer.valueOfProgressBar=100;
                    restart=true;
                    setRestart(milliSeconds);
                }

                else if(counterCanceled){
                    Log.i(TAG,"Timer Canceled");
                    counterCanceled=false;
                    restart=true;
                    AlarmStopped=true;
                    am.cancel(pi);
                    unregisterReceiver(mReceiver);
                    stopSelf();
                }
                else{
                    counterCanceled=true;
                    notificitonService();

                    Log.i(TAG,"Time Limit Finished");

                    Toast.makeText(getApplicationContext(), "Your daily time finished", Toast.LENGTH_LONG).show();
                }

            }
        };
        return countDownTimer;
    }

    public void notificitonService(){

        Log.i(TAG,"Notification");
        String timerState="";
        if(counterCanceled) timerState="Timer Stopped";
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this,CHANNELID)
                    .setContentText(Timer.timeRemainingText)
                    .setContentTitle(timerState) //"Timer enabled"
                    .setSmallIcon(R.drawable.timericon);
        }


        startForeground(1001,notification.build());
        timeRemainingText=Timer.timeRemainingText;
    }

    public void getCurrentTime(){

        Calendar cal = Calendar.getInstance();

        checkCurrentTimeMinutes = cal.get(Calendar.MINUTE);

        // 12-hour format
        int hour = cal.get(Calendar.HOUR);

        // 24-hour format
        checkCurrentTimeHours = cal.get(Calendar.HOUR_OF_DAY);
    }

    public void setRestart(long milliSeconds){
        Log.i(TAG,"i am here in restart");
        countDownTimer.cancel();
        countDownTimer=createCounter(milliSeconds);
        countDownTimer.start();
    }

    public void setAlarm(int Receiver){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // Set to 12:00
        cal.set(Calendar.MINUTE, 0);
        long tomorrow = cal.getTimeInMillis();
        long now = System.currentTimeMillis();

        if(cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 7);
        }

        am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyReciever.class);

        intent.putExtra("Alarm", Boolean.TRUE);

        pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        long intervalMillis = tomorrow - now;
        //am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi); //24*60*60*1000
        /*am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
                AlarmManager.INTERVAL_DAY, pi);*/ //s7777

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                intervalMillis,
                AlarmManager.INTERVAL_DAY, pi);

        /*am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                now,
                3000, pi);*/



    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
