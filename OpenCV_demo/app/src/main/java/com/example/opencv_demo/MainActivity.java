package com.example.opencv_demo;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.opencv_demo.ml.Mobilenetv2BestModel;
import com.example.opencv_demo.ml.Yolov5sFace;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class MainActivity extends AppCompatActivity {
    Button select;
    TextView age;
    ImageView imageView;
    Bitmap bitmap;
    Mat mat;
    int SELECT_CODE = 100;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        age = (TextView) findViewById(R.id.age);

        if (OpenCVLoader.initDebug()) Log.i("loader", "success");
        else Log.i("loader", "err");

        select = findViewById(R.id.select);
        imageView = findViewById(R.id.image);
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

        if (requestCode == SELECT_CODE && data != null)
        {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
//                Bitmap bMap=BitmapFactory.decodeResource(getResources(),R.drawable.faces);
                mat = new Mat();
                Utils.bitmapToMat(bitmap, mat);
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGRA2RGB);
//                Utils.matToBitmap(mat,bitmap);


                File yolo_file = new File(getCacheDir() + "/yolov5s-face.onnx");

                if (!yolo_file.exists()) try {
                    InputStream is = getAssets().open("yolov5s-face.onnx");
                    int size = is.available();
                    byte[] bufer = new byte[size];
                    is.read(bufer);
                    is.close();

                    FileOutputStream fos = new FileOutputStream(yolo_file);
                    fos.write(bufer);
                    fos.close();
                } catch (Exception e) {
                    Log.i("Exception", "failed to read model file");
                }

                Age_prediction AGE = new Age_prediction(yolo_file,getApplicationContext());
                age.setText("Age Range: " + AGE.detection_prediction(mat));

//                Face_detection yolo = new Face_detection(f.getPath(), 0.45f, 0.3f);
//                Bitmap frame = yolo.detect_face(mat, MainActivity.this);
//
//
//                if (frame != null)
//                {
//                    imageView.setImageBitmap(frame);
//                    Age_prediction Dage = new Age_prediction();
//                    age.setText("Age Range: " + Dage.predict_age(frame,getApplicationContext()));
//                }



                //Log.i("size of data",String.valueOf(outs.get(0).size()));

            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }

        }
    }
    public void loadDeepLearningModels(){
        try {
            File yolo_file = new File(getCacheDir() + "/yolov5s-face.onnx");

            if (!yolo_file.exists())
            {
                try {
                    InputStream is = getAssets().open("yolov5s-face.onnx");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();

                    FileOutputStream fos = new FileOutputStream(yolo_file);
                    fos.write(buffer);
                    fos.close();
                }catch (Exception e) {
                    Log.i("Exception", e.getMessage());
                }
            }
            Age_prediction(yolo_file,getApplicationContext()); // Loading 2 models, App start (SAVE THIS VAR)
            a = new AppModel("Huddaaaaaaaaaaaaaaa");

            if(AGE_PREDICTION == null)
                Log.d("SAWAAAAAAAAAAAAAATYYYYYYY", "SAWAAAAAAAAAAAAAATYYYYYYY");

            // TODO SAVE AGE
            Log.d("SAVING MODELS", "SUCCESS");
        } catch (Exception e) {
            Log.i("Exception", e.getMessage());
        }
    }
    public static void getAgeFromImage(Bitmap bitmap){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Core.rotate(mat, mat, -90);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGRA2RGB);

        CURRENT_AGE_CLASS = AGE_PREDICTION.detection_prediction(mat);
    }
        }
