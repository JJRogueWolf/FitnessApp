package com.perfit.jobin.exercisetestingapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClassifierActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();
    private static final Models MODEL_TO_USE = Models.StackOverflow;

    private int gifCode, exerciseId;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
//    private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 960);
    //private static final Size DESIRED_PREVIEW_SIZE = new Size(2160, 3840);
    private Bitmap rgbFrameBitmap = null;
    private Bitmap rgbRotatedFrameBitmap = null;
    private Matrix matrixRotation = new Matrix();
    private Integer sensorOrientation;
    private SkeletonPoser skeletonPoser;


    // public Pose pose;
    public SkeletonPoint[] skelPoints;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.skeleton_tracking);

        Bundle bundle = getIntent().getExtras();
        gifCode = bundle.getInt("gif_id_code");
        exerciseId = bundle.getInt("exercise_value");
//    tracking = findViewById(R.id.tracking);
//    exitX = findViewById(R.id.exit);
//    exitX.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        Intent exitExercise = new Intent(getApplicationContext(),MainActivity.class);
//        startActivity(exitExercise);
//        finish();
//      }
//    });

        switch (exerciseId) {
            case 0:
                exercise = new Pushup(this, exerciseId);
                break;
            case 1:
                exercise = new Squat(this, exerciseId);
                break;
            case 2:
                exercise = new Plank(this, exerciseId);
                break;
            case 3:
                exercise = new Flex(this, exerciseId);
                break;
            default:
                break;
        }

        super.onCreate(savedInstanceState);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {

        try {
            if (MODEL_TO_USE == Models.Google) {
                skeletonPoser = new GooglePoser(this);
            } else {
                skeletonPoser = new StackOverflowPoser(this);
            }
        } catch (IOException e) {
            LOGGER.e(e, "Failed to create classifier.");
        }

        if (skeletonPoser == null) {
            LOGGER.e("No classifier on preview!");
            return;
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);
        matrixRotation.postRotate(exercise.rotationDegree);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);

        setCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        showResult(canvas, size);
                    }
                });
    }

    protected void showResult(Canvas canvas, Size cameraSize) {
        if (skelPoints != null) {
            skeletonDrawingView.drawSkeleton(canvas, skelPoints, MODEL_TO_USE);
        }
    }

    @Override
    protected void processImage() {
        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {

                        if (skeletonPoser != null) {
                            final long startTime = System.nanoTime();
                            int USE_CAMERA = 1;
                            if (USE_CAMERA == 1) {
                                rgbRotatedFrameBitmap = Bitmap.createBitmap(rgbFrameBitmap, 0, 0, rgbFrameBitmap.getWidth(), rgbFrameBitmap.getHeight(), matrixRotation, true);
                                skelPoints = skeletonPoser.skeletonImage(rgbRotatedFrameBitmap);
                            } else {
                                Bitmap photo = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plank1);
                                skelPoints = skeletonPoser.skeletonImage(photo);
                            }
                            Log.d("skel", String.valueOf(skelPoints));
                            String skel = new Gson().toJson(skelPoints);
                            requestRender();
                            exercise.processPoints(skelPoints);
                            readyForNextImage();

                            long endTime = System.nanoTime();
                            long MethodeDuration = (endTime - startTime);
                            LOGGER.d("Time taken for pose estimation : " + MethodeDuration);
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent(ClassifierActivity.this,MainActivity.class);
        startActivity(goBack);
        finish();
    }
}


//}
