package com.example.floatingwindow;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

public class FreezeService extends Service {

    int LAYOUT_FLAG;
    View mFloatingView;
    WindowManager windowManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else
        {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //inflate widget layout
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_widget, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        int width = mFloatingView.getMeasuredHeight();
        int height = mFloatingView.getMeasuredWidth();

        layoutParams.gravity = Gravity.TOP|Gravity.RIGHT|Gravity.LEFT;
        layoutParams.x = width;
        layoutParams.y = height;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(mFloatingView, layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFloatingView!=null)
            windowManager.removeView(mFloatingView);
    }

}
