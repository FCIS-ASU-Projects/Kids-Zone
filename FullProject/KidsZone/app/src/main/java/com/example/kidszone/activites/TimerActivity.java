package com.example.kidszone.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.example.kidszone.R;
import com.example.kidszone.databinding.ActivityFreezeBinding;
import com.example.kidszone.services.TimerService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    static ActivityFreezeBinding binding;
    public static int hour,minute;
    public static long milliSeconds,seconds;
    private static long START_TIME_IN_MILLIS = 30*60000;
    public static final String SAVED_START_TIME_IN_MILLIS = "START_TIME_IN_MILLIS";
    public static long mTimeLeftInMillis;
    public static boolean mTimerRunning;
//    public static long mEndTime; // Used to prevent the lag that happens in the timer while rotating the app or close and open it again
    public static CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeze);

        binding = ActivityFreezeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.setTimerButton.setOnClickListener(pickTimeButtonClick);

        binding.startTimerButton.setOnClickListener(v -> {
            if (mTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        binding.resetTimerButton.setOnClickListener(v -> resetTimer());
    }
    private final View.OnClickListener pickTimeButtonClick = v -> {
        // TODO Auto-generated method stub
        timePicker(v);
    };

    public void timePicker(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view1, selectedHour, selectedMinute) -> {
            hour = selectedHour;
            minute = selectedMinute;
            seconds = 0;
            milliSeconds = 0; //milliSeconds = seconds*1000
            START_TIME_IN_MILLIS = ((long)hour *60*60000 + (long)minute*60000);
            resetTimer();
            binding.textViewTime.setText(String.format(Locale.getDefault(),"%02d:%02d:%02d",hour,minute,seconds));
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,onTimeSetListener,hour,minute,true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void startTimer() {

//        Intent timerServiceIntent = new Intent(TimerActivity.this, TimerService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(timerServiceIntent);
//        }
//        Toast.makeText(getApplicationContext(), "START SERVICE", Toast.LENGTH_LONG).show();
//        startService(timerServiceIntent);

        Toast.makeText(getApplicationContext(), "START SERVICE", Toast.LENGTH_LONG).show();
        startService(new Intent(TimerActivity.this, TimerService.class));

//        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

//        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                mTimeLeftInMillis = millisUntilFinished;
//                updateCountDownText();
//            }
//
//            @Override
//            public void onFinish() {
//                mTimerRunning = false;
//                updateButtons();
//            }
//        }.start();

//        mTimerRunning = true;
//        updateButtons();
    }

    private void pauseTimer() {

        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateButtons();

        stopService(new Intent(TimerActivity.this, TimerService.class));
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        updateButtons();
    }

    public static void updateCountDownText() {

        final long hr = TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(mTimeLeftInMillis));
        final long min = TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis));

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hr, min, sec);

        binding.textViewTime.setText(timeLeftFormatted);
    }

    @SuppressLint("SetTextI18n")
    public static void updateButtons() {
        if (mTimerRunning) {
            binding.resetTimerButton.setVisibility(View.INVISIBLE);
            binding.startTimerButton.setText("Pause");
        } else {
            binding.startTimerButton.setText("Start");

            if (mTimeLeftInMillis < 1000) {
                binding.startTimerButton.setVisibility(View.INVISIBLE);
            } else {
                binding.startTimerButton.setVisibility(View.VISIBLE);
            }

            if (mTimeLeftInMillis < START_TIME_IN_MILLIS) {
                binding.resetTimerButton.setVisibility(View.VISIBLE);
            } else {
                binding.resetTimerButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save Variables
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(SAVED_START_TIME_IN_MILLIS, START_TIME_IN_MILLIS);

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Save the variables
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        START_TIME_IN_MILLIS = prefs.getLong(SAVED_START_TIME_IN_MILLIS, 30*60000); // The second parameter is the value that puts in the 1st parameter if it is empty

        if(mTimeLeftInMillis==0)
            mTimeLeftInMillis = START_TIME_IN_MILLIS;

        if (mTimerRunning) {
//            mEndTime = prefs.getLong("endTime", 0);
//            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
            }
            updateCountDownText();
            updateButtons();
        }

        else{
            updateCountDownText();
            updateButtons();
        }
    }
}