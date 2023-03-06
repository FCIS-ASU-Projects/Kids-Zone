package com.example.gp_imagepreprocessing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.io.IOException;

public class MainActivity extends CameraActivity {

    Button select, camera;
    ImageView imageView;
    Bitmap bitmap_img;
    int SELECT_CODE = 100;
    MatOfFloat mat;

    CameraBridgeViewBase cameraBridgeViewBase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(OpenCVLoader.initDebug())Log.d("LOADED", "SUCCESS");
        else  Log.d("LOADED", "ERROR");

        camera = findViewById(R.id.camera);
        select = findViewById(R.id.select);
        imageView = findViewById(R.id.imageView);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_CODE && data!=null) //Means that the user chooses a new image
        {
            //Store the data in mipmap variable
            try {
                bitmap_img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                Log.i("Image Width", String.valueOf(bitmap_img.getWidth()));
                Log.i("Image Height", String.valueOf(bitmap_img.getHeight()));

                Bitmap resized_img = Bitmap.createScaledBitmap(bitmap_img, 96, 96, true);
                Log.i("Image Width", String.valueOf(resized_img.getWidth()));
                Log.i("Image Height", String.valueOf(resized_img.getHeight()));

                imageView.setImageBitmap(resized_img); //To see that the image has been selected successfully
                mat = new MatOfFloat();
                Utils.bitmapToMat(resized_img, mat);

//                Core.normalize(mat, mat, 0, 255, 2, CvType.CV_8UC3);
//                Core.normalize(mat, mat, 0, 1, Core.NORM_MINMAX, -1, new Mat());

//                Core.normalize(mat, mat, 0.0, 1.0, Core.NORM_MINMAX, CvType.CV_64FC1);

//                Scalar max = new Scalar(minmax.maxVal);
                Core.multiply(mat, Scalar.all(1.0/255.0), mat);
//                Log.d("Scalar.all(1.0/255.0)  ", String.valueOf(Scalar.all(1.0/255.0)));
                Log.d("Image Info", String.valueOf(mat.dump()));
                Log.d("Image Info", String.valueOf(mat.dump()));

            } catch (IOException e) {
                Log.d("SELECT", "ERROR IN SELECTING THE IMAGE");
                throw new RuntimeException(e);
            }

        }
    }
}

//////////////////////////////////////////////
//package com.example.myapplication;
//
//        import androidx.annotation.NonNull;
//        import androidx.annotation.Nullable;
//        import androidx.appcompat.app.AppCompatActivity;
//
//        import android.Manifest;
//        import android.content.Intent;
//        import android.content.pm.PackageManager;
//        import android.graphics.Bitmap;
//        import android.media.Image;
//        import android.os.Bundle;
//        import android.provider.MediaStore;
//        import android.util.Log;
//        import android.view.View;
//        import android.widget.Button;
//        import android.widget.ImageView;
//
//        import org.opencv.android.CameraActivity;
//        import org.opencv.android.CameraBridgeViewBase;
//        import org.opencv.android.OpenCVLoader;
//        import org.opencv.android.Utils;
//        import org.opencv.core.Mat;
//
//        import java.io.IOException;
//        import java.util.Collections;
//        import java.util.List;
//
//public class MainActivity extends CameraActivity {
//
//    CameraBridgeViewBase cameraBridgeViewBase;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        getPermission();
//
//        cameraBridgeViewBase = findViewById(R.id.cameraView);
//        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
//            @Override
//            public void onCameraViewStarted(int width, int height) {
//
//            }
//
//            @Override
//            public void onCameraViewStopped() {
//
//            }
//
//            @Override
//            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//                return inputFrame.rgba(); //convert the frame to red green blue alpha
//            }
//        });
//        if(OpenCVLoader.initDebug())
//        {
//            Log.d("LOADED", "SUCCESS");
//            cameraBridgeViewBase.enableView();
//        }
//        else  Log.d("LOADED", "ERROR");
//    }
//
//    @Override
//    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
//        return Collections.singletonList(cameraBridgeViewBase);
//    }
//
//    void getPermission()
//    {
//        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//        {
//            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        //If we did not take the permission, so take it
//        if(grantResults.length>0 && grantResults[0]!=PackageManager.PERMISSION_GRANTED)
//        {
//            getPermission();
//        }
//    }
//}