package org.perfit.fitness.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.perfit.fitness.R;
import org.perfit.fitness.activities.MoreComing;
import org.perfit.fitness.activities.SessionManager;
import org.perfit.fitness.activities.TotalScoreDisplay;
import org.perfit.fitness.googlemodel.Keypoint;
import org.perfit.fitness.tflite.SkeletonPoint;
import org.perfit.fitness.utilities.AppController;
import org.perfit.fitness.utilities.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

import pl.droidsonroids.gif.GifImageView;

public class Squat extends Exercise {

    private final int squatExId;
    private final SessionManager sessionManager;

    private final int SQUATS_UNKNOWN = 0;
    private final int SQUATS_UP = 1;
    private final int SQUATS_DOWN = 2;

    private int curState = -1;
    private int prevPose;
    // Flags added by Sai - End

    private final ConstraintLayout detectingLayout;
    private final GifImageView workouts;
    private final TextView exerciseName_skel;
    private final Chronometer timer;

    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private float currentRollValue = -999;

    private int checkSingle = 0;

    private String correctionText = "";
    private final TextView beginStatus;

    private View rootView;
    String fullString = "";

    public Squat(Activity classifierActivity, int exerciseIdsquat) {
        super(classifierActivity);
        rotationDegree = 0;
        squatExId = exerciseIdsquat;

        sessionManager = new SessionManager(activity.getApplicationContext());
        detectingLayout = activity.findViewById(R.id.detecting_layout);
        workouts = activity.findViewById(R.id.workoutGifs);
        exerciseName_skel = activity.findViewById(R.id.exercise_name);
        timer = activity.findViewById(R.id.timeValue);
        beginStatus = activity.findViewById(R.id.feedbackBegin);
        GifImageView exerciseIcon = activity.findViewById(R.id.exerciseiconLooking);
        GifImageView exerciseIconInexercise = activity.findViewById(R.id.exerciseIcontoShow);
        exerciseIcon.setImageResource(R.drawable.squat_gif_whitebg);
        exerciseIconInexercise.setImageResource(R.drawable.squat_gif_blackbg);

        String path = Environment.getExternalStorageDirectory() + File.separator + "PerfitLogs";
        File folder = new File(path);
        folder.mkdir();

        // Used by parent onStart to speak
        introSpeech = "Get ready to do squats";

    }

    private float scoreCalculator(int count) {
        float[][] ageConstList = {{14.76601f, 13.89742f, 12.594539f, 11.2916565f, 9.988773f},
                {12.594539f, 11.291656f, 9.98877f, 8.685889f, 7.383006f}};
        float result = (float) (10 * (1 - Math.exp(-(count / ageConstList[0][0]))));
        return result;
    }

    public void processPoints(SkeletonPoint[] poseKeypoints) {
        super.processPoints(poseKeypoints);

        if (!humanDetected()) {
            if (System.nanoTime() - startGlobalTime > timeLimitBeforeDetection) {
//                saveAndGoHome();
                saveAndGoNext();
            }
            return;
        }

        int curPose = processSinglePose();

        updateStringCheck++;
        if (updateStringCheck >= 10) {
            callAPIDataCollection();
            updateStringCheck = 0;
        }

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
//                Bitmap bitmap = getScreenShot(rootView);
//                store(bitmap, "/screenshot.png");
//            }
//        },0,5000);

        // Human detected twenty times ?

        if (!setupExerciseScreenDone) {
            setupExerciseScreen();
        }

        // Count has not increased for a while
        if ((lastCountIncreasedTime != -1) && (System.nanoTime() - lastCountIncreasedTime > timeLimitAfterDetection)) {
            saveAndGoNext();
        }

        /* Exercise exceeds 3 minutes go back to home
        if((firstCountIncreasedTime != -1) && (System.nanoTime() - firstCountIncreasedTime >
        maxTimeToExercise)) {
            saveAndGoNext();
        } */

        if (curPose == SQUATS_UNKNOWN) {
            return;
        }

        if ((curState != curPose) && (prevPose == curPose)) {
            if (curState == SQUATS_UP) {
                countOrTime++;

                if (firstCountIncreasedTime == -1)
                    firstCountIncreasedTime = System.nanoTime();
                lastCountIncreasedTime = System.nanoTime();

                speakTextWithFrequency(countOrTime + "", 0);
            }
            curState = curPose;
        }
        prevPose = curPose;

    }


