package com.example.kidszone_test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.kidszone_test.databinding.ActivityMainBinding;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    public static int hour,minute;
    public static long milliSeconds,seconds;

    public static boolean onThisLayout=true;
    public static String PREF_NAME = "myPrefFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        /*SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREF_NAME,0);
        sharedPreferences.edit().putBoolean("isMainActivity",true).commit();*/

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        onThisLayout=true;
        binding.textViewTime.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
        binding.buttonPickTime.setOnClickListener(pickTimeButtonClick);
        binding.buttonStart.setOnClickListener(startButtonClick);
    }
    private View.OnClickListener pickTimeButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            timePicker(v);
        }
    };
    private View.OnClickListener startButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            onThisLayout=false;
            Timer.onThisLayout=true;

            Intent intent = new Intent(MainActivity.this,Timer.class);
            intent.putExtra("milliSeconds",milliSeconds);
            startActivity(intent);
            finish();
        }
    };
    public void timePicker(View view)
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                seconds=(hour*60*60)+(minute*60);
                milliSeconds = seconds*1000;
                binding.textViewTime.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,onTimeSetListener,hour,minute,true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

}