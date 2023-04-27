package com.example.opencv_demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;


import com.example.opencv_demo.ml.Mobilenetv2BestModel;

import org.opencv.core.Mat;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Age_prediction {

    int imageSize=96;
    private Mobilenetv2BestModel age_model;
    private Face_detection yolo;
    private static String[] classes = {"0-3","4-6","7-13","14-19","20-32","33-45","46+"};
    private static  ImageProcessor imageProcessor =new ImageProcessor.Builder()
            .add(new ResizeOp( 96 , 96 , ResizeOp.ResizeMethod.BILINEAR ) )
            .add(new NormalizeOp(0.0f , 255.0f))
            .build();



    public Age_prediction(File yolo_file,Context context)
    {
        this.yolo = new Face_detection(yolo_file.getPath(), 0.45f, 0.3f);
        this.age_model=load_model(context);
    }
    public String detection_prediction(Mat mat)
    {

//        Face_detection yolo = new Face_detection(f.getPath(), 0.45f, 0.3f);
        Bitmap frame = yolo.detect_face(mat);


        if (frame != null)
        {
            return predict_age(frame);
        }
        return "faild";
    }
    private Mobilenetv2BestModel load_model(Context context)
    {
        Mobilenetv2BestModel model=null;
        try {
             model = Mobilenetv2BestModel.newInstance(context);
        }
        catch (IOException e)
        {
            Log.i( "Exception", "failed to load tflite model");
        }
        return model;
    }
    private String predict_age(Bitmap bitmap)
    {
        int maxClass = 0;

        try {
//            Mobilenetv2BestModel model = Mobilenetv2BestModel.newInstance(context);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1,96, 96,3}, DataType.FLOAT32);
            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);


            tensorImage.load(bitmap);
            tensorImage = imageProcessor.process(tensorImage);
            ByteBuffer byteBuffer = tensorImage.getBuffer();


            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Mobilenetv2BestModel.Outputs outputs = age_model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();

            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxClass = i;
                }
            }

            // Releases model resources if no longer used.
//            age_model.close();
        }
        catch (Exception e)
        {
            Log.i( "Exception", "failed to predict age");
        }

        return classes[maxClass];
    }
}
