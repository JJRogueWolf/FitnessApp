package org.perfit.fitness.exercise;

import android.app.Activity;
import android.content.Intent;
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
import org.perfit.fitness.tflite.SkeletonPoint;
import org.perfit.fitness.utilities.AppController;
import org.perfit.fitness.utilities.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class Plank extends Exercise {

    private final SessionManager sessionManager;
    private final int plankExId;
    private boolean timerRunning = false;
    private int checkSingle = 0;
    private long mLastStopTime = 0;

    private final int PLANK_UNKNOWN = 0;
    private final int PLANK_UP = 1;

    private int curState = -1;
    private int prevPose;
    private String correctionText = "";
    private final TextView beginStatus;

    private final ConstraintLayout detectingLayout;
    private final GifImageView workouts;
    private final TextView exerciseNameText;
    private final Chronometer timer;
    private String plankPrintList;

    String currentDateandTime = "";

    public Plank(Activity classifierActivity, int exerciseIdplank) {
        super(classifierActivity);
        timeLimitAfterDetection = 3000000000L;

        rotationDegree = 90;
        plankExId = exerciseIdplank;
        startGlobalTime = System.nanoTime();
        sessionManager = new SessionManager(activity.getApplicationContext());

        detectingLayout = activity.findViewById(R.id.detecting_layout);
        workouts = activity.findViewById(R.id.workoutGifs);
        exerciseNameText = activity.findViewById(R.id.exercise_name);
        timer = activity.findViewById(R.id.timeValue);
        mChronometer = activity.findViewById(R.id.plankTimer);
        beginStatus = activity.findViewById(R.id.feedbackBegin);
        GifImageView exerciseIcon = activity.findViewById(R.id.exerciseiconLooking);
        GifImageView exerciseIconInExercise = activity.findViewById(R.id.exerciseIcontoShow);
        exerciseIcon.setImageResource(R.drawable.plank_gif_whitebg);
        exerciseIconInExercise.setImageResource(R.drawable.plank_gif_blackbg);

        String path = Environment.getExternalStorageDirectory() + File.separator + "PerfitLogs";
        // Create the folder.
        File folder = new File(path);
        folder.mkdir();

        SimpleDateFormat sdf = new SimpleDateFormat("ddMM-HHmm", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());

        // Used by parent onStart to speak
        introSpeech = "Get into Plank pose";

    }

    private float scoreCalculator(long duration) {
        return (float) (10 * (1 - Math.exp(-0.5f * duration / 153.34)));
    }

    private void setupExerciseScreen() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        speakText("Now try to hold that plank pose!");
                        detectingLayout.setVisibility(View.GONE);
                        workouts.setBackgroundResource(Utilities.gifList.get(2));

                        exerciseNameText.setText(Utilities.exerciseName.get(2));
                        lastCountIncreasedTime = System.nanoTime();
                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.start();
                        startChronometer();
                        setupExerciseScreenDone = true;
                    }
                });
            }
        }, 2000);
    }

    public void processPoints(SkeletonPoint[] poseKeypoints) {
        super.processPoints(poseKeypoints);

        int curPose = processSinglePose();

        updateStringCheck++;
        if (updateStringCheck >= 10){
            callAPIDataCollection();
            updateStringCheck = 0;
        }

        if (!humanDetected(curPose)) {
            if (System.nanoTime() - startGlobalTime > timeLimitBeforeDetection) {
//                saveAndGoHome();
                saveAndGoNext();
            }
            return;
        }

        if (!setupExerciseScreenDone) {
            setupExerciseScreen();
            return;
        }

        // Count has not increased for a while
        if ((lastCountIncreasedTime != -1) && ((System.nanoTime() - lastCountIncreasedTime) > timeLimitAfterDetection)) {
            saveAndGoNext();
        }


        /* Exercise exceeds 3 minutes go back to home
        if((firstCountIncreasedTime != -1) && (System.nanoTime() - firstCountIncreasedTime >
        maxTimeToExercise)) {
            saveAndGoNext();
        } */

        if (curPose == PLANK_UNKNOWN) {
            // Count has not increased for a bit, needs encouragement
//            if((lastCountIncreasedTime != -1) && (System.nanoTime() - lastCountIncreasedTime <=
//            timeLimitBeforeEncouragement)) {
//                speakText("Now, try to maintain that Plank pose");
//            }
            if ((lastCountIncreasedTime != -1) && (System.nanoTime() - lastCountIncreasedTime >= timeLimitAfterDetection)) {
                saveAndGoNext();
            }

            prevPose = curPose;
            return;
        }

        // curPose is PLANK_UP
        if ((prevPose == curPose)) {

            if (firstCountIncreasedTime == -1)
                firstCountIncreasedTime = System.nanoTime();
            lastCountIncreasedTime = System.nanoTime();

//            countOrTime =
//                    (int) ((lastCountIncreasedTime - firstCountIncreasedTime) / 1000000000L) ;

            countOrTime = (int) ((SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000);

            if ((countOrTime > 0) && (countOrTime % 5 == 0)) {
                speakText(countOrTime + " seconds");
            }
        }
        prevPose = curPose;

    }

    private boolean isCorrectPosition() {
        //TODO: Needs more logic
        if (correctPositionCount > 20)
            return true;
        else if (keypoints[16].getPosition().x > 0.2 && keypoints[16].getPosition().x < 0.4) {
            correctionText = "Correct position, now correct your stance.";
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    beginStatus.setText(correctionText);
                }
            });
            correctPositionCount++;
            return true;
        } else {
            if (keypoints[16].getPosition().x < 0.2) {
                speakTextWithFrequency("Move back", 5000000000L);
                correctionText = "Move back a little from the camera";
                speakText("Move back");
            } else if (keypoints[16].getPosition().x > 0.4) {
                speakTextWithFrequency("Come closer", 5000000000L);
                correctionText = "Come closer to the camera";
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
        //TODO: Needs more logic
        return true;
    }

    private boolean humanDetected(int curPose) {
        if (humanPoseCount >= 5)
            return true;

        if (!isSkeletonEmpty()) {

//            if (!isCorrectPosition()) {
//                humanPoseCount = 0;
//                return false;
//            }

            if (curPose == PLANK_UP) {
//                startChronometer();

                humanPoseCount++;
                return false;
            }

        }
        return false;

    }

    private void startChronometer() {
        if (!timerRunning) {
            mChronometer.setBase(SystemClock.elapsedRealtime() - mLastStopTime);
            mChronometer.start();
            timerRunning = true;
        }
    }

    // TODO: Save score information before going to next activity
    private void saveAndGoHome() {
        addScoreToSession();

        Intent i = new Intent(activity.getApplicationContext(), MoreComing.class);
        activity.startActivity(i);
        activity.finish();
    }

    private void saveAndGoNext() {
        addScoreToSession();

        Intent goWithScore = new Intent(activity.getApplicationContext(), TotalScoreDisplay.class);
        Bundle putExerciseId = new Bundle();
        putExerciseId.putInt("countOrTime", countOrTime);
        putExerciseId.putInt("id", plankExId);
//        putExerciseId.putString("skeletalPointList", plankPrintList);
        goWithScore.putExtras(putExerciseId);
        activity.startActivity(goWithScore);
    }

    private void addScoreToSession() {
        score = scoreCalculator(countOrTime);
        sessionManager.addPlankScore(score);
        sessionManager.addPlankTime(countOrTime);
    }

    private int processSinglePose() {
        double angleValueforSlope;
        final float knee_angle;

        if (isSkeletonEmpty())
            return PLANK_UNKNOWN;

        Arrays.sort(keypoints);

        int plankStatus = PLANK_UNKNOWN;

//
//        Testing

        final double slopeAngleLeft = Math.atan2(keypoints[14].getPosition().x - keypoints[16].getPosition().x, keypoints[16].getPosition().y - keypoints[14].getPosition().y);
        final double hipSlopeAngle = Math.atan2(keypoints[6].getPosition().x - keypoints[12].getPosition().x, keypoints[12].getPosition().y - keypoints[6].getPosition().y);
        final double slopeAngleRight = Math.atan2(keypoints[13].getPosition().x - keypoints[15].getPosition().x, keypoints[15].getPosition().y - keypoints[13].getPosition().y);

        if (AppController.DEBUG_MODE) {
            final DecimalFormat df = new DecimalFormat("0.##");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView printval = activity.findViewById(R.id.valueCheckdisp);
                    printval.setText(df.format(slopeAngleRight) + "\n" + df.format(slopeAngleLeft) + "\n" + df.format(hipSlopeAngle) +
                            "\n" + keypoints[16].getPosition().x);

                    String printForDebug = "\nkneepointX : " + df.format(keypoints[14].getPosition().x) + "kneepointY : " + df.format(keypoints[14].getPosition().y) +
                            "\ntoepointX : " + df.format(keypoints[16].getPosition().x) + "toepointY : " + df.format(keypoints[16].getPosition().y) +
                            "\nHippointX : " + df.format(keypoints[12].getPosition().x) + "HippointY : " + df.format(keypoints[12].getPosition().y) +
                            "\nshoulderpointX : " + df.format(keypoints[6].getPosition().x) + "shoulderpointX : " + df.format(keypoints[6].getPosition().y) +
                            "Slope Angle Right : " + df.format(slopeAngleRight) + " Slope Angle Left : " + df.format(slopeAngleLeft) + " Hip slope angle : " + df.format(hipSlopeAngle) +
                            " Foot Point = " + keypoints[16].getPosition().x + "\n";

//                    writeToFile(printForDebug);

                    for (int i=0; i < keypoints.length; i++){
                        printList += "(" + df.format(keypoints[i].getPosition().x) + "," + df.format(keypoints[i].getPosition().y) + "); ";
                    }
                    printList += " / " + correctionText + " / ";

                    printList += "\nSlope Angle Left : " + df.format(slopeAngleLeft) + ", Hip slope angle : " + df.format(hipSlopeAngle);
                    printList += " || ";
                }
            });
        }

        if ((0.05 <= slopeAngleLeft && slopeAngleLeft <= 0.35) && (/*0.05 <= hipSlopeAngle && */Math.abs(hipSlopeAngle) <= 0.35)) {
            plankStatus = PLANK_UP;
            correctionText = "Good. Maintain that plank pose";
        }

        if (0.36 <= slopeAngleLeft && slopeAngleLeft <= 0.4) {
            speakText("Lower your Hip");
            correctionText = "Lower your Hip";
            plankStatus = PLANK_UNKNOWN;
        }

        return plankStatus;
    }

    private void writeToFile(String data) {
        try {
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "PerfitLogs/" + currentDateandTime + ".txt", true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}