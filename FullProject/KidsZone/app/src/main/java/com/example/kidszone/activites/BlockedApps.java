package com.example.kidszone.activites;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kidszone.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.kidszone.adapter.LockedAppAdapter;
import com.example.kidszone.app_model.AppModel;
import com.example.kidszone.shared.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

public class BlockedApps extends AppCompatActivity {
    public static List<AppModel> lockedAppsList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    static Context context;
    ImageView allAppsBtn;
    @SuppressLint("StaticFieldLeak")
    static LockedAppAdapter lockedAppsAdapter = new LockedAppAdapter(lockedAppsList, context);
    RecyclerView recyclerView;
    LockedAppAdapter adapter;
    static ProgressDialog progressDialog;
    @SuppressLint("StaticFieldLeak")
    static LinearLayout emptyLockListInfo;
    RelativeLayout enableUsageAccess, enableOverlayAccess;
    TextView btnEnableUsageAccess, btnEnableOverlay;
    ImageView checkBoxOverlay, checkBoxUsage;
    private static List<String> prefAppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_apps);
        getWindow().setStatusBarColor(ContextCompat.getColor(BlockedApps.this, R.color.beige));
        final Context context = this;

        progressDialog = new ProgressDialog(this);
        emptyLockListInfo = findViewById(R.id.emptyLockListInfo);
        allAppsBtn = findViewById(R.id.all_apps_button_img);
        enableOverlayAccess = findViewById(R.id.permissionsBoxDisplay);
        enableUsageAccess = findViewById(R.id.permissionsBoxUsage);
        btnEnableOverlay = findViewById(R.id.enableStatusDisplay);
        btnEnableUsageAccess = findViewById(R.id.enableStatusUsage);
        checkBoxOverlay = findViewById(R.id.checkedIconDisplay);
        checkBoxUsage = findViewById(R.id.checkedIconUsage);
        recyclerView = findViewById(R.id.lockedAppsList);
        adapter = new LockedAppAdapter(lockedAppsList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        toggleEmptyLockListInfo(context);
        getLockedApps(context);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_locked_apps);

        showBlockingInfo();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_locked_apps:
                    return true;
                case R.id.nav_all_apps:
                    startActivity(new Intent(getApplicationContext(),
                            AllMobileApps.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
        allAppsBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(BlockedApps.this, AllMobileApps.class);
            startActivity(myIntent);
        });
        progressDialog.setOnShowListener(dialog -> {
            toggleEmptyLockListInfo(context);
            getLockedApps(context);
        });

        //toggle permissions box
        togglePermissionBox();
    }

    private void showBlockingInfo(){
        SharedPrefUtil prefUtil = SharedPrefUtil.getInstance(this);
        //boolean checkSchedule = prefUtil.getBoolean("confirmSchedule");
        // String startTimeHour = prefUtil.getStartTimeHour();
        // String startTimeMin = prefUtil.getStartTimeMinute();
        //String endTimeHour = prefUtil.getEndTimeHour();
        //String endTimeMin = prefUtil.getEndTimeMinute();
        List<String> appsList = prefUtil.getLockedAppsList();
        //List<String> days = prefUtil.getDaysList();
        // List<String> shortDaysName = new ArrayList<>();
        //days.forEach(day -> shortDaysName.add(day.substring(0,3)));
        if(appsList.size() > 0){
            /*if(checkSchedule){
                scheduleMode.setText("Every " +String.join(", ", shortDaysName) +" from "+ startTimeHour+":"+startTimeMin+" to "+endTimeHour+":"+endTimeMin);
            } else {
                scheduleMode.setText("Always Blocking");
            }*/
        } else {
            //blockingInfoLayout.setVisibility(View.GONE);
        }
    }
    private void togglePermissionBox() {
        if (!Settings.canDrawOverlays(this) || !isAccessGranted()) {
            emptyLockListInfo.setVisibility(View.GONE);

            btnEnableOverlay.setOnClickListener(v -> overlayPermission());
            btnEnableUsageAccess.setOnClickListener(v -> accessPermission());

            if (Settings.canDrawOverlays(this)) {
                btnEnableOverlay.setVisibility(View.INVISIBLE);
                checkBoxOverlay.setColorFilter(Color.GREEN);
            }
            if (isAccessGranted()) {
                btnEnableUsageAccess.setVisibility(View.INVISIBLE);
                checkBoxUsage.setColorFilter(Color.GREEN);
            }
        } else {
            enableUsageAccess.setVisibility(View.GONE);
            enableOverlayAccess.setVisibility(View.GONE);
            toggleEmptyLockListInfo(this);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public static void getLockedApps(Context ctx) {
//        toggleEmptyLockListInfo(ctx);
        List<String> prefAppList = SharedPrefUtil.getInstance(ctx).getLockedAppsList();
        List<ApplicationInfo> packageInfos = ctx.getPackageManager().getInstalledApplications(0);
        lockedAppsList.clear();
        for (int i = 0; i < packageInfos.size(); i++) {
            if (packageInfos.get(i).icon > 0) {
                String name = packageInfos.get(i).loadLabel(ctx.getPackageManager()).toString();
                Drawable icon = packageInfos.get(i).loadIcon(ctx.getPackageManager());
                String packageName = packageInfos.get(i).packageName;
//                Bundle metaData =packageInfos.get(i).metaData;
//                int ageRating;
//                if (metaData!=null){
//                    ageRating = metaData.getInt("com.android.vending.DEMO_MODE_APP_AGE_RESTRICTION");
//                }
//                else{
//                    ageRating=-1;
//                }
//                int ageRating = metaData.getInt("com.android.vending.DEMO_MODE_APP_AGE_RESTRICTION");

                if (prefAppList.contains(packageName)) {
                    lockedAppsList.add(new AppModel(name, icon, 1, packageName));
                } else {
                    continue;
                }
            }
        }
        lockedAppsAdapter.notifyDataSetChanged();
//        progressDialog.dismiss();
    }
    public static void toggleEmptyLockListInfo(Context ctx) {
        prefAppList = SharedPrefUtil.getInstance(ctx).getLockedAppsList();
        if (prefAppList.size() > 0) {
            emptyLockListInfo.setVisibility(View.GONE);
        } else {
            emptyLockListInfo.setVisibility(View.VISIBLE);
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
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
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

    // TODO create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedule_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onResume() {
        super.onResume();
        togglePermissionBox();
        if(prefAppList!=null)
        {
            if (prefAppList.size() > 0) {
                emptyLockListInfo.setVisibility(View.GONE);
            } else {
                emptyLockListInfo.setVisibility(View.VISIBLE);
            }
        }
//        toggleEmptyLockListInfo(context);
    }

}