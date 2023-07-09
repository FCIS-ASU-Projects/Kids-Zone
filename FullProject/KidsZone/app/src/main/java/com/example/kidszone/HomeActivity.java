package com.example.kidszone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Dialog;
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
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kidszone.activites.UnblockedApps;
import com.example.kidszone.activites.HelpActivity;
import com.example.kidszone.activites.IntroScreen;
import com.example.kidszone.activites.ScreenTimerActivity;
import com.example.kidszone.databinding.ActivityHomeBinding;
import com.example.kidszone.deeplearningmodel.Age_prediction;
import com.example.kidszone.apps_blocking_background_manager.BackgroundManager;
import com.example.kidszone.services.GetBackCoreService;
import com.example.kidszone.services.LockScreenService;
import com.example.kidszone.shared.SharedPrefUtil;
import com.google.gson.Gson;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static ActivityHomeBinding binding;
    public static List<ApplicationInfo> ALL_MOBILE_APPS;
    private static Age_prediction AGE_PREDICTION;
    public static int IMAGE_CURRENT_AGE_CLASS = -1;
    public static String AGE_TO_BE_BLOCKED_FOR ="2-->6";
    private static final String SAVED_AGE_TO_BE_BLOCKED_FOR ="SAVED_AGE_TO_BE_BLOCKED_FOR";
    public static int TIMER_RESTARTS_AT = 0;
    private static final String SAVED_SWITCH_STATE ="SAVED_SWITCH_STATE";
    private static boolean SWITCH_STATE = false;
    public static boolean IS_CAMERA_RUNNING = false;
    public static boolean IS_BLOCK_ON = false;
    public static boolean IS_FREEZE_ON = false;
    public static boolean IS_TIMER_FOR_TODAY_FINISHED = false;
    private final String[] ages_classes = {"2-->6", "2-->14"};
    public static Dictionary<String, Integer>classFromAge;
    @SuppressLint("StaticFieldLeak")
    static Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this.getApplicationContext();
        getPermission();
        getWindow().setStatusBarColor(ContextCompat.getColor(HomeActivity.this, R.color.black));
        setContentView(R.layout.activity_home);

        checkAppsFirstTimeLaunch();
        setAgeFromClassDict();

        ALL_MOBILE_APPS = ctx.getPackageManager().getInstalledApplications(0);

        startServices();

        if (OpenCVLoader.initDebug()) Log.d("LOADER", "SUCCESS");
        else Log.d("LOADER", "ERROR");
        loadDeepLearningModels();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        binding.freezeButton.setOnClickListener(view -> openFreezeTimerActivity());
        binding.blockButton.setOnClickListener(view -> openBlockAppsActivity());
        binding.specifyAgeButton.setOnClickListener(view -> showAgeCustomDialog());
        binding.appBar.helpIcon.setOnClickListener(view -> openHelpActivity());
    }
    private void setAgeFromClassDict(){
        classFromAge = new Hashtable<>();
        classFromAge.put("2-->6", 1);
        classFromAge.put("2-->14", 2);
        classFromAge.put("+15",3);
        classFromAge.put("NOTHING", -1);
    }
    private void startServices(){
        startService(new Intent(HomeActivity.this, LockScreenService.class));
        BackgroundManager.getInstance().init(this).startService();
    }
    @SuppressLint("SetTextI18n")
    private void showAgeCustomDialog(){
        final Dialog dialog = new Dialog(HomeActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.select_age_dialog);

        //Initializing the views of the dialog.
        TextView selected_age_txtView = dialog.findViewById(R.id.selected_age);
        selected_age_txtView.setText("Current Age is "+AGE_TO_BE_BLOCKED_FOR);

        AutoCompleteTextView autoCompleteTxt = dialog.findViewById(R.id.auto_complete_txt);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.my_list_item, ages_classes);
        autoCompleteTxt.setAdapter(arrayAdapter);
        final String[] selected_age = {""};
        autoCompleteTxt.setOnItemClickListener((adapterView, view, i, l) -> selected_age[0] = adapterView.getItemAtPosition(i).toString());


        Button submitButton = dialog.findViewById(R.id.submit_age_button);
        submitButton.setOnClickListener(v -> {
            AGE_TO_BE_BLOCKED_FOR = selected_age[0];
            selected_age_txtView.setText("Current Age is "+ AGE_TO_BE_BLOCKED_FOR);
            dialog.dismiss();
        });

        dialog.show();
    }
    public void onSwitchClicked(View view){

        if(binding == null)
            binding = ActivityHomeBinding.inflate(getLayoutInflater());

        if(binding.appSwitch.isChecked()){
            // TODO Start the Camera Service
            IS_CAMERA_RUNNING = true;
            SWITCH_STATE = true;
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
            IMAGE_CURRENT_AGE_CLASS = -1;
            SWITCH_STATE = false;
            Toast.makeText(HomeActivity.this,"Camera Stopped Monitoring",Toast.LENGTH_LONG).show();
            if(GetBackCoreService.ten_seconds_timer != null){
                GetBackCoreService.ten_seconds_timer.cancel();
                stopService(new Intent(HomeActivity.this, GetBackCoreService.class));
            }
        }

    }
    public void getPermission(){
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
    public void loadDeepLearningModels(){
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
            Log.d("SAVING MODELS", "SUCCESS");
        } catch (Exception e) {
            Log.i("Exception", e.getMessage());
        }
    }
    public static void getAgeFromImage(Bitmap bitmap){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Core.rotate(mat, mat, -90);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGRA2RGB);

        IMAGE_CURRENT_AGE_CLASS = AGE_PREDICTION.detection_prediction(mat);
    }
    private void checkAppsFirstTimeLaunch() {
        boolean secondTimePref = SharedPrefUtil.getInstance(this).getBoolean("secondRun");
        if (!secondTimePref) {
            Intent myIntent = new Intent(HomeActivity.this, IntroScreen.class);
            HomeActivity.this.startActivity(myIntent);
            SharedPrefUtil.getInstance(this).putBoolean("secondRun", true);
        }
    }
    public void openFreezeTimerActivity(){
        Intent intent = new Intent(this, ScreenTimerActivity.class);
        startActivity(intent);
    }
    public void openHelpActivity(){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
    public void openBlockAppsActivity(){
        Intent intent = new Intent(this, UnblockedApps.class);
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

        // RETRIEVE DATA
        AGE_TO_BE_BLOCKED_FOR = prefs.getString(SAVED_AGE_TO_BE_BLOCKED_FOR, "2-->6"); // The second parameter is the value that puts in the 1st parameter if it is empty
        SWITCH_STATE = prefs.getBoolean(SAVED_SWITCH_STATE, false); // The second parameter is the value that puts in the 1st parameter if it is empty

        // SWITCH BUTTON
        binding.appSwitch.setChecked(SWITCH_STATE);

        // AGE PREDICTION
        if(AGE_PREDICTION == null)
        {
            Gson gson = new Gson();
            String json = prefs.getString("AGE_PREDICTION", "");
            AGE_PREDICTION = gson.fromJson(json, Age_prediction.class);
            Log.d("Retrieving AGE_PREDICTION WITH SharedPreferences--> ", "AGE_PREDICTION IS NOT NULL");
        }
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
        editor.putString(SAVED_AGE_TO_BE_BLOCKED_FOR, AGE_TO_BE_BLOCKED_FOR);
        editor.putBoolean(SAVED_SWITCH_STATE, SWITCH_STATE);

        // AGE PREDICTION
        Gson gson = new Gson();
        String json = gson.toJson(AGE_PREDICTION);
        editor.putString("AGE_PREDICTION", json);

        editor.apply();
        Log.d("SAVED with SharedPreferences--> ", "AGE_PREDICTION");
    }
}