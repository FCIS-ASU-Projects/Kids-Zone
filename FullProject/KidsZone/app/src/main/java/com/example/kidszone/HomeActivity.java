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

import com.example.kidszone.activites.BlockedApps;
import com.example.kidszone.activites.IntroScreen;
import com.example.kidszone.activites.TimerActivity;
import com.example.kidszone.databinding.ActivityHomeBinding;
import com.example.kidszone.deeplearningmodel.Age_prediction;
import com.example.kidszone.services.BackgroundManager;
import com.example.kidszone.services.GetBackCoreService;
import com.example.kidszone.services.LockScreenService;
import com.example.kidszone.shared.SharedPrefUtil;
import com.google.gson.Gson;
//import com.google.android.gms.drive.Drive;
//import com.google.android.gms.drive.Metadata;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.games.Games;
//import com.google.android.gms.games.GamesMetadata;
//import com.google.android.gms.games.GamesMetadata.LoadGamesResult;

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

public class HomeActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static ActivityHomeBinding binding;
    public static Age_prediction AGE_PREDICTION;
    public static int IMAGE_CURRENT_AGE_CLASS =6;
    public static String AGE_TO_BE_BLOCKED_FOR ="-13";
    public static String SAVED_AGE_TO_BE_BLOCKED_FOR ="SAVED_AGE_TO_BE_BLOCKED_FOR";
    public static String SAVED_SWITCH_STATE ="SAVED_SWITCH_STATE";
    public static boolean SWITCH_STATE = false;
    public static boolean IS_CAMERA_RUNNING = false;
    private static final String SAVED_IS_CAMERA_RUNNING = "IS_CAMERA_RUNNING";
    public static boolean IS_BLOCK_ON = false;
    private final String[] ages_classes = {"-3","-6","-13","-19"};
    public static Dictionary<String, Integer> classFromAge;
    @SuppressLint("StaticFieldLeak")
    static Context ctx;

    //    static List<AppModel> lockedAppsModel =new ArrayList<>();
//    static List<AppModel> allApps=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this.getApplicationContext();
        checkAppsFirstTimeLaunch();
        getPermission();
        startServices();
        setAgeFromClassDict();
//        getInstallApps();

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
//                .build();
//        mGoogleApiClient.connect();
        getWindow().setStatusBarColor(ContextCompat.getColor(HomeActivity.this, R.color.beige));
        setContentView(R.layout.activity_home);

        if (OpenCVLoader.initDebug()) Log.d("LOADER", "SUCCESS");
        else Log.d("LOADER", "ERROR");
        loadDeepLearningModels();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        binding.freezeButton.setOnClickListener(view -> openFreezeTimerActivity());
        binding.blockButton.setOnClickListener(view -> openBlockAppsActivity());
        binding.specifyAgeButton.setOnClickListener(view -> showAgeCustomDialog());

        if(AGE_PREDICTION != null)
            Log.d("AGE_PREDICTION ", "LOADED 2 MODELS");
        else
            Log.d("AGE_PREDICTION ", "NOT LOADED 2 MODELS");

        // TODO Delete image from gallery
