package com.example.kidszone.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.widget.Toast;

import com.example.kidszone.R;
import com.example.kidszone.hiddencamera.CameraView;
import com.example.kidszone.shared.CameraConstants;
import com.example.kidszone.shared.GetBackFeatures;
import com.example.kidszone.shared.GetBackStateFlags;
import com.example.kidszone.shared.IFrontCaptureCallback;
import com.example.kidszone.utils.CameraUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class GetBackCoreService extends Service implements
        IFrontCaptureCallback {

    private static ActionLocks actionLocks = null;
    private SharedPreferences preferences;
    private String photoPath = null;
    private static GetBackStateFlags stateFlags = new GetBackStateFlags();
    private static GetBackFeatures features = new GetBackFeatures();
    public static Timer ten_seconds_timer;
    Looper myLooper = Looper.myLooper();
    private static boolean isModeActive = false;
    private static class ActionLocks {
        public AtomicBoolean lockCapture;
        public AtomicBoolean lockSmsSend;
        public AtomicBoolean lockEmailSend;
        public AtomicBoolean lockLocationFind;
        public AtomicBoolean lockDataDelete;

        public ActionLocks() {
            lockCapture = new AtomicBoolean(false);
            lockSmsSend = new AtomicBoolean(false);
            lockEmailSend = new AtomicBoolean(false);
            lockLocationFind = new AtomicBoolean(false);
            lockDataDelete = new AtomicBoolean(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(getApplicationContext(),"Service is Destroyed",Toast.LENGTH_LONG).show();
        CameraUtils.LogUtil.LogD(CameraConstants.LOG_TAG, "Service Destroyed");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //  Toast.makeText(this,"",Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= 26) {
            //if more than 26
            if(Build.VERSION.SDK_INT > 26){
                String CHANNEL_ONE_ID = ".com.example.service";
                String CHANNEL_ONE_NAME = "hidden camera";
                NotificationChannel notificationChannel = null;
                notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                        CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.createNotificationChannel(notificationChannel);
                }

                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.child_background);
                Notification notification = new Notification.Builder(getApplicationContext())
                        .setChannelId(CHANNEL_ONE_ID)
                        .setContentTitle("Service")
                        .setContentText("Capture picture using foreground service")
                        .setSmallIcon(R.drawable.child_background)
                        .setLargeIcon(icon)
                        .build();

                Intent notificationIntent = new Intent(getApplicationContext(), GetBackCoreService.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                notification.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                startForeground(104, notification);
            }
            //if version 26
            else{
                startForeground(104, updateNotification());
            }
        }
        //if less than version 26
        else{
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Service")
                    .setContentText("Capture picture using foreground service")
                    .setSmallIcon(R.drawable.child_background)
                    .setOngoing(true).build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                startForeground(104, notification);
            }
        }

        takeAction(null);

        return START_STICKY;
    }
    @Override
    public void onPhotoCaptured(String filePath) {

        synchronized (stateFlags){
            Toast.makeText(this,"onPhotoCaptured",Toast.LENGTH_LONG).show();

            stateFlags.isPhotoCaptured = true;
            addBooleanPreference(CameraConstants.PREFERENCE_IS_PHOTO_CAPTURED,
                    stateFlags.isPhotoCaptured);

            CameraUtils.LogUtil.LogD(CameraConstants.LOG_TAG, "Image saved at - "
                    + filePath);
            photoPath = filePath;
            addStringPreference(CameraConstants.PREFERENCE_PHOTO_PATH, photoPath);
        }

        actionLocks.lockCapture.set(false);

        takeAction(null);
    }
    private synchronized void takeAction(Bundle bundle) {
        //capturePhoto();

        ten_seconds_timer = new Timer();

        ten_seconds_timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        capturePhoto();
                    }
                });
            }
        }, 10000, 10000);
    }
    private void capturePhoto() {
        Log.d("capturePhoto","function");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CameraUtils.LogUtil.LogD(CameraConstants.LOG_TAG,
                            "Inside captureThread run");

                    myLooper.prepare();

                    // Check if phone is being used.
                    CameraView frontCapture = new CameraView(
                            GetBackCoreService.this.getBaseContext());
                    frontCapture.capturePhoto(GetBackCoreService.this);

                    myLooper.loop();
                }
            }).start();
        }
    private Notification updateNotification() {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GetBackCoreService.class), 0);

        return new NotificationCompat.Builder(this)
                .setTicker("Ticker")
                .setContentTitle("Service")
                .setContentText("Capture picture using foreground service")
                .setSmallIcon(R.drawable.child_background)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
    }
    public GetBackCoreService() {
        super();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void addBooleanPreference(String key, boolean value) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }
    private void addStringPreference(String key, String value) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.commit();
    }
    @Override
    public void onCaptureError(int errorCode) {}
}