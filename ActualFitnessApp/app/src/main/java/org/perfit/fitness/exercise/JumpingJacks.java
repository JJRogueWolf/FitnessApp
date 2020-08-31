package org.perfit.fitness.exercise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import java.text.DecimalFormat;

import pl.droidsonroids.gif.GifImageView;

public class JumpingJacks extends Exercise {

    private final int jumpingExId;
    private final SessionManager sessionManager;

    private final int JUMP_UNKNOWN = 0;
    private final int JUMP_WIDE = 1;
    private final int JUMP_CLOSE = 2;

    private int curState = -1;
    private int prevPose;
    private String state = "Null";
    // Flags added by Sai - End

    private final ConstraintLayout detectingLayout;
    private final GifImageView workouts;
    private final TextView exerciseName_skel;
    private final Chronometer timer;
    private int checkSingle = 0;

    private String correctionText = "";
    private final TextView beginStatus;

    private boolean timerRunning = false;
    private long mLastStopTime = 0;

    private String jumpingPrintList;

    public JumpingJacks(Activity classifierActivity, int exerciseIdsquat) {
        super(classifierActivity);
        rotationDegree = 0;
        jumpingExId = exerciseIdsquat;

        sessionManager = new SessionManager(activity.getApplicationContext());
        detectingLayout = activity.findViewById(R.id.detecting_layout);
        workouts = activity.findViewById(R.id.workoutGifs);
        exerciseName_skel = activity.findViewById(R.id.exercise_name);
        timer = activity.findViewById(R.id.timeValue);
        mChronometer = activity.findViewById(R.id.plankTimer);
        beginStatus = activity.findViewById(R.id.feedbackBegin);
        GifImageView exerciseIcon = activity.findViewById(R.id.exerciseiconLooking);
        GifImageView exerciseIconInexercise = activity.findViewById(R.id.exerciseIcontoShow);
        exerciseIcon.setImageResource(R.drawable.jumpingjack_white);
        exerciseIconInexercise.setImageResource(R.drawable.jumpingjack_black);

        // Used by parent onStart to speak
        introSpeech = "Get ready to do Jumping Jacks";

    }

    private float scoreCalculator(long duration) {
        return (float) (10 * (1 - Math.exp(-0.5f * duration / 153.34)));
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

        /*
        if (curPose == JUMP_UNKNOWN) {
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
        */

        if ((lastCountIncreasedTime != -1) && (System.nanoTime() - lastCountIncreasedTime >= 3000000000L)) {
            pauseChronometer();
        }

//        if ((curPose == JUMP_WIDE && prevPose == JUMP_CLOSE) || (curPose == JUMP_CLOSE && prevPose == JUMP_WIDE)) {
        if (curPose == JUMP_WIDE) {
            if (firstCountIncreasedTime == -1)
                firstCountIncreasedTime = System.nanoTime();
            lastCountIncreasedTime = System.nanoTime();
            startChronometer();

            countOrTime = (int) ((SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000);

            if ((countOrTime > 0) && (countOrTime % 5 == 0)) {
                speakText(countOrTime + " seconds");
            }
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
            correctionText = "Correct position, Now lets start jumping";
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
            } else {
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

    private void startChronometer() {
        if (!timerRunning) {
            mChronometer.setBase(SystemClock.elapsedRealtime() - mLastStopTime);
            mChronometer.start();
            timerRunning = true;
        }
    }

    private void pauseChronometer() {
        if (timerRunning) {
            mChronometer.stop();
            mLastStopTime = SystemClock.elapsedRealtime() - mChronometer.getBase();
            timerRunning = false;
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
                        speakText("Now, lets start Jumping Jacks");
                        workouts.setBackgroundResource(Utilities.gifList.get(0));
                        exerciseName_skel.setText(Utilities.exerciseName.get(0));
                        lastCountIncreasedTime = System.nanoTime();
                        setupExerciseScreenDone = true;
                        detectingLayout.setVisibility(View.GONE);
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
        put_exerciseid.putInt("id", jumpingExId);
//        put_exerciseid.putString("skeletalPointList", jumpingPrintList);
        goAfterSquat.putExtras(put_exerciseid);
        activity.startActivity(goAfterSquat);
    }

    private void addScoreToSession() {
        score = scoreCalculator(countOrTime);
        sessionManager.addJumpingScore(score);
        sessionManager.addJumpingTime(countOrTime);
    }

    private int processSinglePose() {

        if (isSkeletonEmpty())
            return JUMP_UNKNOWN;

        final double footPositiveSlope = Math.atan2(keypoints[11].getPosition().x - keypoints[16].getPosition().x, keypoints[16].getPosition().y - keypoints[11].getPosition().y);
        final double footNegativeSlope = Math.atan2(keypoints[12].getPosition().x - keypoints[15].getPosition().x, keypoints[15].getPosition().y - keypoints[12].getPosition().y);

        double leftHandSlope = Math.atan2(keypoints[10].getPosition().y - keypoints[11].getPosition().y, keypoints[10].getPosition().x - keypoints[11].getPosition().x);
        double rightHandSlope = Math.atan2(keypoints[9].getPosition().y - keypoints[12].getPosition().y, keypoints[12].getPosition().x - keypoints[9].getPosition().x);

        final double leftHandSlopedegree = Math.abs(Math.toDegrees(leftHandSlope));
        final double rightHandSlopedegree = Math.abs(Math.toDegrees(rightHandSlope));


        int squatsStatus = JUMP_UNKNOWN;

        if (((footPositiveSlope < 0.39 && footPositiveSlope > 0.01) || (footNegativeSlope > -0.29 && footNegativeSlope < -0.01)) &&
                ((leftHandSlopedegree > 165/* && leftHandSlopedegree < 175*/) && (rightHandSlopedegree > 165/* && rightHandSlopedegree < 175*/))) {
            squatsStatus = JUMP_CLOSE;
            state = "close";
        }

        if (((footPositiveSlope < 0.6 && footPositiveSlope > 0.4) || (footNegativeSlope < -0.3 && footNegativeSlope > -0.6)) &&
                ((/*leftHandSlopedegree > 120 && */leftHandSlopedegree < 140) && (/*rightHandSlopedegree > 120 && */rightHandSlopedegree < 140))) {
            squatsStatus = JUMP_WIDE;
            state = "wide";
        }

        if (AppController.DEBUG_MODE) {
            activity.runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    TextView printval = activity.findViewById(R.id.valueCheckdisp);
                    final DecimalFormat df = new DecimalFormat("0.##");
                    printval.setText(df.format(leftHandSlopedegree) + "\n" + df.format(rightHandSlopedegree) + "\n"
                            + df.format(footNegativeSlope) + "\n" + df.format(footPositiveSlope) + "\n" + state);

                    for (int i=0; i < keypoints.length; i++){
                        printList += "(" + df.format(keypoints[i].getPosition().x) + "," + df.format(keypoints[i].getPosition().y) + "); ";
                    }
                    printList += " / " + correctionText + " / ";

                    printList += "left = " + df.format(leftHandSlopedegree) + ", right = " + df.format(rightHandSlopedegree) + ", foot -ve = "
                            + df.format(footNegativeSlope) + ", foot +ve = " + df.format(footPositiveSlope);
                    printList += " || ";
                }
            });
        }


        return squatsStatus;

    }

}
