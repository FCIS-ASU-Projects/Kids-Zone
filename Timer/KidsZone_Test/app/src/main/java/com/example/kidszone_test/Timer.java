package com.example.kidszone_test;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kidszone_test.databinding.ActivityCountdowntimerBinding;
import com.example.kidszone_test.databinding.ActivityMainBinding;

public class Timer extends AppCompatActivity {

    ActivityCountdowntimerBinding binding;

    String TAG = "Timer";
    int test=1;

    public static boolean onThisLayout;

    public static String timeRemainingText= "";
    public static boolean isStillRunning=false;

    public static int valueOfProgressBar;
    private int minutesCount,secondsCount;
    private int mainHours,mainMinutes;
    private long allSecondsCount,milliSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        valueOfProgressBar=100;
        minutesCount=0;
        secondsCount=0;
        mainHours=MainActivity.hour;
        mainMinutes=MainActivity.minute;
        allSecondsCount=MainActivity.seconds;
        milliSeconds=MainActivity.milliSeconds;
        Log.i(TAG,"milliSeconds = "+milliSeconds);

        // Using binding instead of findById
        binding = ActivityCountdowntimerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.buttonCancel.setOnClickListener(cancelButtonClick);

        Intent intent = new Intent(this,TimerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }

        Log.i(TAG,"Started Service");
    }

    private View.OnClickListener cancelButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            onThisLayout=false;
            MainActivity.onThisLayout=true;

            unregisterReceiver(broadcastReceiver);
            TimerService.counterCanceled=true;
            TimerService.countDownTimer.onFinish();
            finish();

            Intent intent = new Intent(Timer.this,MainActivity.class);
            startActivity(intent);

        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.COUNTDOWN_TS));
        Log.i(TAG,"Registered broadcast receiver");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateGUI(Intent intent){

        if(intent.getExtras()!=null){

            long millisUntilFinished = intent.getLongExtra("countdown",milliSeconds);

            Log.i(TAG,"Countdown seconds remaining : "+(milliSeconds/1000)+"  secondsCount = "+secondsCount);

            updateValues();

            timeRemainingText= "";

            if(mainHours<10) timeRemainingText+="0";
            timeRemainingText+=mainHours;
            timeRemainingText+=":";

            if(mainMinutes<10) timeRemainingText+="0";
            timeRemainingText+=mainMinutes;

            TimerService.timeRemainingText=timeRemainingText;
            binding.textViewTimer.setText(timeRemainingText);

            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
            sharedPreferences.edit().putLong("time",milliSeconds).apply(); //millisUntilFinished

            /*SharedPreferences sharedPreferences2 = getSharedPreferences(MainActivity.PREF_NAME,0);
            sharedPreferences2.edit().putBoolean("isMainActivity",false).commit();*/
        }
    }

    private void updateValues(){

        if(valueOfProgressBar==100){
            mainHours=MainActivity.hour;
            mainMinutes=MainActivity.minute;
            secondsCount=0;
            minutesCount=0;
            allSecondsCount=MainActivity.seconds;
        }

        binding.progressBar2.setProgress(valueOfProgressBar);

        Log.i(TAG,"progress bar :   "+valueOfProgressBar+"  percent = ");

        if(secondsCount==59){
            mainMinutes--;
            minutesCount++;
            secondsCount=0;
        }

        if(minutesCount==59){
            mainHours--;
            minutesCount=0;
        }

        secondsCount++;
        allSecondsCount--;
        valueOfProgressBar=(int)(((double)allSecondsCount/(double)MainActivity.seconds)*100);
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(TimerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
