package com.example.kidszone.activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kidszone.R;
import com.example.kidszone.adapter.AllAppAdapter;
import com.example.kidszone.app_model.AppModel;
import com.example.kidszone.databinding.ActivityAllMobileAppsBinding;
import com.example.kidszone.databinding.ActivityHomeBinding;
import com.example.kidszone.shared.SharedPrefUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AllMobileApps extends AppCompatActivity {
    private ActivityAllMobileAppsBinding binding;
    private List<AppModel> apps = new ArrayList<>();
    private AllAppAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_mobile_apps);
        getWindow().setStatusBarColor(ContextCompat.getColor(AllMobileApps.this, R.color.black));

        binding = ActivityAllMobileAppsBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        binding.appBar.helpIcon.setOnClickListener(view -> openHelpActivity());

        binding.bottomNavigation.setSelectedItemId(R.id.nav_all_apps);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_locked_apps:
                    this.finish();
                    startActivity(new Intent(getApplicationContext(),
                            BlockedApps.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_all_apps:
                    return true;
            }
            return false;
        });


        adapter = new AllAppAdapter(apps, this);
        binding.recycleView.setLayoutManager(new GridLayoutManager(this, 5));
        binding.recycleView.setAdapter(adapter);
        getInstalledApps();
        progressDialog = new ProgressDialog(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, 300);
    }

    public void openHelpActivity(){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.setTitle("Fetching Apps");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, 300);

    }
    public void getInstalledApps() {
        List<String> prefLockedAppList = SharedPrefUtil.getInstance(this).getLockedAppsList();
        /*List<ApplicationInfo> packageInfos = getPackageManager().getInstalledApplications(0);*/
        PackageManager pk = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = pk.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String name = activityInfo.loadLabel(getPackageManager()).toString();
            Drawable icon = activityInfo.loadIcon(getPackageManager());
            String packageName = activityInfo.packageName;
            if (!packageName.matches("com.robocora.appsift|com.android.settings")) {
                if (!prefLockedAppList.isEmpty()) {
                    //check if apps is locked
                    if (prefLockedAppList.contains(packageName)) {
                        apps.add(new AppModel(name, icon, 1, packageName));
                    } else {
                        apps.add(new AppModel(name, icon, 0, packageName));
                    }
                } else {
                    apps.add(new AppModel(name, icon, 0, packageName));
                }
            } else {
                //do not add settings to app list
            }

        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String userInput = newText.toLowerCase();
                ArrayList<AppModel> newList = new ArrayList<>();
                for (AppModel app : apps) {
                    if (app.getAppName().toLowerCase().contains(userInput)) {
                        newList.add(app);
                    }
                }
                adapter.updateList(newList);
                return false;
            }
        });
        return true;
    }
}