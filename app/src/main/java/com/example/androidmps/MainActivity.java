package com.example.androidmps;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Contour;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import java.util.List;
import android.graphics.PointF;
import android.graphics.Bitmap;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final double CANNY_THRESHOLD_RATIO = .2; //Suggested range .2 - .4
    private static final int CANNY_STD_DEV = 1;
    public static int filename = R.drawable.showing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button showImage = (Button) findViewById(R.id.button1);
        showImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable=true;
                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        filename,
                        options);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);
                myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
            }

        });

        Button cartoon = (Button) findViewById(R.id.buttonCartoon);
        cartoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable=true;

                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        filename,
                        options);

                CartoonFilter cf = new CartoonFilter();
                myBitmap = cf.getCartoonImage(myBitmap);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(myBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                myImageView.setImageDrawable(new BitmapDrawable(getResources(),myBitmap));
            }
        });

        Button oldFilter = (Button) findViewById(R.id.buttonOldFilter);
        oldFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable=true;

                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        filename,
                        options);

                OldFilter of = new OldFilter();
                myBitmap = of.changeToOld(myBitmap);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                myImageView.setImageDrawable(new BitmapDrawable(getResources(),myBitmap));
            }
        });

        Button cannyEdge = (Button) findViewById(R.id.buttonCannyEdge);
        cannyEdge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable=true;

                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        filename,
                        options);

                Bitmap output = JCanny.CannyEdges(myBitmap, CANNY_STD_DEV, CANNY_THRESHOLD_RATIO);
                myBitmap = output;

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(myBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                myImageView.setImageDrawable(new BitmapDrawable(getResources(),myBitmap));
            }
        });

        Button flowerFilter = (Button) findViewById(R.id.buttonFlower);
        flowerFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable=true;

                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        filename,
                        options);
                Bitmap copyBitmap = myBitmap;

                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(5);
                myRectPaint.setColor(Color.RED);
                myRectPaint.setStyle(Paint.Style.STROKE);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                FaceDetector faceDetector = new
                        FaceDetector.Builder(getApplicationContext()).setProminentFaceOnly(true).setMode(FaceDetector.SELFIE_MODE).setLandmarkType(FaceDetector.CONTOUR_LANDMARKS)
                        .build();
                if(!faceDetector.isOperational()){
                    new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                    return;
                }

                Frame frame = new Frame.Builder().setBitmap(copyBitmap).build();
                SparseArray<Face> faces = faceDetector.detect(frame);
                Bitmap flower = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.flower_crown,
                        options);

                for(int i=0; i<faces.size(); i++) {
                    Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    List<Landmark> lmarks = thisFace.getLandmarks();
                    List<Contour> contours = thisFace.getContours();
                    for (int j = 0; j < contours.size(); j++) {
                        PointF[] pts = contours.get(j).getPositions();
                        if (contours.get(j).getType() == 1) {

                            tempCanvas.drawLine(pts[0].x, pts[0].y, pts[pts.length - 1].x, pts[pts.length - 1].y, myRectPaint);
                            tempCanvas.drawBitmap(flower, (pts[0].x + pts[pts.length - 1].x) / 2 - flower.getWidth() / 2, (pts[0].y + pts[pts.length - 1].y) / 2 - flower.getHeight() / 2, new Paint());

                        }
                    }
                }

                myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
            }
        });

        Button dogFaceFilter = (Button) findViewById(R.id.buttonDogFace);
        dogFaceFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable=true;

                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        filename,
                        options);
                Bitmap copyBitmap = myBitmap;

                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(5);
                myRectPaint.setColor(Color.RED);
                myRectPaint.setStyle(Paint.Style.STROKE);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                FaceDetector faceDetector = new
                        FaceDetector.Builder(getApplicationContext()).setProminentFaceOnly(true).setMode(FaceDetector.SELFIE_MODE).setLandmarkType(FaceDetector.CONTOUR_LANDMARKS)
                        .build();
                if(!faceDetector.isOperational()){
                    new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                    return;
                }


                Frame frame = new Frame.Builder().setBitmap(copyBitmap).build();
                SparseArray<Face> faces = faceDetector.detect(frame);
                Bitmap dogLeftEar = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.dog_left_ear,
                        options);
                Bitmap dogRightEar = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.dog_right_ear,
                        options);
                Bitmap dogNose = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.dog_nose,
                        options);

                Bitmap dogTongue = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.dog_tongue,
                        options);

                for(int i=0; i<faces.size(); i++) {
                    Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    List<Landmark> lmarks = thisFace.getLandmarks();
                    List<Contour> contours = thisFace.getContours();
                    for (int j = 0 ; j < contours.size(); j++) {
                        PointF[] pts = contours.get(j).getPositions();
                        if(contours.get(j).getType() == 1) {
                            tempCanvas.drawBitmap(dogLeftEar, pts[pts.length  -   3].x - dogLeftEar.getWidth()/2, pts[pts.length  -   3].y - dogLeftEar.getHeight(), new Paint());
                            tempCanvas.drawBitmap(dogRightEar, pts[3].x , pts[3].y - dogRightEar.getHeight(), new Paint());
                        }

                        if(contours.get(j).getType()== 12) {
                            tempCanvas.drawBitmap(dogNose, pts[0].x - dogNose.getWidth()/2 , pts[pts.length - 1].y - dogNose.getHeight() / 2, new Paint());
                        }

                        if(contours.get(j).getType()== 10) {
                            tempCanvas.drawBitmap(dogTongue, pts[pts.length / 2 - 1].x - dogTongue.getWidth() / 2 - 10 , pts[pts.length / 2].y , new Paint());
                        }
                    }
                }

                myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
            }
        });

        Button faceDetection = (Button) findViewById(R.id.buttonFaceDetection);
        faceDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable=true;

                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        filename,
                        options);
                Bitmap copyBitmap = myBitmap;

                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(5);
                myRectPaint.setColor(Color.RED);
                myRectPaint.setStyle(Paint.Style.STROKE);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                FaceDetector faceDetector = new
                        FaceDetector.Builder(getApplicationContext()).setProminentFaceOnly(true).setMode(FaceDetector.SELFIE_MODE).setLandmarkType(FaceDetector.CONTOUR_LANDMARKS)
                        .build();
                if(!faceDetector.isOperational()){
                    new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                    return;
                }


                Frame frame = new Frame.Builder().setBitmap(copyBitmap).build();
                SparseArray<Face> faces = faceDetector.detect(frame);


                for(int i=0; i<faces.size(); i++) {
                    Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    List<Landmark> lmarks = thisFace.getLandmarks();
                    List<Contour> contours = thisFace.getContours();
                    for (int j = 0 ; j < contours.size(); j++) {
                        PointF[] pts = contours.get(j).getPositions();
                        for (int k = 0; k < pts.length -1; k++) {
                            tempCanvas.drawLine(pts[k].x, pts[k].y, pts[k+1].x, pts[k+1].y, myRectPaint);
                        }
                        if(contours.get(j).getType() == 1)
                            tempCanvas.drawLine(pts[pts.length-1].x, pts[pts.length-1].y, pts[0].x, pts[0].y, myRectPaint);
                    }
                }

                myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
            }
        });

        /*
        @Override
            public void onClick(View v) {
                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable=true;
                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.test1,
                        options);
                Bitmap copyBitmap = myBitmap;
                CartoonFilter cf = new CartoonFilter();
                //OldFilter of = new OldFilter();
                //Bitmap output = JCanny.CannyEdges(myBitmap, CANNY_STD_DEV, CANNY_THRESHOLD_RATIO);
                myBitmap = cf.getCartoonImage(myBitmap);
                //myBitmap = of.changeToOld(myBitmap);
                //myBitmap = output;
                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(5);
                myRectPaint.setColor(Color.RED);
                myRectPaint.setStyle(Paint.Style.STROKE);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                FaceDetector faceDetector = new
                        FaceDetector.Builder(getApplicationContext()).setProminentFaceOnly(true).setMode(FaceDetector.SELFIE_MODE).setLandmarkType(FaceDetector.CONTOUR_LANDMARKS)
                        .build();
                if(!faceDetector.isOperational()){
                    new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                    return;
                }

                Frame frame = new Frame.Builder().setBitmap(copyBitmap).build();
                SparseArray<Face> faces = faceDetector.detect(frame);
                Bitmap flower = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.flower_crown,
                        options);
                Bitmap dogLeftEar = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.dog_left_ear,
                        options);
                Bitmap dogRightEar = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.dog_right_ear,
                        options);
                Bitmap dogNose = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.dog_nose,
                        options);

                Bitmap dogTongue = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.dog_tongue,
                        options);

                for(int i=0; i<faces.size(); i++) {
                    Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    List<Landmark> lmarks = thisFace.getLandmarks();
                    List<Contour> contours = thisFace.getContours();
                    for (int j = 0 ; j < contours.size(); j++) {
                        PointF[] pts = contours.get(j).getPositions();
                        if(contours.get(j).getType() == 1) {
                            //tempCanvas.drawBitmap(dogLeftEar, 0, 0, new Paint());
                            //tempCanvas.drawLine(pts[pts.length  -   3].x, pts[pts.length -  3].y, pts[pts.length  -  2].x, pts[pts.length  -  2].y, myRectPaint);
                            tempCanvas.drawBitmap(dogLeftEar, pts[pts.length  -   3].x - dogLeftEar.getWidth()/2, pts[pts.length  -   3].y - dogLeftEar.getHeight(), new Paint());
                            tempCanvas.drawBitmap(dogRightEar, pts[3].x , pts[3].y - dogRightEar.getHeight(), new Paint());

                            //flower
                            //tempCanvas.drawLine(pts[0].x, pts[0].y, pts[pts.length -1].x, pts[pts.length -1].y, myRectPaint);
                            //tempCanvas.drawBitmap(flower, (pts[0].x + pts[pts.length -1].x)/2 - flower.getWidth()/2, (pts[0].y + pts[pts.length -1].y) / 2 - flower.getHeight()/2 , new Paint());

                        }

                        if(contours.get(j).getType()== 12) {
                            tempCanvas.drawBitmap(dogNose, pts[0].x - dogNose.getWidth()/2 , pts[pts.length - 1].y - dogNose.getHeight() / 2, new Paint());
                        }

                        if(contours.get(j).getType()== 10) {
                            tempCanvas.drawBitmap(dogTongue, pts[pts.length / 2 - 1].x - dogTongue.getWidth() / 2 - 10 , pts[pts.length / 2].y , new Paint());
                        }
                        /*for (int k = 0; k < pts.length -1; k++) {
                            tempCanvas.drawLine(pts[k].x, pts[k].y, pts[k+1].x, pts[k+1].y, myRectPaint);
                        }
                        if(contours.get(j).getType() == 1)
                            tempCanvas.drawLine(pts[pts.length-1].x, pts[pts.length-1].y, pts[0].x, pts[0].y, myRectPaint);

                    }
                }
        myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
    }*/

    }
}
