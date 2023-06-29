package com.example.kidszone.activites;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kidszone.R;
import com.example.kidszone.databinding.ActivityBlockedAppsBinding;
import com.example.kidszone.adapter.UnblockedAppsAdapter;
import com.example.kidszone.app_model.AppModel;
import com.example.kidszone.shared.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

public class UnblockedApps extends AppCompatActivity {
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static ActivityBlockedAppsBinding binding;
    public static List<AppModel> unblockedApps = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private static UnblockedAppsAdapter lockedAppsAdapter;
    private UnblockedAppsAdapter adapter;
    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_apps);
        getWindow().setStatusBarColor(ContextCompat.getColor(UnblockedApps.this, R.color.black));
        context = this;

        binding = ActivityBlockedAppsBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        binding.appBar.helpIcon.setOnClickListener(view -> openHelpActivity());

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

        lockedAppsAdapter = new UnblockedAppsAdapter(unblockedApps, this.getApplicationContext());
        progressDialog = new ProgressDialog(this);
        adapter = new UnblockedAppsAdapter(unblockedApps, this);

        binding.lockedAppsList.setLayoutManager(new LinearLayoutManager(this));
        binding.lockedAppsList.setAdapter(adapter);

        getUnblockedApps(context);
        toggleEmptyLockListInfo();

        binding.allAppsButtonImg.setOnClickListener(view -> {
            Intent myIntent = new Intent(UnblockedApps.this, AllMobileApps.class);
            this.finish();
            startActivity(myIntent);
        });
        progressDialog.setOnShowListener(dialog -> {
            getUnblockedApps(context);
            toggleEmptyLockListInfo();
        });

        togglePermissionBox();
    }
    public void openHelpActivity(){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
    @SuppressLint("NotifyDataSetChanged")
    private static void getUnblockedApps(Context ctx) {
//        toggleEmptyLockListInfo(ctx);
        List<String> prefAppList = SharedPrefUtil.getInstance(ctx).getLockedAppsList();
        List<ApplicationInfo> packagesInfo = ctx.getPackageManager().getInstalledApplications(0);
        unblockedApps.clear();

        for (int i = 0; i < packagesInfo.size(); i++) {
            if (packagesInfo.get(i).icon > 0) { //  THERE IS AN ICON FOR THIS APP
                String name = packagesInfo.get(i).loadLabel(ctx.getPackageManager()).toString();
                Drawable icon = packagesInfo.get(i).loadIcon(ctx.getPackageManager());
                String packageName = packagesInfo.get(i).packageName;

                if (!prefAppList.contains(packageName)) {
                    unblockedApps.add(new AppModel(name, icon, 1, packageName)); // UNBLOCKED APP
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
            toggleEmptyLockListInfo();
        }
    }
    public static void toggleEmptyLockListInfo() {
        if (unblockedApps.size() > 0) {
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedule_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onResume() {
        super.onResume();
        togglePermissionBox();
        toggleEmptyLockListInfo();
    }
}