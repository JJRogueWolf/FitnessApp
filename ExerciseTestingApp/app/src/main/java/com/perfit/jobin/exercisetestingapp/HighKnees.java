package com.perfit.jobin.exercisetestingapp;

import android.app.Activity;
import android.graphics.PointF;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Arrays;

public class HighKnees extends Exercise {

    int exerciseIdsquatValue;
    int squatFlag = 0;

    public HighKnees(Activity classifierActivity, int exerciseIdsquat) {
        super();
        rotationDegree = 0;
        activity = classifierActivity;
        exerciseIdsquatValue = exerciseIdsquat;
        startGlobalTime = System.nanoTime();
    }

    public void processPoints(SkeletonPoint[] poseKeypoints) {

        if(startGlobalTime == -1)
            startGlobalTime = System.nanoTime();

        int status = processSinglePose(poseKeypoints);

        // If nothing ever happens for 20s since start of detection
        // go home
        if(status == -1 && !firstPoseDetected){
            long currentGlobalTime = System.nanoTime();
        }

        if (this.stateFlag1 == 0 && status == 1) {
            firstPoseDetected = true;
            lastDetectedTime = System.nanoTime();
            Toast.makeText(
                    activity.getApplicationContext(),
                    "UP",
                    Toast.LENGTH_SHORT)
                    .show();
//            ((TextView)activity.findViewById(R.id.plankvaluefour)).setText("UP");
            this.stateFlag1 = 1;
            this.stateFlag2 = 0;
            this.stateFlag3 = 0;
            this.stateCount1 += 1;
        }
        if (this.stateFlag2 == 0 && status == 2) {
            firstPoseDetected = true;
            lastDetectedTime = System.nanoTime();
            Toast.makeText(
                    activity.getApplicationContext(),
                    "DOWN",
                    Toast.LENGTH_SHORT)
                    .show();
//            ((TextView)activity.findViewById(R.id.plankvaluefour)).setText("DOWN");
            this.stateFlag2 = 1;
            this.stateFlag1 = 0;
            this.stateCount2 += 1;
        }
        if (this.stateFlag3 == 0 && status == 3) {
            firstPoseDetected = true;
            lastDetectedTime = System.nanoTime();
            Toast.makeText(
                    activity.getApplicationContext(),
                    "HALF DOWN",
                    Toast.LENGTH_SHORT)
                    .show();
//            ((TextView)activity.findViewById(R.id.plankvaluefour)).setText("HALF DOWN");
            this.stateFlag3 = 1;
            this.stateFlag1 = 0;
            this.stateCount3 += 1;
        }

        long currentTime = System.nanoTime();
        countOrTime = stateCount2;
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

        if(allElementsEmpty)
            return -1;

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

        /*
        Status legend: 
        0 - undetected
        1 - UP
        2 - DOWN
        3 - HALF DOWN
        */

        float squat_angle = getAngleBetweenLines(12, 14, 11, 13);
        DecimalFormat df = new DecimalFormat("##.#");
//        ((TextView)activity.findViewById(R.id.priArmAngle)).setText(df.format(squat_angle));
        int squatStatus = 0;

        return squatStatus;
    }
}
