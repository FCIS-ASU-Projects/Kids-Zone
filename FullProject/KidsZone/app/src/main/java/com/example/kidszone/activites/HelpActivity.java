package com.example.kidszone.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import com.example.kidszone.HomeActivity;
import com.example.kidszone.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(HelpActivity.this, R.color.black));
        setContentView(R.layout.activity_help);
    }
}