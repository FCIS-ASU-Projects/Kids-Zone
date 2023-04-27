package com.example.kidszone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.example.kidszone.activites.BlockedApps;
import com.example.kidszone.activites.IntroScreen;
import com.example.kidszone.activites.TimerActivity;
import com.example.kidszone.databinding.ActivityHomeBinding;
import com.example.kidszone.services.BackgroundManager;
import com.example.kidszone.shared.SharedPrefUtil;

public class MainActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         BackgroundManager.getInstance().init(this).startService();

        checkAppsFirstTimeLaunch();

        setContentView(R.layout.activity_home);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        getPermission();

        binding.freezeButton.setOnClickListener(view -> openFreezeTimerActivity());

        binding.blockButton.setOnClickListener(view -> openBlockAppsActivity());
//        Button buttonAddWidget;
//        buttonAddWidget = (Button) findViewById(R.id.button_widget);
//        getPermission();
//
//        buttonAddWidget.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!Settings.canDrawOverlays(MainActivity.this))
//                {
//                    getPermission();
//                }
//                else
//                {
//                    Intent intent = new Intent(MainActivity.this, WidgetService.class);
//                    startService(intent);
//                    finish();
//                }
//            }
//        });
    }

    public void getPermission(){
        //check for alert window permission
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
//        {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
//            startActivityForResult(intent, 1);
//        }

        // ZAIN CODE
//        if(!Settings.canDrawOverlays(this))
//        {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
//            startActivityForResult(intent, 1);
//        }

        // HEBA PERMISSIONS
        if(!Settings.canDrawOverlays(this) || !isAccessGranted())
        {
            overlayPermission();
            accessPermission();
        }

    }
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            }
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public void accessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isAccessGranted()) {
                //Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intent, 102);
            }
        }
    }
    public void overlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(myIntent, 101);
            }
        }
    }
    private void checkAppsFirstTimeLaunch() {
        /*Intent myIntent = new Intent(MainActivity.this, IntroScreen.class);
        MainActivity.this.startActivity(myIntent);*/
        boolean secondTimePref = SharedPrefUtil.getInstance(this).getBoolean("secondRun");
        if (!secondTimePref) {
            Intent myIntent = new Intent(MainActivity.this, IntroScreen.class);
            MainActivity.this.startActivity(myIntent);
            SharedPrefUtil.getInstance(this).putBoolean("secondRun", true);
        }
    }

    public void openFreezeTimerActivity(){
        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);
    }

    public void openBlockAppsActivity(){
        Intent intent = new Intent(this, BlockedApps.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            if(!Settings.canDrawOverlays(MainActivity.this))
            {
                Toast.makeText(this, "Permission denied by user.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}