package org.perfit.fitness.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.TextView;

import org.json.JSONObject;
import org.perfit.fitness.R;
import org.perfit.fitness.activities.BaseDashBoard;
import org.perfit.fitness.activities.MoreComing;
import org.perfit.fitness.activities.SessionManager;
import org.perfit.fitness.activities.TotalScoreDisplay;
import org.perfit.fitness.exercise.Exercise;
import org.perfit.fitness.exercise.JumpingJacks;
import org.perfit.fitness.exercise.Plank;
import org.perfit.fitness.exercise.Pushup;
import org.perfit.fitness.exercise.Squat;
import org.perfit.fitness.googlemodel.GooglePoser;
import org.perfit.fitness.posenet.Posenet;
import org.perfit.fitness.stackoverflowmodel.StackOverflowPoser;
import org.perfit.fitness.tflite.SkeletonPoint;
import org.perfit.fitness.tflite.SkeletonPoser;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.AppController;
import org.perfit.fitness.utilities.ImageUploadData;
import org.perfit.fitness.utilities.Logger;
import org.perfit.fitness.utilities.UploadImage;
import org.perfit.fitness.utilities.VolleyCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;


public class ClassifierActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();
    private static Models MODEL_TO_USE = Models.StackOverflow;

    private static int exerciseId;
    private TextView countDisp;
    private TextView tracking;

    private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 960);
    private Bitmap rgbFrameBitmap = null;
    private Bitmap rgbRotatedFrameBitmap = null;
    private final Matrix matrixRotation = new Matrix();
    private SkeletonPoser skeletonPoser;
    private static int userId;

    private static SessionManager clasiifierManager;

    // public Pose pose;
    private SkeletonPoint[] skelPoints;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.skeleton_tracking);

        clasiifierManager = new SessionManager(getApplicationContext());
        TextView exitButon = findViewById(R.id.exitButton);
        TextView exerciseName_skel = findViewById(R.id.exercise_name);
        countDisp = findViewById(R.id.countDisplay);
        GifImageView workouts = findViewById(R.id.workoutGifs);

        HashMap<String,Integer> getUserid = clasiifierManager.getUserId();
        userId = getUserid.get(SessionManager.KEY_USERID);

        Bundle bundle = getIntent().getExtras();
        int gifCode = bundle.getInt("gif_id_code");
        exerciseId = bundle.getInt("exercise_value");
