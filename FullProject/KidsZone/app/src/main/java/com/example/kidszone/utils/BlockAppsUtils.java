package com.example.kidszone.utils;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.List;

import static android.app.AppOpsManager.MODE_ALLOWED;

public class BlockAppsUtils {
    UsageStatsManager usageStatsManager;
    private final Context context;

    public BlockAppsUtils(Context context) {
        this.context = context;
    }

    public String getLauncherTopApp() {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> taskInfoList = manager.getRunningTasks(1);
            if (null != taskInfoList && !taskInfoList.isEmpty()) {
                return taskInfoList.get(0).topActivity.getPackageName();
            }
        }
        else {
            long endTime = System.currentTimeMillis(); //????????????????????????????????
            long beginTime = endTime - 10000; //????????????????????????????????
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!TextUtils.isEmpty(result))
                Log.d("RESULT", result);
            return result;
        }
        return "";
    }

}
