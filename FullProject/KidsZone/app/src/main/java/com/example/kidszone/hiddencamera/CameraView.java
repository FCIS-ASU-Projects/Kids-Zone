package com.example.kidszone.hiddencamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import static android.content.ContentValues.TAG;
import static android.content.Context.WINDOW_SERVICE;

import com.example.kidszone.HomeActivity;
import com.example.kidszone.services.GetBackCoreService;
import com.example.kidszone.shared.CameraConstants;
import com.example.kidszone.shared.IFrontCaptureCallback;
import com.example.kidszone.utils.CameraUtils;

public class CameraView implements SurfaceHolder.Callback, PictureCallback, ErrorCallback {
    private Context context = null;
    private WindowManager winMan;
    public static SurfaceHolder sHolder;
    public static Camera camera;
    private Parameters parameters;
    private AudioManager audioMgr = null;
    private WindowManager.LayoutParams params = null;
    private IFrontCaptureCallback callback;
    private SurfaceView surfaceView = null;

    public CameraView(Context ctx) {
        context = ctx;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            audioMgr = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO get camera parameters
        parameters = camera.getParameters();

        // TODO set camera parameters
        camera.setParameters(parameters);
        camera.startPreview();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            audioMgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        Log.d(TAG, "Taking picture");

        camera.takePicture(null, null, this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            Log.d(TAG, "Camera Opened");
            camera.setPreviewDisplay(sHolder);

        } catch (IOException exception) {
            camera.release();
            camera = null;
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if(camera != null)
        {
            // TODO stop the preview
            camera.stopPreview();

            // TODO release the camera
            camera.release();
            Log.d(TAG, "Camera released");

            // TODO unbind the camera from this object
            camera = null;
        }
    }
    public void capturePhoto(IFrontCaptureCallback frontCaptureCb) {

        callback = frontCaptureCb;

        if (!CameraUtils.isFrontCameraPresent(context))
            callback.onCaptureError(-1);

        surfaceView = new SurfaceView(context);
        winMan = (WindowManager) context
                .getSystemService(WINDOW_SERVICE);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                1,
                1,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        winMan.addView(surfaceView, params);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            surfaceView.setZOrderOnTop(true);
        }

        SurfaceHolder holder = surfaceView.getHolder();

        holder.setFormat(PixelFormat.TRANSPARENT);

        sHolder = holder;
        sHolder.addCallback(this);
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        CameraUtils.LogUtil.LogD(CameraConstants.LOG_TAG, "Opening Camera");

        // The Surface has been created, acquire the camera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
    }
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        if (data != null) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
                audioMgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);

            WindowManager winMan = (WindowManager) context
                    .getSystemService(WINDOW_SERVICE);
            winMan.removeView(surfaceView);

            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                        data.length, opts);

                // TODO GET THE AGE OF THE IMAGE (Note: image need to be rotated with -90 degrees)
                HomeActivity.getAgeFromImage(bitmap);

                Log.d("HomeActivity.getAgeFromImage(bitmap) ", "=============================================================================");
                Log.d("HomeActivity.getAgeFromImage(bitmap) ", Integer.toString(HomeActivity.IMAGE_CURRENT_AGE_CLASS));
                Log.d("HomeActivity.getAgeFromImage(bitmap) ", "=============================================================================");


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

                Log.d("RETURNED FROM MODEL", Integer.toString(HomeActivity.IMAGE_CURRENT_AGE_CLASS));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        GetBackCoreService getBackCoreService=new GetBackCoreService();
        getBackCoreService.stopSelf();
    }
    @Override
    public void onError(int error, Camera camera) {
        Log.d(TAG, "Camera Error : " + error, null);

        WindowManager winMan = (WindowManager) context
                .getSystemService(WINDOW_SERVICE);
        winMan.removeView(surfaceView);
        callback.onCaptureError(-1);
    }
}