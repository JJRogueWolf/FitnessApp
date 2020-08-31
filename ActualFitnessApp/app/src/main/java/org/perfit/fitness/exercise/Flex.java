package org.perfit.fitness.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.perfit.fitness.R;
import org.perfit.fitness.activities.MoreComing;
import org.perfit.fitness.activities.SessionManager;
import org.perfit.fitness.activities.TotalScoreDisplay;
import org.perfit.fitness.tflite.SkeletonPoint;
import org.perfit.fitness.utilities.Utilities;

import java.text.DecimalFormat;
import java.util.Arrays;

import pl.droidsonroids.gif.GifImageView;

public class Flex extends Exercise {
    public Flex(Activity activity) {
        super(activity);
    }
    /*


    /*private final int exerciseIdFlexValue;
    private final SessionManager flexManager;
    private int hipAngleFlag = 0;
    private int hipAngleStatus = 0;
    private float maxLeftAngle = -999.0f;
    private float minRightAngle = 999.0f;
    private float averageLeftAngle = 0.0f;
    private float averageRightAngle = 0.0f;
    private float leftIndex = 0;
    private float rightIndex = 0;


    private final RelativeLayout detectingLayout;
    private final GifImageView workouts;
    private final TextView exerciseName_skel;
    private final Chronometer timer;
    private boolean allElementsEmpty;
    private boolean flagToPlayOnce = false;
    private int trackcount = 0;
    private final int checkSingle;
    private Handler startDelay;
    private boolean startFlagafterdelay = false;
    private boolean firstPoseDetected;

    public Flex(Activity classifierActivity, int exerciseIdsquat, int checkSingle) {
        super(classifierActivity);
        rotationDegree = 0;
        this.checkSingle = checkSingle;
        exerciseIdFlexValue = exerciseIdsquat;
        startGlobalTime = System.nanoTime();
        flexManager = new SessionManager(activity.getApplicationContext());
        detectingLayout = activity.findViewById(R.id.detecting_layout);
        workouts = activity.findViewById(R.id.workoutGifs);
        exerciseName_skel = activity.findViewById(R.id.exercise_name);
        timer = activity.findViewById(R.id.timeValue);
    }

    private int scoreCalculatorLeft(float angle) {
//        angle = -100;
        if (Math.abs(angle) > 180) {
            angle = 180.0f;
        }
        if (Math.abs(angle) < 100) {
            angle = 100.0f;
        }
        float score = (float) (10 * (1 - Math.exp(-0.25 * ((Math.abs(angle) - 100) / 10))));
        return (int) score;
    }

    private int scoreCalculatorRight(float angle) {
//        angle = -80;
        if (Math.abs(angle) > 80) {
            angle = 80.0f;
        }
        if (Math.abs(angle) < 0) {
            angle = 0.0f;
        }
        float score = (float) (10 * (1 - Math.exp(-0.25 * ((80 - Math.abs(angle)) / 10))));
        return (int) score;
    }

    public void processPoints(SkeletonPoint[] poseKeypoints) {
        super.processPoints(poseKeypoints);
        int status = processSinglePose();

        // If nothing ever happens for 20s since start of detection
        // go home
        if (detectingLayout.getVisibility() == View.VISIBLE && status == -1 && !firstPoseDetected) {
            long currentGlobalTime = System.nanoTime();
            if (currentGlobalTime - startGlobalTime > 20000000000L) {
                score = scoreCalculatorLeft(-100);
                score = scoreCalculatorRight(-80);
                flexManager.addFlexibiltyScore(score);
                flexManager.addFlexibilityTime(0);
                saveAndGoHome(activity);
            }
        }

        long currentGlobalTimetostart = System.nanoTime();
        if (!allElementsEmpty) {
            if (trackcount >= 40) {

                if (startGlobalTime == -1) {
                    startGlobalTime = System.nanoTime();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!flagToPlayOnce) {
//                            lastDetectedTime = System.nanoTime();
                            detectingLayout.setVisibility(View.GONE);
                            workouts.setBackgroundResource(Utilities.gifList.get(1));
                            exerciseName_skel.setText(Utilities.exerciseName.get(1));
                            timer.setBase(SystemClock.elapsedRealtime());
                            timer.start();
                            flagToPlayOnce = true;
                            startDelay = new Handler();
                            startDelay.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startFlagafterdelay = true;
                                }
                            }, 2000);
                        }
                    }
                });
            }
        }
        // If no transitions detected in 10s
        if (startFlagafterdelay && currentTime - lastDetectedTime > 10000000000L && lastDetectedTime != -1) {
            int totalCount = (scoreCalculatorLeft(averageRightAngle) + scoreCalculatorRight(averageLeftAngle)) / 2;
            flexManager.addFlexibiltyScore(Math.abs(totalCount));
            flexManager.addFlexibiltyCount(countOrTime);
            long duration = (currentTime - startGlobalTime) / 1000000000L;
            int durationInSeconds = (int) duration;
            flexManager.addFlexibilityTime(durationInSeconds);
            Intent goafterflex = new Intent(activity.getApplicationContext(), TotalScoreDisplay.class);
            Bundle put_exerciseid = new Bundle();
            put_exerciseid.putInt("down", countOrTime);
            put_exerciseid.putInt("id", exerciseIdFlexValue);
            put_exerciseid.putInt("checkSingle", checkSingle);
            put_exerciseid.putLong("plankTime",0);
            goafterflex.putExtras(put_exerciseid);
            activity.startActivity(goafterflex);
            finish();
        }

        if (currentTime - startGlobalTime > 180000000000L && lastDetectedTime != -1) {
            int totalCount = (scoreCalculatorLeft(averageLeftAngle) + scoreCalculatorRight(averageRightAngle)) / 2;
//            this.score = scoreCalculator(totalCount);
            flexManager.addFlexibiltyScore(totalCount);
            saveAndGoHome(activity);
        }
    }

    private void saveAndGoHome(Context context) {
        Intent i = new Intent(context, MoreComing.class);
        context.startActivity(i);
        finish();
    }

    private int processSinglePose() {
        if (keypoints == null)
            return -1;
        allElementsEmpty = true;
        for (SkeletonPoint x : keypoints
        ) {
            if (x != null) {
                allElementsEmpty = false;
                break;
            }
        }

        if (allElementsEmpty) {
            return -1;
        } else {
            trackcount++;
        }

        PointF neckCoordinates = new PointF();
        neckCoordinates.x = (keypoints[5].getPosition().x + keypoints[6].getPosition().x) / 2f;
        neckCoordinates.y = (keypoints[5].getPosition().y + keypoints[6].getPosition().y) / 2f;

        SkeletonPoint neckPoint = new SkeletonPoint(17, neckCoordinates, 0.8f);
        // 80% confidence this is correct, because why not
        Arrays.sort(keypoints);

        // 17: NECK | 12: RIGHT HIP | 11: LEFT HIP
        if (neckPoint.getPosition().x == -1.0f || keypoints[12].getPosition().x == -1.0f || keypoints[11].getPosition().x == -1.0f) {
            System.out.println("Undetected");
            return -1;
        }

        int flexStatus = -1;
        float right_angle = -1.0f, left_angle = -1.0f;
        right_angle = getAngleBetweenPointsNeckFirst(neckPoint, 12, 14);
        if (right_angle == -1)
            right_angle = getAngleBetweenPointsNeckFirst(neckPoint, 12, 16);
        left_angle = getAngleBetweenPointsNeckFirst(neckPoint, 11, 13);
        if (left_angle == -1)
            left_angle = getAngleBetweenPointsNeckFirst(neckPoint, 11, 15);

        DecimalFormat df = new DecimalFormat("##.#");
//        ((TextView) activity.findViewById(R.id.priArmAngle)).setText(df.format(right_angle));
//        ((TextView) activity.findViewById(R.id.totalPriAngle)).setText(df.format(left_angle));

        float right_hip_angle = getLineAngleLegNeckSecond(11, neckPoint);
        float left_hip_angle = getLineAngleLegNeckSecond(12, neckPoint);

        if (right_hip_angle < minRightAngle)
            minRightAngle = right_hip_angle;
        if (left_hip_angle > maxLeftAngle)
            maxLeftAngle = left_hip_angle;

        if (right_hip_angle < -120)
            hipAngleStatus = 1;
        if (left_hip_angle > -60)
            hipAngleStatus = 2;


        // Status legend:
        // 0 - undetected
        // 1 - LEFT
        // 2 - RIGHT


        if ((190 <= left_angle && left_angle <= 220) && (200 <= right_angle && right_angle <= 220))
            flexStatus = 2;
        if ((140 <= left_angle && left_angle <= 165) && (140 <= right_angle && right_angle <= 180))
            flexStatus = 1;

        return flexStatus;
    }*/

}
