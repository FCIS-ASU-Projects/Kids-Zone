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

import com.example.kidszone.HomeActivity;
import com.example.kidszone.R;
import com.example.kidszone.adapter.UnblockedAppsAdapter;
import com.example.kidszone.app_model.AppModel;
import com.example.kidszone.databinding.ActivityUnblockedAppsBinding;
import com.example.kidszone.shared.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

public class UnblockedApps extends AppCompatActivity {
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static ActivityUnblockedAppsBinding binding;
    public static List<AppModel> unblockedApps = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private static UnblockedAppsAdapter unblockedAppsAdapter;
    private UnblockedAppsAdapter adapter;
    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unblocked_apps);
        getWindow().setStatusBarColor(ContextCompat.getColor(UnblockedApps.this, R.color.black));
        context = this;

        binding = ActivityUnblockedAppsBinding.inflate(getLayoutInflater());
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

        unblockedAppsAdapter = new UnblockedAppsAdapter(unblockedApps, this.getApplicationContext());
        progressDialog = new ProgressDialog(this);
        adapter = new UnblockedAppsAdapter(unblockedApps, this);

        binding.unblockedAppsList.setLayoutManager(new LinearLayoutManager(this));
        binding.unblockedAppsList.setAdapter(adapter);

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
    }
    public void openHelpActivity(){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
    @SuppressLint("NotifyDataSetChanged")
    private static void getUnblockedApps(Context ctx) {
        List<String> unblockedApps = SharedPrefUtil.getInstance(ctx).getUnblockedAppsList();
        UnblockedApps.unblockedApps.clear();

        for (int i = 0; i < HomeActivity.ALL_MOBILE_APPS.size(); i++) {
            if (HomeActivity.ALL_MOBILE_APPS.get(i).icon > 0) { //  THERE IS AN ICON FOR THIS APP
                String packageName = HomeActivity.ALL_MOBILE_APPS.get(i).packageName;

                if (unblockedApps.contains(packageName)) {
                    String name = HomeActivity.ALL_MOBILE_APPS.get(i).loadLabel(ctx.getPackageManager()).toString();
                    Drawable icon = HomeActivity.ALL_MOBILE_APPS.get(i).loadIcon(ctx.getPackageManager());
                    UnblockedApps.unblockedApps.add(new AppModel(name, icon, 1, packageName)); // UNBLOCKED APP
                }
            }
        }

        unblockedAppsAdapter.notifyDataSetChanged();
    }
    public static void toggleEmptyLockListInfo() {
        if (unblockedApps.size() > 0) {
            binding.emptyUnblockListInfo.setVisibility(View.GONE);
        } else {
            binding.emptyUnblockListInfo.setVisibility(View.VISIBLE);
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
        toggleEmptyLockListInfo();
    }
}