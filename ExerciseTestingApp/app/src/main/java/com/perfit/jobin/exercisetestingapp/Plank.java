package com.perfit.jobin.exercisetestingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Plank extends Exercise {

    View view;
    int exerciseIdplankValue;
    private int[] statusChange = new int[4];
    private int index = -1;

    int pushupStatus, stateStatus;

    public Plank(Activity classifierActivity, int exerciseIdplank) {
        super();
        rotationDegree = 90;
        activity = classifierActivity;
        exerciseIdplankValue = exerciseIdplank;
        startGlobalTime = System.nanoTime();
    }

    public int scoreCalculator(long duration) {
        if (duration >= 360000000000l)
            return 5;
        else if (duration >= 240000000000l)
            return 4;
        else if (duration >= 120000000000l)
            return 3;
        else if (duration >= 60000000000l)
            return 2;
        else if (duration >= 30000000000l)
            return 1;
        else
            return 0;
    }

    public void processPoints(SkeletonPoint[] poseKeypoints) {

        if (startGlobalTime == -1)
            startGlobalTime = System.nanoTime();

        int status = processSinglePose(poseKeypoints);

        // If nothing ever happens for 20s since start of detection
        // go home
//        if(status == -1 && !firstPoseDetected){
//            long currentGlobalTime = System.nanoTime();
//            if(currentGlobalTime - startGlobalTime > 20000000000l){
//
//            }
//        }

        if (status == 1) {

            firstPoseDetected = true;
            if (this.startTime == -1) {
                this.startTime = System.nanoTime();
                plankCheck = true;
            }
            correctCount++;
//            if (correctCount % 5 == 0) {

            this.lastRightTime = System.nanoTime();
            Toast.makeText(
                    activity.getApplicationContext(),
                    "Plank pose detected",
                    Toast.LENGTH_SHORT)
                    .show();
//                ((TextView)activity.findViewById(R.id.plankvaluefour)).setText("Detected");
//            }

            // Congratulate if you detect plank for 30 consecutive times
        } else if (status == 0) {
//            Toast.makeText(
//                    activity.getApplicationContext(),
//                    this.hipAngle + " / " + this.primaryArmAngle + " / " + this.secondaryArmAngle,
//                    Toast.LENGTH_SHORT)
//                    .show();
        } else {
            plankCheck = false;
        }

    }

    public int processSinglePose(SkeletonPoint[] poseKeypoints) {
        super.processSinglePose(poseKeypoints);

        if (keypoints == null)
            return -1;
        boolean allElementsEmpty = true;
        for (SkeletonPoint x : keypoints
        ) {
            if (x != null) {
                allElementsEmpty = false;
                break;
            }
        }

        if (allElementsEmpty)
            return -1;


        PointF neckCoordinates = new PointF();
        neckCoordinates.x = (keypoints[5].getPosition().x + keypoints[6].getPosition().x) / 2f;
        neckCoordinates.y = (keypoints[5].getPosition().y + keypoints[6].getPosition().y) / 2f;

        SkeletonPoint neckPoint = new SkeletonPoint(17, neckCoordinates, 0.8f);
        Arrays.sort(keypoints);
        int plankStatus = 0;
//        final DecimalFormat df = new DecimalFormat("0.##");
//
        final double angleValueforSlope =
                Math.atan2(keypoints[6].getPosition().x - keypoints[16].getPosition().x,
                        keypoints[16].getPosition().y - keypoints[6].getPosition().y);
        final ScrollView scrollView = activity.findViewById(R.id.scroll);
        scrollView.fullScroll(View.FOCUS_DOWN);
        final TextView printValues = activity.findViewById(R.id.printSecond);
        int[][] hipAngleList = {{7, 9, 5}, {8, 10, 6}, {7, 10, 5}, {8, 9, 6}, {8, 9, 5}, {7, 10,
                6}};
        int[][] primaryArmAngleList = {{12, 6, 8}, {11, 5, 7}, {12, 5, 7}, {11, 6, 8}, {12, 6, 7}
                , {11, 5, 8}};
        int[][] secondaryArmAngleList = {{6, 8, 10}, {5, 7, 9}, {17, 8, 10}, {17, 7, 9}};
        int[][] legFailAngleList = {{12, 14, 16}, {11, 13, 15}};
        int[][] plankArmAngleList = {{10, 6, 12}, {10, 6, 11}, {10, 5, 12}, {10, 5, 11}, {9, 6,
                12}, {9, 6, 11}, {9, 5, 12}, {9, 5, 11}};
        int[][] secondaryHipAngleList = {{7, 5, 15}, {8, 6, 16}, {7, 6, 15}, {8, 5, 16}, {7, 5,
                16}, {8, 6, 15}, {7, 6, 16}, {8, 5, 15}};

        final DecimalFormat df = new DecimalFormat("##.#");
        //Return if leg angle is not straight
        for (int i = 0; i < legFailAngleList.length; i++) {
            float angle = getAngleBetweenPoints(legFailAngleList[i][0], legFailAngleList[i][1],
                    legFailAngleList[i][2]);
            if (angle != -1.0f && !(150 <= angle && angle <= 220))
                return -1;
        }

        for (int i = 0; i < plankArmAngleList.length; i++) {
            float angle = getAngleBetweenPoints(plankArmAngleList[i][0], plankArmAngleList[i][1],
                    plankArmAngleList[i][2]);
            if (angle != -1.0f) {
                plankArmAngle = angle;
                break;
            }
        }

        for (int i = 0; i < hipAngleList.length; i++) {
            float angle = getAngleBetweenPointsNeckFirst(neckPoint, hipAngleList[i][1],
                    hipAngleList[i][2]);
            if (angle != -1.0f) {
                hipAngle = angle;
                break;
            }
        }

        for (int i = 0; i < secondaryHipAngleList.length; i++) {
            float angle = getAngleBetweenPoints(secondaryHipAngleList[i][0],
                    secondaryHipAngleList[i][1], secondaryHipAngleList[i][2]);
            if (angle != -1.0f) {
                secondaryHipAngle = angle;
                break;
            }
        }

        for (int i = 0; i < primaryArmAngleList.length; i++) {
            float angle = getAngleBetweenPoints(primaryArmAngleList[i][0],
                    primaryArmAngleList[i][1], primaryArmAngleList[i][2]);
            if (angle != -1.0f) {
                primaryArmAngle = angle;
                break;
            }
        }
        for (int i = 0; i < secondaryArmAngleList.length; i++) {
            float angle = -1.0f;
            if (secondaryArmAngleList[i][0] == 17)
                angle = getAngleBetweenPointsNeckFirst(neckPoint, secondaryArmAngleList[i][1],
                        secondaryArmAngleList[i][2]);
            else
                angle = getAngleBetweenPoints(secondaryArmAngleList[i][0],
                        secondaryArmAngleList[i][1], secondaryArmAngleList[i][2]);
            if (angle != -1.0f) {
                secondaryArmAngle = angle;
                break;
            }
        }


        float knee_dx = keypoints[16].getPosition().x - keypoints[14].getPosition().x;
        float knee_dy = keypoints[14].getPosition().y - keypoints[16].getPosition().y;

        final float knee_angle = knee_dy/knee_dx;

        if ((5<=knee_angle && knee_angle<=8.5) && (0.05 <= angleValueforSlope && angleValueforSlope <= 0.25)) {
            plankStatus = 1;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                printValues.append("\n" + " Value =" + df.format(angleValueforSlope) + " " + df.format(knee_angle));
            }
        });

        return plankStatus;

    }
}
