package com.example.kidszone_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class TRASH_HomeActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        new Handler().postDelayed(()->{

            SharedPreferences sharedPreferences=getSharedPreferences(MainActivity.PREF_NAME, 0);
            boolean isMainActivity =sharedPreferences.getBoolean("isMainActivity",true);

            if(isMainActivity){
                Intent intent = new Intent(TRASH_HomeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Intent intent = new Intent(TRASH_HomeActivity.this,Timer.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }

        },1000);
    }
}
