package com.example.floatingwindow;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class FreezeTimer {

//    public void startFreezing()
//    {
//                if(!Settings.canDrawOverlays(MainActivity.this))
//                {
//                    getPermission();
//                }
//                else
//                {
//                    Intent intent = new Intent(MainActivity.this, WidgetService.class);
//                    startService(intent);
//                    finish();
//                }
//    }
//    public void getPermission()
//    {
//        //check for alert window permission
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
//        {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
//            startActivityForResult(intent, 1);
//        }
//    }

}
