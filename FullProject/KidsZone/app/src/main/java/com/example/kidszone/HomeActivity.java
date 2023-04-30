package com.example.kidszone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kidszone.activites.BlockedApps;
import com.example.kidszone.activites.IntroScreen;
import com.example.kidszone.activites.TimerActivity;
import com.example.kidszone.databinding.ActivityHomeBinding;
import com.example.kidszone.deeplearningmodel.Age_prediction;
import com.example.kidszone.services.BackgroundManager;
import com.example.kidszone.services.GetBackCoreService;
import com.example.kidszone.shared.SharedPrefUtil;
import com.google.gson.Gson;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    static Age_prediction AGE_PREDICTION;
    public static String CURRENT_AGE_CLASS="";
    private static boolean IS_CAMERA_RUNNING = false;
    private static final String SAVED_IS_CAMERA_RUNNING = "IS_CAMERA_RUNNING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkAppsFirstTimeLaunch();
        setContentView(R.layout.activity_home);
        getWindow().setStatusBarColor(ContextCompat.getColor(HomeActivity.this, R.color.beige));

        if (OpenCVLoader.initDebug()) Log.d("LOADER", "SUCCESS");
        else Log.d("LOADER", "EROR");
        loadDeepLearningModels();

        if(AGE_PREDICTION != null)
            Log.d("AGE_PREDICTION ", "LOADED 2 MODELS");
        else
            Log.d("AGE_PREDICTION ", "NOT LOADED 2 MODELS");

        BackgroundManager.getInstance().init(this).startService();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        getPermission();

        binding.freezeButton.setOnClickListener(view -> openFreezeTimerActivity());

        binding.blockButton.setOnClickListener(view -> openBlockAppsActivity());

        // TODO Delete image from gallery
//        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/test.jpg");
//        file.delete();
//        imageView.setImageResource(com.example.kidszone.R.drawable.dummy);
    }

    public void onSwitchClicked(View view){
        if(binding.appSwitch.isChecked()){
            // TODO Start the Camera Service
            IS_CAMERA_RUNNING = true;
            Toast.makeText(HomeActivity.this,"Camera Is Monitoring NOW",Toast.LENGTH_LONG).show();
            if(Build.VERSION.SDK_INT >25){
                startForegroundService(new Intent(HomeActivity.this, GetBackCoreService.class));
            }else{
                startService(new Intent(HomeActivity.this, GetBackCoreService.class));
            }
        }
        else {
            // TODO Stop the Camera Service
            IS_CAMERA_RUNNING = false;
            Toast.makeText(HomeActivity.this,"Camera Stopped Monitoring",Toast.LENGTH_LONG).show();
            if(GetBackCoreService.ten_seconds_timer != null){
                GetBackCoreService.ten_seconds_timer.cancel();
                stopService(new Intent(HomeActivity.this, GetBackCoreService.class));
            }
        }
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
        overlayPermission();
        accessPermission();

    }
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
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
        // check if we already  have permission to draw over other apps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA},
                            102);
                }
            } else {
                // if not construct intent to request permission
                final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                // request permission via start activity for result
                startActivityForResult(intent, 101);
            }
        }
    }
    private void loadDeepLearningModels(){
        try {
            File yolo_file = new File(getCacheDir() + "/yolov5s-face.onnx");

            if (!yolo_file.exists())
            {
                try {
                    InputStream is = getAssets().open("yolov5s-face.onnx");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();

                    FileOutputStream fos = new FileOutputStream(yolo_file);
                    fos.write(buffer);
                    fos.close();
                }catch (Exception e) {
                    Log.i("Exception", e.getMessage());
                }
            }

            AGE_PREDICTION = new Age_prediction(yolo_file,getApplicationContext()); // Loading 2 models, App start (SAVE THIS VAR)
            // TODO SAVE AGE

        } catch (Exception e) {
            Log.i("Exception", e.getMessage());
        }
    }
    public static void getAgeFromImage(Bitmap bitmap){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        // mat.postRotate(-90); // Need to be from type matrix not Mat
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGRA2RGB);

        CURRENT_AGE_CLASS = AGE_PREDICTION.detection_prediction(mat);
    }
    private void checkAppsFirstTimeLaunch() {
        /*Intent myIntent = new Intent(MainActivity.this, IntroScreen.class);
        MainActivity.this.startActivity(myIntent);*/
        boolean secondTimePref = SharedPrefUtil.getInstance(this).getBoolean("secondRun");
        if (!secondTimePref) {
            Intent myIntent = new Intent(HomeActivity.this, IntroScreen.class);
            HomeActivity.this.startActivity(myIntent);
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

        if (requestCode == 101) {
            // if so check once again if we have permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted
                    if(ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                    }else {
                        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                102);
                    }
                }
                else {
                    Toast.makeText(this, "Permission denied by user.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        // TODO Retrieve saved the variables
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        // SWITCH BUTTON
        IS_CAMERA_RUNNING = prefs.getBoolean(SAVED_IS_CAMERA_RUNNING, false); // The second parameter is the value that puts in the 1st parameter if it is empty

        if(IS_CAMERA_RUNNING)
            binding.appSwitch.setChecked(true);
        else
            binding.appSwitch.setChecked(false);


        // AGE PREDICTION
        Gson gson = new Gson();
        String json = prefs.getString("AGE_PREDICTION", "");
        AGE_PREDICTION = gson.fromJson(json, Age_prediction.class);

        if(AGE_PREDICTION != null)
            Log.d("Retrieving AGE_PREDICTION WITH SharedPreferences--> ", "AGE_PREDICTION IS NOT NULL");
        else
            Log.d("Retrieving AGE_PREDICTION WITH SharedPreferences--> ", "AGE_PREDICTION IS NULL");
    }
    @Override
    protected void onStop() {
        super.onStop();

        // TODO Save Variables
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // SWITCH BUTTON
        editor.putBoolean(SAVED_IS_CAMERA_RUNNING, IS_CAMERA_RUNNING);
        editor.apply();

        // AGE PREDICTION
        Gson gson = new Gson();
        String json = gson.toJson(AGE_PREDICTION);
        editor.putString("AGE_PREDICTION", json);
        editor.apply();

        Log.d("SAVED with SharedPreferences--> ", "AGE_PREDICTION");

    }
}