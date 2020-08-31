package com.perfit.jobin.exercisetestingapp;

import android.app.Activity;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Squat extends Exercise {

    int exerciseIdsquatValue;
    private int[] statusChange = new int[4];
    private int index = -1;
    int squatsStatus = 0;
    String statusSquat;

    boolean checkInSquats = false;

    public Squat(Activity classifierActivity, int exerciseIdsquat) {
        super();
        rotationDegree = 0;
        activity = classifierActivity;
        exerciseIdsquatValue = exerciseIdsquat;
        startGlobalTime = System.nanoTime();
    }

    public int scoreCalculator(int count) {
        if (count >= 60)
            return 5;
        else if (count >= 40)
            return 4;
        else if (count >= 30)
            return 3;
        else if (count >= 20)
            return 2;
        else if (count >= 10)
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
        if (status == -1 && !firstPoseDetected) {
//            ((TextView)activity.findViewById(R.id.plankvaluefour)).setText("Not Detecting");
        }

        if (this.stateFlag1 == 0 && status == 1) {
            firstPoseDetected = true;
            lastDetectedTime = System.nanoTime();
            Toast.makeText(activity.getApplicationContext(), "UP", Toast.LENGTH_SHORT).show();
            this.stateFlag1 = 1;
            this.stateFlag2 = 0;
            this.stateCount1 += 1;
        }
        if (this.stateFlag2 == 0 && status == 2) {
            firstPoseDetected = true;
            lastDetectedTime = System.nanoTime();
            Toast.makeText(activity.getApplicationContext(), "DOWN", Toast.LENGTH_SHORT).show();
            this.stateFlag2 = 1;
            this.stateFlag1 = 0;
            this.stateCount2 += 1;
        }
        countOrTime = (stateCount1 + stateCount2) / 2;
        ((TextView) activity.findViewById(R.id.counDisplay)).setText(String.valueOf(countOrTime));
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
        // 80% confidence this is correct, because why not
        Arrays.sort(keypoints);

        // 17: NECK | 12: RIGHT HIP | 11: LEFT HIP
        if (neckPoint.getPosition().x == -1.0f || keypoints[12].getPosition().x == -1.0f || keypoints[11].getPosition().x == -1.0f) {
            System.out.println("Undetected");
            return -1;
        }

        final double hipLenght = Math.abs(keypoints[11].getPosition().x - keypoints[12].getPosition().x);
        final double toeLenght = Math.abs(keypoints[15].getPosition().x - keypoints[16].getPosition().x);

        final double positiveSlope = Math.atan2(keypoints[11].getPosition().x - keypoints[14].getPosition().x, keypoints[14].getPosition().y - keypoints[11].getPosition().y);
        final double negativeSlope = Math.atan2(keypoints[12].getPosition().x - keypoints[13].getPosition().x, keypoints[13].getPosition().y - keypoints[12].getPosition().y);

        if (!checkInSquats) {
            if ((toeLenght / hipLenght) > 1 && (toeLenght / hipLenght) < 2) {
//            statusSquat = "InSquats";
                checkInSquats = true;
            } else {
                if ((toeLenght / hipLenght) > 2) {
                    statusSquat = "Closer";
                } else if ((toeLenght / hipLenght) < 1) {
                    statusSquat = "Wider";
                }
            }
        }

        if (checkInSquats){
            if (positiveSlope < 0.6 && negativeSlope > -0.6){
                statusSquat = "Up";
                squatsStatus = 1;
            }else{
                if (positiveSlope > 0.7 && negativeSlope < -0.7){
                    statusSquat = "Down";
                    squatsStatus = 2;
                }
                else
                {
                    statusSquat = "None";
                    squatsStatus = 0;
                }
            }
        }

        int squatsResponce = 0;

        statusChange[++index % 3] = squatsStatus;
        statusChange[3] = statusChange[0];

        if (statusChange[0] == 2 && statusChange[1] == 2 || statusChange[1] == 2 && statusChange[2] == 2 || statusChange[2] == 2 && statusChange[3] == 2 ){
            squatsResponce = 2;
        }
        if (statusChange[0] == 1 && statusChange[1] == 1 || statusChange[1] == 1 && statusChange[2] == 1 || statusChange[2] == 1 && statusChange[3] == 1){
            squatsResponce = 1;
        }
        /*
        Status legend: 
        0 - undetected
        1 - UP
        2 - DOWN
        3 - HALF DOWN
        */


        final DecimalFormat dfVa = new DecimalFormat("0.##");
        final TextView printValues = activity.findViewById(R.id.printSecond);
        final ScrollView scrollView = activity.findViewById(R.id.scroll);
        scrollView.fullScroll(View.FOCUS_DOWN);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                printValues.append("\nRight to Left: " + dfVa.format(squatRighttoLeftSlope) + ", Left to Right: " + dfVa.format(squatLefttoRightSlope) + (squatsStatus == 1 ? "Up" : squatsStatus == 2 ? "Down" : "None"));
                printValues.append("\nSlope to Left: " + dfVa.format(positiveSlope) + ", Slope to Right: " + dfVa.format(negativeSlope) + " " + statusSquat);
            }
        });

        return squatsResponce;
    }
}
