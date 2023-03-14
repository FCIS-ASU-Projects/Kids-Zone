package com.example.opencv_demo;



import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.opencv_demo.ml.Yolov5sFace;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
    Button select;
    ImageView imageView;
    Bitmap bitmap;
    Mat mat;
    int SELECT_CODE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(OpenCVLoader.initDebug()) Log.i("loader","success");
        else Log.i("loader","err");

        select=findViewById(R.id.select);
        imageView=findViewById(R.id.image);
        select.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,SELECT_CODE);
            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_CODE && data != null)
        {
            try {
                bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                imageView.setImageBitmap(bitmap);
//                Bitmap bMap=BitmapFactory.decodeResource(getResources(),R.drawable.faces);
                mat = new Mat();
                Utils.bitmapToMat(bitmap,mat);
                Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGRA2RGB);
//                Utils.matToBitmap(mat,bitmap);
                File f= new File(getCacheDir()+"/yolov5s-face.onnx");

                if(!f.exists())try {
                    InputStream is=getAssets().open("yolov5s-face.onnx");
                    int size=is.available();
                    byte[] bufer=new byte[size];
                    is.read(bufer);
                    is.close();

                    FileOutputStream fos=new FileOutputStream(f);
                    fos.write(bufer);
                    fos.close();
                }catch (Exception e){
                    Log.i("Exception","failed to read model file");
                }
                Face_detection yolo=new Face_detection(f.getPath(),0.45f,0.3f);
                Bitmap frame=yolo.detect_face(mat,MainActivity.this);

                 if(frame != null)
                 {
                     imageView.setImageBitmap(frame);
                 }
                 //Log.i("size of data",String.valueOf(outs.get(0).size()));



            } catch (Exception e) {
                Log.i("Exception",e.getMessage());
            }

        }
    }




}


