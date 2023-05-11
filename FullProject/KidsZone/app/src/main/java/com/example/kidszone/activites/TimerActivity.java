package com.example.kidszone.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kidszone.HomeActivity;
import com.example.kidszone.R;
import com.example.kidszone.databinding.ActivityFreezeBinding;
import com.example.kidszone.services.LockScreenService;
import com.example.kidszone.services.TimerService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static ActivityFreezeBinding binding;
    public static int hour,minute;
    public static long milliSeconds,seconds;
    public static long START_TIME_IN_MILLIS = 30*60000;
    public static final String SAVED_START_TIME_IN_MILLIS = "START_TIME_IN_MILLIS";
    public static long mTimeLeftInMillis = 0;
    public static boolean mTimerRunning;
    public static CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeze);
        getWindow().setStatusBarColor(ContextCompat.getColor(TimerActivity.this, R.color.beige));

        binding = ActivityFreezeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String edit_text= "Edit";
        SpannableString spannableString = new SpannableString(edit_text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                // TODO edit the time threshold
            }
        };
        spannableString.setSpan(clickableSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.editResetThreshold.setText(spannableString);
        binding.editResetThreshold.setMovementMethod(LinkMovementMethod.getInstance());

        binding.setTimerButton.setOnClickListener(view1 -> timePicker());

        binding.startTimerButton.setOnClickListener(v -> {
            if (mTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        binding.resetTimerButton.setOnClickListener(v -> resetTimer());
    }
    public void timePicker(){
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
        startService(new Intent(TimerActivity.this, TimerService.class));
    }
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;

        if(binding==null)
            binding = ActivityFreezeBinding.inflate(getLayoutInflater());
        updateButtons();

        stopService(new Intent(TimerActivity.this, TimerService.class));
    }
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;

        if(binding==null)
            binding = ActivityFreezeBinding.inflate(getLayoutInflater());
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
        }
        else {
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

        // TODO Save Variables
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(SAVED_START_TIME_IN_MILLIS, START_TIME_IN_MILLIS);

        editor.apply();
    }
    @Override
    protected void onStart() {
        super.onStart();

        // TODO Retrieve saved the variables
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        START_TIME_IN_MILLIS = prefs.getLong(SAVED_START_TIME_IN_MILLIS, 30*60000); // The second parameter is the value that puts in the 1st parameter if it is empty

        // THIS IS USED AT FIRST TIME THE APP LAUNCH THIS ACTIVITY
        if(mTimeLeftInMillis==0 && !HomeActivity.IS_TIMER_FOR_TODAY_FINISHED)
            mTimeLeftInMillis = START_TIME_IN_MILLIS;

        updateCountDownText();
        updateButtons();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}