package org.perfit.fitness.exercise;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class Pushup extends Exercise {

    private final int exIdPushup;
    private final SessionManager sessionManager;


    private final int PUSHUP_UNKNOWN = 0;
    private final int PUSHUP_UP = 1;
    private final int PUSHUP_DOWN = 2;

    private int curState = -1;
    private int prevPose;

    private final ConstraintLayout detectingLayout;
    private final GifImageView workouts;
    private final TextView exerciseName;
    private final Chronometer timer;

    private int checkSingle;
    private final TextView beginStatus;

    private String correctionText = "";

    final float hipAngle = -1.0f;

    private String fullString = "";
    private String currentDateandTime = "";
    private float howHigh;

    private String pushupPrintList;


    public Pushup(Activity classifierActivity, int exerciseIdPushup) {
        super(classifierActivity);
        rotationDegree = 90;
        exIdPushup = exerciseIdPushup;
        startGlobalTime = System.nanoTime();
        sessionManager = new SessionManager(activity.getApplicationContext());
        detectingLayout = activity.findViewById(R.id.detecting_layout);
        workouts = activity.findViewById(R.id.workoutGifs);
        exerciseName = activity.findViewById(R.id.exercise_name);
        timer = activity.findViewById(R.id.timeValue);
        beginStatus = activity.findViewById(R.id.feedbackBegin);
        GifImageView exerciseIcon = activity.findViewById(R.id.exerciseiconLooking);
        GifImageView exerciseIconInexercise = activity.findViewById(R.id.exerciseIcontoShow);
        exerciseIcon.setImageResource(R.drawable.push_gif_whitebg);
        exerciseIconInexercise.setImageResource(R.drawable.push_gif_blackbg);

        String path = Environment.getExternalStorageDirectory() + File.separator + "PerfitLogs";
        File folder = new File(path);
        folder.mkdir();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMM-HHmm", Locale.getDefault());

        currentDateandTime = sdf.format(new Date());

        speakText("Get into Push Up, up position ");
    }

    private float scoreCalculator(int count) {
        float[][] ageConstList = {{24.3205f, 20.41184f, 17.80607f, 14.766012f, 13.463128f,
                13.02883f}, {15.200306f, 15.6346f, 0.0f, 0.0f, 0.0f, 0.0f}};
        float result = (float) (10 * (1 - Math.exp(-(count / ageConstList[0][0]))));
        return result;
    }

    public void processPoints(SkeletonPoint[] poseKeypoints) {
        super.processPoints(poseKeypoints);

        int curPose = processSinglePose();

        updateStringCheck++;
        if (updateStringCheck >= 10){
            callAPIDataCollection();
            updateStringCheck = 0;
        }

        // Human detected twenty times ?
        if (!humanDetected()) {
            if (System.nanoTime() - startGlobalTime > timeLimitBeforeDetection) {
//                saveAndGoHome();
                saveAndGoNext();
            }
            return;
        }

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

        if (curPose == PUSHUP_UNKNOWN) {
            return;
        }

        if ((curState != curPose) && (prevPose == curPose)) {
            if (curState == PUSHUP_DOWN) {
                countOrTime++;

                if (firstCountIncreasedTime == -1)
                    firstCountIncreasedTime = System.nanoTime();
                lastCountIncreasedTime = System.nanoTime();

                speakTextWithFrequency(countOrTime + "",0);
            }
            curState = curPose;
        }
        prevPose = curPose;
    }

    private boolean humanDetected() {

        if (humanPoseCount >= 10)
            return true;

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
        }

        return false;
    }

    private boolean isCorrectPosition() {
        //TODO: Needs more logic
        if (correctPositionCount > 10)
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

    private void setupExerciseScreen() {
        Handler exerciseBeginDelay = new Handler();
        exerciseBeginDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                startGlobalTime = System.nanoTime();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        speakText("Now, lets start counting push ups");
                        workouts.setBackgroundResource(Utilities.gifList.get(1));
                        exerciseName.setText(Utilities.exerciseName.get(1));
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
        put_exerciseid.putInt("id", exIdPushup);
//        put_exerciseid.putString("skeletalPointList", pushupPrintList);
        goAfterSquat.putExtras(put_exerciseid);
        activity.startActivity(goAfterSquat);
    }

    private void addScoreToSession() {
        sessionManager.addPushupScore(scoreCalculator(countOrTime));
        sessionManager.addPushupCount(countOrTime);
        long duration = (System.nanoTime() - startGlobalTime) / 1000000000L;
        int durationInSeconds = (int) duration - 10;
        sessionManager.addPushupTime(durationInSeconds);
    }

    private int processSinglePose() {

        if (isSkeletonEmpty())
            return PUSHUP_UNKNOWN;

        final double angleValueforSlope;

        int pushupStatus = PUSHUP_UNKNOWN;

        angleValueforSlope =
                Math.atan2(keypoints[6].getPosition().x - keypoints[16].getPosition().x,
                        keypoints[16].getPosition().y - keypoints[6].getPosition().y);

        if (angleValueforSlope < 0.5 /* && (155<=hipAngle&&hipAngle<=210)*/) {
            howHigh = keypoints[6].getPosition().x - keypoints[16].getPosition().x;
            if ((howHigh < 0.075 && howHigh > -0.075) && (angleValueforSlope < 0.3 && angleValueforSlope > 0)) {
                pushupStatus = PUSHUP_DOWN;
            } else if (angleValueforSlope > 0.3) {
                pushupStatus = PUSHUP_UP;
            }
        }

        if (AppController.DEBUG_MODE) {
            final int status = pushupStatus;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final DecimalFormat df = new DecimalFormat("0.##");
                    TextView printval = activity.findViewById(R.id.valueCheckdisp);
                    printval.setText(df.format(howHigh) + "\n" + status + "\n" + df.format(angleValueforSlope) + "\n" + keypoints[16].getPosition().x);

                    for (int i=0; i < keypoints.length; i++){
                        printList += "(" + df.format(keypoints[i].getPosition().x) + "," + df.format(keypoints[i].getPosition().y) + "); ";
                    }
                    printList += " / " + correctionText + " / ";

                    printList += "HowHigh=> " + df.format(howHigh) + ", Status=>" + status + ", SlopeAngle = " + df.format(angleValueforSlope);
                    printList += " || ";
                }
            });
        }

        return pushupStatus;

    }

}