//        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/test.jpg");
//        file.delete();
//        imageView.setImageResource(com.example.kidszone.R.drawable.dummy);
    }
    private void setAgeFromClassDict(){
        classFromAge = new Hashtable<>();
        classFromAge.put("-3", 0);
        classFromAge.put("-6", 1);
        classFromAge.put("-13",2);
        classFromAge.put("-19",3);
        classFromAge.put("-32", 4);
        classFromAge.put("-45",5);
        classFromAge.put("+46", 6);
//        classFromAge.put(-1, "+46");
    }
    private void startServices(){
        startService(new Intent(HomeActivity.this, LockScreenService.class));
//        startService(new Intent(HomeActivity.this, TimerStartsWithBlockService.class));
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
        dialog.setContentView(R.layout.specify_age_dialog);

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
            IMAGE_CURRENT_AGE_CLASS = 6;
            SWITCH_STATE = false;
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
            // TODO SAVE AGE
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
    //    public void getInstallApps(){
//        PackageManager pk = getPackageManager();
//        Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ResolveInfo> resolveInfoList = pk.queryIntentActivities(intent, 0);
////        Metadata metadata = new Metadata.Builder(mGoogleApiClient).setPackageName("com.example.kidszone").build();
////        metadata.load(new MetadataLoadCallback() {
////            @Override
////            public void onMetadataLoaded(Metadata metadata) {
////                int contentRating = metadata.getContentRating().getRating();
////                Log.d("Age Rating", "The app age rating is: " + contentRating);
////            }
////
////            @Override
////            public void onMetadataFailed() {
////                Log.d("Age Rating", "Failed to load metadata");}
////        });
//
//        for (ResolveInfo resolveInfo : resolveInfoList) {
//            ActivityInfo activityInfo = resolveInfo.activityInfo;
//            String name = activityInfo.loadLabel(getPackageManager()).toString();
//            Drawable icon = activityInfo.loadIcon(getPackageManager());
////            String packageName = activityInfo.packageName;
//
//
//
//
//            String packageName = "com.roblox.client";
//           try{
//               Retrofit retrofit = new Retrofit.Builder()
//                       .baseUrl("https://a0d2-156-213-207-229.eu.ngrok.io/") // +"/app-age-rating?package_name="+packageName
//                       // as we are sending data in json format so
//                       // we have to add Gson converter factory
//                       .addConverterFactory(GsonConverterFactory.create())
//                       // at last we are building our retrofit builder.
//                       .build();
//               RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
//
//               Call<AppAgeRate> call = retrofitAPI.createPost(packageName);
//
////               AppAgeRate appAgeRate = call.execute().body();
////               String age_response = appAgeRate.getAgeRate();
////
////               Log.d("AppAgeRate", age_response);
////
//               call.enqueue(new Callback<AppAgeRate>() {
//                   @Override
//                   public void onResponse(Call<AppAgeRate> call, Response<AppAgeRate> response) {
//                       if (response.isSuccessful()) {
//                           AppAgeRate appAgeRate = response.body();
//                           String age_response = appAgeRate.getAgeRate();
//                           // Use the message here
//                       } else {
//                           // Handle error here
//                           Log.d("ON RESPONSE", "FAILED RESPONSE");
//                       }
//                   }
//                   @Override
//                   public void onFailure(Call<AppAgeRate> call, Throwable t) {
//                       // Handle failure here
//                       Log.d("onFailure", "FAILED");
//                   }
//               });
////            allApps.add(new AppModel(name,icon,0,packageName,ageRating));
//           } catch (Exception e) {
//               throw new RuntimeException(e);
//           }
//        }
//    }
//    public static void AUTOMATIC_BLOCK(){
//        if(HomeActivity.IS_CAMERA_RUNNING){
//
//            // classes = {"0-3","4-6","7-13","14-19","20-32","33-45","46+"};
//            String aClass = Age_prediction.classes[IMAGE_CURRENT_AGE_CLASS];
//            int startAge,endAge;
////            if (!lockedApps.isEmpty()){
////                lockedApps.clear();
////            }
//
////            lockedApps = BlockedApps.lockedAppsList;
//            if (!lockedAppsModel.isEmpty()){
//                lockedAppsModel.clear();
//            }
////            lockedApps=SharedPrefUtil.getInstance(ctx).getLockedAppsList();
//
//            if(IMAGE_CURRENT_AGE_CLASS !=6){
//                String [] arrOfStr= aClass.split("-",-2);
//                startAge=Integer.parseInt(arrOfStr[0]);
//                endAge=Integer.parseInt(arrOfStr[1]);
//            }
//            else{
//                startAge=46;
//                endAge=100;
//            }
//
//            List<String>lockedAppsPackages=new ArrayList<>();
//            if(allApps.isEmpty()){
//                Log.d("installed","EMPTYYYYYYYYY INSTALLLEEDDDDDDDDD APPPPPPPPS");
//            }
//            for (AppModel a:allApps) {
//
//                // Meta data != NULL
//                if(a.getAgeRating()!=-1){
//
//                    if(a.getAgeRating()>endAge){
//                        if(a.getStatus()==0){ // Not Blocked
//                            a.setStatus(1);
//                            lockedAppsModel.add(a);
//                            //update data
//                            lockedAppsPackages.add(a.getPackageName());
////                          SharedPrefUtil.getInstance(ctx).createLockedAppsList(lockedApps);
//                        }
//                    }
//
//                    else{
//                        if(a.getStatus()==1) {
//                            a.setStatus(0);
//                            lockedAppsModel.remove(a);
//                            //update data
////                            SharedPrefUtil.getInstance(ctx).createLockedAppsList(lockedApps);
//                        }
//                    }
//                }
//
//                // Meta data == NULL
//                else{
//                    if(endAge>=13){
//                        if(a.getStatus()==1) {
//                            a.setStatus(0);
//                            lockedAppsModel.remove(a);
//                            //update data
////                            SharedPrefUtil.getInstance(ctx).createLockedAppsList(lockedApps);
//
//                        }
//                    }
//                    else{
//                        if(a.getStatus()==0){
//                            a.setStatus(1);
//                            lockedAppsModel.add(a);
//                            //update data
//                            lockedAppsPackages.add(a.getPackageName());
////                            SharedPrefUtil.getInstance(ctx).createLockedAppsList(lockedApps);
//                        }
//                    }
//                }
//            }
//            SharedPrefUtil.getInstance(ctx).createLockedAppsList(lockedAppsPackages);
//        }
//    }
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
        AGE_TO_BE_BLOCKED_FOR = prefs.getString(SAVED_AGE_TO_BE_BLOCKED_FOR, "-13"); // The second parameter is the value that puts in the 1st parameter if it is empty
        SWITCH_STATE = prefs.getBoolean(SAVED_SWITCH_STATE, false); // The second parameter is the value that puts in the 1st parameter if it is empty

        if(IS_CAMERA_RUNNING)
        {
            binding.appSwitch.setChecked(true);
            // TODO Start the Camera Service
            SWITCH_STATE = true;
            Toast.makeText(HomeActivity.this,"Camera Is Monitoring NOW",Toast.LENGTH_LONG).show();
            if(Build.VERSION.SDK_INT >25){
                startForegroundService(new Intent(HomeActivity.this, GetBackCoreService.class));
            }else{
                startService(new Intent(HomeActivity.this, GetBackCoreService.class));
            }
        }

        else
            binding.appSwitch.setChecked(false);

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
        editor.putBoolean(SAVED_IS_CAMERA_RUNNING, IS_CAMERA_RUNNING);
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