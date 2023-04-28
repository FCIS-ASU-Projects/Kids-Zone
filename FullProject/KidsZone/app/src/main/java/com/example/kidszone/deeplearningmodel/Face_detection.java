package com.example.kidszone.deeplearningmodel;

import static org.opencv.calib3d.Calib3d.estimateAffine2D;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.dnn.Dnn.DNN_BACKEND_CUDA;
import static org.opencv.dnn.Dnn.DNN_TARGET_CUDA;
import static org.opencv.dnn.Dnn.NMSBoxes;
import static org.opencv.dnn.Dnn.blobFromImage;
import static org.opencv.dnn.Dnn.readNet;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class Face_detection {
    private String modelPath="";
    private static float [][] anchors = { {4.f,5.f,  8.f,10.f,  13.f,16.f}, {23.f,29.f,  43.f,55.f,  73.f,105.f},{146.f,217.f,  231.f,300.f,  335.f,433.f} };
    private static float [] stride = { 8.0f, 16.0f, 32.0f };
    private float nmsthreshold=0.45f,confthreshold=0.3f;
    private static int inpWidth = 640,inpHeight = 640;

    private ArrayList<Float> confidences=new ArrayList<Float>();
    private ArrayList<Rect2d> boxes=new ArrayList<>();
    private ArrayList< ArrayList<Integer>> landmarks=new ArrayList<>();

    public Face_detection(String modelPath,float nmsthreshold,float confthreshold)
    {
        this.modelPath=modelPath;
        this.nmsthreshold=nmsthreshold;
        this.confthreshold=confthreshold;
    }
    private ArrayList<Mat> load_model(Mat frame, Context activity)
    {

        ArrayList<Mat> outs = new ArrayList<>();

        try {
            Mat blob = blobFromImage(frame, 1 / 255.0f , new Size(640, 640), new Scalar(0, 0, 0), true, false);

            Net net = readNet(modelPath);
            net.setPreferableBackend(DNN_BACKEND_CUDA);
            net.setPreferableTarget(DNN_TARGET_CUDA);
            net.setInput(blob);

            net.forward(outs, net.getUnconnectedOutLayersNames());
//            Log.i("success","Load onnx model successfully");
        }catch (Exception e)
        {
            Log.i("Exception","can't load onnx model");
            return null;
        }
        return outs;
    }

    private Bitmap process_output(Mat frame, Mat outs)
    {


        outs=outs.reshape(1,(int)outs.total() / 16 );

        /////generate proposals

        float ratioh = (float)frame.height() / inpHeight, ratiow = (float)frame.width() / inpWidth;
        int n = 0, q = 0, i = 0, j = 0, nout = 16, row_ind = 0, k = 0; ///xmin,ymin,xamx,ymax,box_score,x1,y1, ... ,x5,y5,face_score
        for (n = 0; n < 3; n++)   /// Feature Map Scale
        {
            int num_grid_x = (int)(inpWidth / stride[n]);
            int num_grid_y = (int)(inpHeight / stride[n]);
            for (q = 0; q < 3; q++)    ///anchor
            {
                float anchor_w = anchors[n][q * 2];
                float anchor_h = anchors[n][q * 2 + 1];
                for (i = 0; i < num_grid_y; i++)
                {
                    for (j = 0; j < num_grid_x; j++)
                    {
                        int c_idx=row_ind * nout;
                        float box_score = sigmoid_x((float)outs.get(row_ind,4)[0]);
                        //float conf=box_score*face_score;

                        if (box_score > confthreshold)
                        {

                            float cx = (sigmoid_x((float)outs.get(row_ind,0)[0]) * 2.f - 0.5f + j) * stride[n];  ///cx
                            float cy = (sigmoid_x((float)outs.get(row_ind,1)[0]) * 2.f - 0.5f + i) * stride[n];   ///cy
                            float w = (float)Math.pow(sigmoid_x((float)outs.get(row_ind,2)[0]) * 2.f, 2.f) * anchor_w;   ///w
                            float h = (float)Math.pow(sigmoid_x((float)outs.get(row_ind,3)[0]) * 2.f, 2.f) * anchor_h;  ///h


                            int left = (int)((cx - 0.5*w) * ratiow);
                            int top = (int)((cy - 0.5*h)*ratioh);
                            float face_score=sigmoid_x((float)outs.get(row_ind,15)[0]);
                            confidences.add(face_score);

                            boxes.add(new Rect2d(left, top, (int)(w*ratiow), (int)(h*ratioh)));

                            ArrayList<Integer> landmark=new ArrayList<>();
                            for (k = 5; k < 15; k+=2)
                            {
                                //int ind = k - 5;
                                landmark.add((int) ((outs.get(row_ind,k)[0] * anchor_w + j * stride[n]) * ratiow));
                                landmark.add((int) ((outs.get(row_ind,k+1)[0] * anchor_h + i * stride[n]) * ratioh));
                            }
                            landmarks.add(landmark);

                        }
                        row_ind++;
                    }
                }
            }
        }

        // Perform non maximum suppression to eliminate redundant overlapping boxes with
        // lower confidences
        Bitmap cropedImg=null;
        if(!confidences.isEmpty())
            cropedImg=nms(frame);



        return cropedImg;
    }

    private Bitmap nms(Mat frame)
    {
        // Perform non maximum suppression to eliminate redundant overlapping boxes with
        // lower confidences
//        if(confidences.isEmpty())
//           Log.i("check size","emptyyyy");

        MatOfFloat confidence = new MatOfFloat(Converters.vector_float_to_Mat(confidences));
        MatOfInt indices = new MatOfInt();
        MatOfRect2d bbox = new MatOfRect2d();
        bbox.fromList(boxes);
        NMSBoxes(bbox, confidence, confthreshold, nmsthreshold, indices);
        int[] indices_arr = indices.toArray();
        float maxiod=-1;
        int max_indx=-1;
        for (int ind = 0; ind < indices_arr.length; ++ind)
        {
            int idx = indices_arr[ind];
            Log.i("in loop","nms");
            //Rect2d box = boxes.get(idx);
            ArrayList<Integer> lm= landmarks.get(idx);
            float IOD = Math.abs((lm.get(0) + lm.get(1) / 2.0f) - (lm.get(3) + lm.get(4) / 2.0f));
            if(IOD > maxiod)
            {
                maxiod=IOD;
                max_indx=idx;

            }

        }
        if(max_indx != -1)
        {
            //Rect2d box = boxes.get(max_indx);

            Mat image=alignFaces(frame,  landmarks.get(max_indx));
            Bitmap bp=convertMatToBitMap(image);
            //Utils.matToBitmap(image,frame);
            //Bitmap cropImg = Bitmap.createBitmap(frame, (int)box.x, (int)box.y,(int)box.width, (int)box.height);
            return bp;
        }
        return null;
    }
    static float sigmoid_x(float x)
    {
        return 1.f / (1.f + (float)Math.exp(-x));
    }
    private Mat alignFaces(Mat frame, ArrayList<Integer> landmarks)
    {
        Mat src=new Mat(5,2,CV_32F);
        float[] srcTri = {38.2946f, 51.6963f,
                73.5318f, 51.5014f,
                56.0252f, 71.7366f ,
                41.5493f, 92.3655f,
                70.7299f, 92.2041f};
        src.put(0,0,srcTri);


        Mat dst=new Mat(5,2,CV_32F);
        float[] dstTri={landmarks.get(0), landmarks.get(1)
                , landmarks.get(2), landmarks.get(3),
                landmarks.get(4), landmarks.get(5),
                landmarks.get(6), landmarks.get(7),
                landmarks.get(8), landmarks.get(9)};
        dst.put(0,0,dstTri);


        Mat warpMat = estimateAffine2D(dst, src);
        Mat warpDst = Mat.zeros( frame.rows(), frame.cols(), frame.type() );
        Imgproc.warpAffine( frame, warpDst, warpMat, new Size(112,112) );

        return warpDst;
    }
    public Bitmap convertMatToBitMap(Mat input){
        Bitmap bmp = null;
        Mat rgb = new Mat();
        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGB);

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgb, bmp);
            Log.i("converted","success convertMatToBitMap");
        }
        catch (Exception e){
            Log.d("falid convertMatToBitMap",e.getMessage());
        }
        return bmp;
    }
    public Bitmap detect_face(Mat inputImg,Context activity)
    {
        ArrayList<Mat> outs=load_model(inputImg,activity);
        Bitmap detectImg=process_output(inputImg,outs.get(0));
        return detectImg;
    }
}