    private boolean isPhoneCorrectAngle() {
        if ((currentRollValue > 1.3 && currentRollValue < 1.7) || (currentRollValue < -1.3 && currentRollValue > -1.7)) {
            correctionText = "Phone angle is correct, now correct your position and stance.";
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    beginStatus.setText(correctionText);
                }
            });
            return true;
        } else {
            correctionText = "Your phone is too tilted, ensure it is upright!";
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    beginStatus.setText(correctionText);
                }
            });
            speakTextWithFrequency("Make sure your phone is upright!", 8000000000L);
            return false;
        }
    }

    private boolean humanDetected() {
        if (humanPoseCount >= 10)
            return true;

//        if (!isPhoneCorrectAngle()) {
//            humanPoseCount = 0;
//            return false;
//        }

        if (!isSkeletonEmpty()) {

            if (!isCorrectPosition()) {
                humanPoseCount = 0;
                return false;
            }

            if (!isCorrectStance()) {
                humanPoseCount = 0;
                return false;
            }

            humanPoseCount++;
            return false;

        }
        return false;

    }

    private boolean isCorrectPosition() {
        //TODO: Needs more logic
        if (correctPositionCount > 10)
            return true;
        else if (keypoints[16].getPosition().y >= 0.7 && keypoints[16].getPosition().y <= 0.9) {
            correctionText = "Correct position, Now correct your stance.";
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    beginStatus.setText(correctionText);
                }
            });
            correctPositionCount++;
            return true;
        } else {
            if (keypoints[16].getPosition().y < 0.7) {
                correctionText = "Come closer to the camera";
                speakTextWithFrequency("Come closer", 5000000000L);
            } else if (keypoints[16].getPosition().y > 0.91) {
                correctionText = "Move back a little from the camera";
                speakTextWithFrequency("Move back", 5000000000L);
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    beginStatus.setText(correctionText);
                }
            });
            return false;
        }
    }

    private boolean isCorrectStance() {
        final double hipLength =
                Math.abs(keypoints[11].getPosition().x - keypoints[12].getPosition().x);
        final double toeLength =
                Math.abs(keypoints[15].getPosition().x - keypoints[16].getPosition().x);

        if ((toeLength / hipLength) > 1 && (toeLength / hipLength) < 2.8) {
            correctionText = "Correct, remain in this stance.";
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    beginStatus.setText(correctionText);
                }
            });
            return true;
        } else {
            if ((toeLength / hipLength) > 2.9) {
                correctionText = "Bring your legs closer.";
                speakTextWithFrequency("Bring your legs closer", 5000000000L);
            } else if ((toeLength / hipLength) < 1) {
                correctionText = "Widen your legs.";
                speakTextWithFrequency("Widen your legs", 5000000000L);
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    beginStatus.setText(correctionText);
                }
            });
            return false;
        }

    }

    private void setupExerciseScreen() {
        Handler exerciseBeginDelay = new Handler();
        exerciseBeginDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                startGlobalTime = System.nanoTime();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        speakText("Now, lets start counting Squats");
                        workouts.setBackgroundResource(Utilities.gifList.get(0));
                        exerciseName_skel.setText(Utilities.exerciseName.get(0));
                        lastCountIncreasedTime = System.nanoTime();

                        detectingLayout.setVisibility(View.GONE);
                        setupExerciseScreenDone = true;
                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.start();
                    }
                });
            }
        }, 2000);
    }

    private void saveAndGoHome() {
        addScoreToSession();

        Intent i = new Intent(activity.getApplicationContext(), MoreComing.class);
        activity.startActivity(i);
        activity.finish();
    }

    private void saveAndGoNext() {
        addScoreToSession();
        Intent goAfterSquat = new Intent(activity.getApplicationContext(), TotalScoreDisplay.class);
        Bundle put_exerciseid = new Bundle();
        put_exerciseid.putInt("countOrTime", countOrTime);
        put_exerciseid.putInt("id", squatExId);
//        put_exerciseid.putString("skeletalPointList", printList);
        goAfterSquat.putExtras(put_exerciseid);
        activity.startActivity(goAfterSquat);
    }

    private void addScoreToSession() {
        sessionManager.addSquatScore(scoreCalculator(countOrTime));
        sessionManager.addSquatCount(countOrTime);
        long duration = (System.nanoTime() - startGlobalTime) / 1000000000L;
        int durationInSeconds = (int) duration - 10;
        sessionManager.addSquatTime(durationInSeconds);
    }

    private int processSinglePose() {

        if (isSkeletonEmpty())
            return SQUATS_UNKNOWN;

        final double hipLength =
                Math.abs(keypoints[11].getPosition().x - keypoints[12].getPosition().x);
        final double toeLength =
                Math.abs(keypoints[15].getPosition().x - keypoints[16].getPosition().x);

        final double positiveSlope =
                Math.atan2(keypoints[11].getPosition().x - keypoints[14].getPosition().x,
                        keypoints[14].getPosition().y - keypoints[11].getPosition().y);
        final double negativeSlope =
                Math.atan2(keypoints[12].getPosition().x - keypoints[13].getPosition().x,
                        keypoints[13].getPosition().y - keypoints[12].getPosition().y);

        int squatsStatus = SQUATS_UNKNOWN;

        if (AppController.DEBUG_MODE) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView printval = activity.findViewById(R.id.valueCheckdisp);
                    final DecimalFormat df = new DecimalFormat("0.##");
                    printval.setText(df.format(keypoints[16].getPosition().x) + "," + df.format(keypoints[16].getPosition().y) + "\n" +
                            df.format(keypoints[15].getPosition().x) + "," + df.format(keypoints[15].getPosition().y) + "\n" +
                            df.format(keypoints[11].getPosition().x) + "," + df.format(keypoints[11].getPosition().y) + "\n" +
                            df.format(keypoints[12].getPosition().x) + "," + df.format(keypoints[12].getPosition().y) + "\n" +
                            df.format(keypoints[13].getPosition().x) + "," + df.format(keypoints[13].getPosition().y) + "\n" +
                            df.format(keypoints[14].getPosition().x) + "," + df.format(keypoints[14].getPosition().y) + "\n");

                    for (int i = 0; i < keypoints.length; i++) {
                        printList += "(" + df.format(keypoints[i].getPosition().x) + "," + df.format(keypoints[i].getPosition().y) + "); ";
                    }
                    printList += " / " + correctionText + " / ";

                    printList += "ToeToHipDist = " + df.format(toeLength / hipLength) + ", PositiveSlope = " + df.format(positiveSlope) +
                            ", NegativeSlope = " + df.format(negativeSlope);
                    printList += " || ";

//                    fullString = "";
                    fullString += "Left Foot : (" + df.format(keypoints[16].getPosition().x) + "," + df.format(keypoints[16].getPosition().y) + ")\n" +
                            "RightFoot : (" + df.format(keypoints[15].getPosition().x) + "," + df.format(keypoints[15].getPosition().y) + ")\n" +
                            "RightHip : (" + df.format(keypoints[11].getPosition().x) + "," + df.format(keypoints[11].getPosition().y) + ")\n" +
                            "LeftHip : (" + df.format(keypoints[12].getPosition().x) + "," + df.format(keypoints[12].getPosition().y) + ")\n" +
                            "RightKnee : (" + df.format(keypoints[13].getPosition().x) + "," + df.format(keypoints[13].getPosition().y) + ")\n" +
                            "LeftKnee : (" + df.format(keypoints[14].getPosition().x) + "," + df.format(keypoints[14].getPosition().y) + ")\n";
                    fullString += ";\n";
                    System.out.println(fullString);
                    writeToFile(fullString);
                }
            });
        }

        if ((toeLength / hipLength) > 1 && (toeLength / hipLength) < 2.8) {
            if (positiveSlope < 0.6 && negativeSlope > -0.6) {
                squatsStatus = SQUATS_DOWN; // Down state
            } else if (positiveSlope > 0.7 && negativeSlope < -0.7) {
                squatsStatus = SQUATS_UP; // Up state
            } else {
                squatsStatus = SQUATS_UNKNOWN;
            }
        } else
            squatsStatus = SQUATS_UNKNOWN;

        return squatsStatus;

    }

    private void writeToFile(String data) {
        try {
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "PerfitLogs/" + "Log" + ".txt", true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