//        exerciseId = 0;
        Chronometer timerDisp = findViewById(R.id.plankTimer);
        tracking = findViewById(R.id.tracking);

        Exercise.getUserID = userId;
        Exercise.currentExerciseId =exerciseId;

        switch (exerciseId) {
            case 0:
                MODEL_TO_USE = Models.StackOverflow;

                timerDisp.setVisibility(View.GONE);
                countDisp.setVisibility(View.VISIBLE);
                clasiifierManager.initializeAllCounts();
                exercise = new Squat(this, exerciseId);
                break;
            case 1:
                MODEL_TO_USE = Models.StackOverflow;

                timerDisp.setVisibility(View.GONE);
                countDisp.setVisibility(View.VISIBLE);
                exercise = new Pushup(this, exerciseId);
                break;
            case 2:
                MODEL_TO_USE = Models.StackOverflow;

                timerDisp.setVisibility(View.VISIBLE);
                countDisp.setVisibility(View.GONE);
                exercise = new Plank(this, exerciseId);
                break;
            case 3:
                MODEL_TO_USE = Models.StackOverflow;

                timerDisp.setVisibility(View.VISIBLE);
                countDisp.setVisibility(View.GONE);
                exercise = new JumpingJacks(this, exerciseId);
                break;
            default:
                Intent exitIntentMore = new Intent(getApplicationContext(), MoreComing.class);
                startActivity(exitIntentMore);
                finish();
                break;
        }

        exitButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ClassifierActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), BaseDashBoard.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        Handler takeImage = new Handler();
        takeImage.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(rgbFrameBitmap, 225, 225, false);
                store(resizedBitmap, userId + "_" + exerciseId + "_" + sdf.format(new Date())+ ".png");
            }
        },5000);

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ClassifierActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), BaseDashBoard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
            } else if (MODEL_TO_USE == Models.StackOverflow) {
                skeletonPoser = new StackOverflowPoser(this);
            } else {
                skeletonPoser = new Posenet(this);
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

        Integer sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);
        matrixRotation.postRotate(exercise.rotationDegree);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);

        setCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        showResult(canvas);
                    }
                });
    }

    private void showResult(Canvas canvas) {
        if (skelPoints != null) {
            skeletonDrawingView.drawSkeleton(canvas, skelPoints, MODEL_TO_USE, exerciseId);
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
//                                long testTimeEnd = System.nanoTime();
//                                long inNanoSecond = (testTimeEnd - startTime);
//                                Toast.makeText(ClassifierActivity.this, String.valueOf(inNanoSecond), Toast.LENGTH_SHORT).show();
                            } else {
                                // Bitmap photo = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.plank);
                                // skelPoints = skeletonPoser.skeletonImage(photo);
                            }
                            long endTime = System.nanoTime();
                            long MethodeDuration = (endTime - startTime);
                            Exercise.debugImageProcessTime += MethodeDuration + "/";


                            LOGGER.d("Time taken for pose estimation : " + MethodeDuration);

                            if (AppController.DEBUG_MODE) {
                                requestRender();
                            }
                            exercise.processPoints(skelPoints);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (exercise.isSkeletonEmpty()) {
                                        tracking.setText("Not Tracking");
                                        tracking.setTextColor(Color.RED);
                                    } else {
                                        tracking.setText("Tracking");
                                        tracking.setTextColor(Color.GREEN);
                                    }

                                    countDisp.setText(String.valueOf(exercise.countOrTime));
                                }
                            });

                            readyForNextImage();
                        }
                    }
                });
        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                countDisp.setText(String.valueOf(exercise.countOrTime));
                if (exerciseId != 2 && exercise.voiceFlag) {
                    speakCount.speak(String.valueOf(exercise.countOrTime), TextToSpeech.QUEUE_FLUSH, null);
                    exercise.voiceFlag = false;
                }else
                {
                    if (exercise.plankVoiceFlag){
                        speakCount.speak(String.valueOf(exercise.plankCountVoice),TextToSpeech.QUEUE_FLUSH,null);
                        exercise.plankVoiceFlag = false;
                    }
                }

            }
        });
        */
    }

    private static File store(Bitmap bm, String fileName) {
        String dirPath = Environment.getExternalStorageDirectory().toString() + "/PerfitFitness";
        File dir = new File(dirPath);
        File screenshotFile;
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            screenshotFile = new File(dir, fileName);
            screenshotFile.createNewFile();
            // if (file.exists ()) file.delete ();
            // file.mkdirs();

            FileOutputStream fOut = new FileOutputStream(screenshotFile);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();

            switch (exerciseId){
                case 0:
                    SessionManager.KEY_SQUATS_IMAGEPATH = screenshotFile.getPath();
                    break;
                case 1:
                    SessionManager.KEY_PUSHUP_IMAGEPATH = screenshotFile.getPath();
                    break;
                case 2:
                    SessionManager.KEY_PLANK_IMAGEPATH = screenshotFile.getPath();
                    break;
                case 3:
                    SessionManager.KEY_JUMPING_IMAGEPATH = screenshotFile.getPath();
                    break;
                default:
                    Log.e("Error: ", "Exited store Switch case");
                    break;
            }

            return screenshotFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public synchronized void onDestroy() {
        skeletonPoser.close();
        if (exercise.textToSpeech != null){
            exercise.textToSpeech.stop();
            exercise.textToSpeech.shutdown();
        }
        super.onDestroy();
    }

}
