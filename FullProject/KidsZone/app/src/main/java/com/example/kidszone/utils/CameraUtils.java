package com.example.kidszone.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.util.Log;

import com.example.kidszone.shared.CameraConstants;

public class CameraUtils {
    public static boolean isFrontCameraPresent(Context context) {
        // Utils.context = context.getApplicationContext();

        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            if (context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_ANY)) {

                int numOfCameras = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    numOfCameras = Camera.getNumberOfCameras();
                }
                for (int i = 0; i < numOfCameras; i++) {
                    CameraInfo info = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        info = new CameraInfo();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        Camera.getCameraInfo(i, info);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
    public static class LogUtil {
        public static void LogD(String tag, String msg) {
            if (CameraConstants.DEBUG_FLAG)
                Log.d(tag, "===== " + msg + " =====");
        }

        public static void LogW(String tag, String msg) {
            if (CameraConstants.DEBUG_FLAG)
                Log.w(tag, "~~~~~ " + msg + " ~~~~~");
        }

        public static void LogE(String tag, String msg, Throwable e) {
            if (CameraConstants.DEBUG_FLAG)
                Log.e(tag, "^^^^^ " + msg + " ^^^^^", e);
        }
    }
}