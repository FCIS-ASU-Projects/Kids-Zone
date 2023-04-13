package com.example.floatingwindow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.floatingwindow.databinding.ActivityMainBinding;
import com.example.floatingwindow.databinding.FreezePageBinding;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FreezeTimerActivity extends AppCompatActivity {

    FreezePageBinding binding;
    public static int hour,minute;
    public static long milliSeconds,seconds;
    public static boolean onThisLayout=true;
    private static long START_TIME_IN_MILLIS = 30*60000;
    public static final String SAVED_START_TIME_IN_MILLIS = "START_TIME_IN_MILLIS";
    public static long mTimeLeftInMillis;
    public static boolean mTimerRunning;
    public static long mEndTime; // Used to prevent the lag that happens in the timer while rotating the app or close and open it again
    public static CountDownTimer mCountDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freeze_page);

        // TODO Choose the time the user wants to freeze the mobile after
        binding = FreezePageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        onThisLayout=true;

        binding.textViewTime.setText(String.format(Locale.getDefault(),"%02d:%02d:%02d",hour,minute,seconds));
        binding.setTimerButton.setOnClickListener(pickTimeButtonClick);


        // TODO Start counting down till it reaches 00:00:00

        binding.startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        binding.resetTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    private View.OnClickListener pickTimeButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            timePicker(v);
        }
    };

    public void timePicker(View view)
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                seconds = 0;
                milliSeconds = seconds*1000;
                START_TIME_IN_MILLIS = (hour*60*60000 + minute*60000);
                resetTimer();
                binding.textViewTime.setText(String.format(Locale.getDefault(),"%02d:%02d:%02d",hour,minute,seconds));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,onTimeSetListener,hour,minute,true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateButtons();
            }
        }.start();

        mTimerRunning = true;
        updateButtons();
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateButtons();
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        updateButtons();
    }

    private void updateCountDownText() {

        final long hr = TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(mTimeLeftInMillis));
        final long min = TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis));

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hr, min, sec);

        binding.textViewTime.setText(timeLeftFormatted);
    }

    private void updateButtons() {
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

    // TODO the following 2 functions (onStop&onStart) prevents
    //  restarting the app (the timer) while rotating it or opening and closing it.
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(SAVED_START_TIME_IN_MILLIS, START_TIME_IN_MILLIS);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);


        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        START_TIME_IN_MILLIS = prefs.getLong(SAVED_START_TIME_IN_MILLIS, 30*60000); // The second parameter is the value that puts in the 1st parameter if it is empty
        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateButtons();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateButtons();
            } else {
                startTimer();
            }
        }
    }

}