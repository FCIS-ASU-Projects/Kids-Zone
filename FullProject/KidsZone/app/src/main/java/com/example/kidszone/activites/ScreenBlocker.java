package com.example.kidszone.activites;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.kidszone.R;
import com.example.kidszone.databinding.ActivityHomeBinding;
import com.example.kidszone.databinding.ActivityScreenBlockerBinding;
import com.example.kidszone.shared.SharedPrefUtil;

public class ScreenBlocker extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_blocker);
        getWindow().setStatusBarColor(ContextCompat.getColor(ScreenBlocker.this, R.color.black));
        initIconApp();

        ActivityScreenBlockerBinding binding = ActivityScreenBlockerBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        binding.closeBlockScreenBtn.setOnClickListener(v1 -> onBackPressed());

    }

    private void initIconApp() {
        if (getIntent().getStringExtra("broadcast_receiver") != null) {
            ImageView icon = findViewById(R.id.app_icon);
            TextView blockInfo = findViewById(R.id.empty_blocked_list_text);
            String current_app = new SharedPrefUtil(this).getLastApp();
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = getPackageManager().getApplicationInfo(current_app, 0);
                icon.setImageDrawable(applicationInfo.loadIcon(getPackageManager()));
                blockInfo.setText(applicationInfo.loadLabel(getPackageManager()).toString().toUpperCase() + " is blocked by Kids Zone.");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}