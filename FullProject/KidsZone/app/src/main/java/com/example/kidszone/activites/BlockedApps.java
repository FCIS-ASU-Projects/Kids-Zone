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
import com.example.kidszone.databinding.ActivityAllMobileAppsBinding;
import com.example.kidszone.databinding.ActivityBlockedAppsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.kidszone.adapter.LockedAppAdapter;
import com.example.kidszone.app_model.AppModel;
import com.example.kidszone.shared.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

public class BlockedApps extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static ActivityBlockedAppsBinding binding;
    private static List<AppModel> lockedAppsList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private static LockedAppAdapter lockedAppsAdapter;
    private LockedAppAdapter adapter;
    private static ProgressDialog progressDialog;
    private static List<String> prefAppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_apps);
        getWindow().setStatusBarColor(ContextCompat.getColor(BlockedApps.this, R.color.black));
        final Context context = this;

        binding = ActivityBlockedAppsBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        binding.bottomNavigation.setSelectedItemId(R.id.nav_locked_apps);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_locked_apps:
                    return true;
                case R.id.nav_all_apps:
                    this.finish();
                    startActivity(new Intent(getApplicationContext(),
                            AllMobileApps.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });

        binding.appBar.helpIcon.setOnClickListener(view -> openHelpActivity());

        lockedAppsAdapter = new LockedAppAdapter(lockedAppsList, this.getApplicationContext());
        progressDialog = new ProgressDialog(this);
        adapter = new LockedAppAdapter(lockedAppsList, this);

        binding.lockedAppsList.setLayoutManager(new LinearLayoutManager(this));
        binding.lockedAppsList.setAdapter(adapter);

        toggleEmptyLockListInfo(context);
        getLockedApps(context);


        binding.allAppsButtonImg.setOnClickListener(view -> {
            Intent myIntent = new Intent(BlockedApps.this, AllMobileApps.class);
            this.finish();
            startActivity(myIntent);
        });
        progressDialog.setOnShowListener(dialog -> {
            toggleEmptyLockListInfo(context);
            getLockedApps(context);
        });

        //toggle permissions box
        togglePermissionBox();
    }
    public void openHelpActivity(){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
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

                if (prefAppList.contains(packageName)) {
                    lockedAppsList.add(new AppModel(name, icon, 1, packageName));
                }
            }
        }
        lockedAppsAdapter.notifyDataSetChanged();
//        progressDialog.dismiss();
    }
    private void togglePermissionBox() {
        if (!Settings.canDrawOverlays(this) || !isAccessGranted()) {
            binding.emptyLockListInfo.setVisibility(View.GONE);

            binding.enableStatusDisplay.setOnClickListener(v -> overlayPermission());
            binding.enableStatusUsage.setOnClickListener(v -> accessPermission());

            if (Settings.canDrawOverlays(this)) {
                binding.enableStatusDisplay.setVisibility(View.INVISIBLE);
                binding.checkedIconDisplay.setColorFilter(Color.GREEN);
            }
            if (isAccessGranted()) {
                binding.enableStatusUsage.setVisibility(View.INVISIBLE);
                binding.checkedIconUsage.setColorFilter(Color.GREEN);
            }
        } else {
            binding.permissionsBoxUsage.setVisibility(View.GONE);
            binding.permissionsBoxDisplay.setVisibility(View.GONE);
            toggleEmptyLockListInfo(this);
        }
    }
    public static void toggleEmptyLockListInfo(Context ctx) {
        prefAppList = SharedPrefUtil.getInstance(ctx).getLockedAppsList();
        if (prefAppList.size() > 0) {
            binding.emptyLockListInfo.setVisibility(View.GONE);
        } else {
            binding.emptyLockListInfo.setVisibility(View.VISIBLE);
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
                binding.emptyLockListInfo.setVisibility(View.GONE);
            } else {
                binding.emptyLockListInfo.setVisibility(View.VISIBLE);
            }
        }
    }

}