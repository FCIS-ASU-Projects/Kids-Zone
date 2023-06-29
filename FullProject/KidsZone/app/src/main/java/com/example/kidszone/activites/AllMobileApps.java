package com.example.kidszone.activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.kidszone.R;
import com.example.kidszone.adapter.AllAppsAdapter;
import com.example.kidszone.app_model.AppModel;
import com.example.kidszone.databinding.ActivityAllMobileAppsBinding;
import com.example.kidszone.shared.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

public class AllMobileApps extends AppCompatActivity {
    private Context context;
    private ActivityAllMobileAppsBinding binding;
    private List<AppModel> apps = new ArrayList<>();
    private AllAppsAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_mobile_apps);
        getWindow().setStatusBarColor(ContextCompat.getColor(AllMobileApps.this, R.color.black));
        context = this;

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
                            UnblockedApps.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_all_apps:
                    return true;
            }
            return false;
        });


        adapter = new AllAppsAdapter(apps, this);
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

//    public void getInstalledApps() {
//        List<String> prefLockedAppList = SharedPrefUtil.getInstance(this).getLockedAppsList();
//        /*List<ApplicationInfo> packageInfos = getPackageManager().getInstalledApplications(0);*/
//        PackageManager pk = getPackageManager();
//        Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ResolveInfo> resolveInfoList = pk.queryIntentActivities(intent, 0);
//
//        for (ResolveInfo resolveInfo : resolveInfoList) {
//            ActivityInfo activityInfo = resolveInfo.activityInfo;
//            String name = activityInfo.loadLabel(getPackageManager()).toString();
//            Drawable icon = activityInfo.loadIcon(getPackageManager());
//            String packageName = activityInfo.packageName;
//
//            if (!packageName.matches("com.robocora.appsift|com.android.settings")) {
//                if (!prefLockedAppList.isEmpty()) {
//                    //check if app is BLOCKED OR NOT
//                    if (prefLockedAppList.contains(packageName)) {
//                        apps.add(new AppModel(name, icon, 0, packageName)); // BLOCKED APP
//                    }
//                    else {
//                        apps.add(new AppModel(name, icon, 1, packageName)); // UNBLOCKED APP
//                    }
//                }
//                else {
//                    apps.add(new AppModel(name, icon, 0, packageName)); // BLOCKED APP
//                }
//            }
//            else {
//                //do not add settings to app list
//            }
//        }
//
//        adapter.notifyDataSetChanged();
//    }
    public void getInstalledApps() {
        List<String> prefLockedAppList = SharedPrefUtil.getInstance(this).getLockedAppsList();
        List<ApplicationInfo> packagesInfo = context.getPackageManager().getInstalledApplications(0);

        for (int i = 0; i < packagesInfo.size(); i++) {
            if (packagesInfo.get(i).icon > 0) { //  THERE IS AN ICON FOR THIS APP
                String name = packagesInfo.get(i).loadLabel(context.getPackageManager()).toString();
                Drawable icon = packagesInfo.get(i).loadIcon(context.getPackageManager());
                String packageName = packagesInfo.get(i).packageName;

                if (!prefLockedAppList.isEmpty()) {
                    // TODO check if this app is BLOCKED or NOT
                    if (prefLockedAppList.contains(packageName)) {
                        apps.add(new AppModel(name, icon, 0, packageName)); // BLOCKED APP
                    }
                    else {
                        apps.add(new AppModel(name, icon, 1, packageName)); // UNBLOCKED APP
                    }
                }
                else {
                    apps.add(new AppModel(name, icon, 1, packageName)); // UNBLOCKED APP
                }
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
}