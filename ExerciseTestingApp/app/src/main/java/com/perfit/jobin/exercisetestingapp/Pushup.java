package com.perfit.jobin.exercisetestingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Pushup extends Exercise {

    int exerciseIdPushupValue;
    TextView printValues;
    int pushupStatus, stateStatus;
    private int[] statusChange = new int[4];
    private int index = -1;

    public Pushup(Activity classifierActivity, int exerciseIdPushup) {
        super();
        rotationDegree = 90;
        activity = classifierActivity;
        exerciseIdPushupValue = exerciseIdPushup;
        startGlobalTime = System.nanoTime();
    }

    public int scoreCalculator(int count)
    {
        if(count >= 55)
            return 5;
        else if (count >= 35)
            return 4;
        else if (count >= 20)
            return 3;
        else if (count >= 10)
            return 2;
        else if (count >= 5)
            return 1;
        else
            return 0;
    }

    public void processPoints(SkeletonPoint[] poseKeypoints) {

        if (startGlobalTime == -1)
            startGlobalTime = System.nanoTime();

        int status = processSinglePose(poseKeypoints);


        if(this.stateFlag1 == 0 && status == 1)
        {
            firstPoseDetected = true;
            lastDetectedTime = System.nanoTime();
            this.stateFlag1 = 1;
            this.stateFlag2 = 0;
            this.stateCount1 += 1;
        }
        if (this.stateFlag2 == 0 && status == 2) {
            firstPoseDetected = true;
            lastDetectedTime = System.nanoTime();
            this.stateFlag2 = 1;
            this.stateFlag1 = 0;
            this.stateCount2 += 1;
        }


        countOrTime = (stateCount1 + stateCount2) / 2;
        ((TextView)activity.findViewById(R.id.counDisplay)).setText(String.valueOf(countOrTime));

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

        if (allElementsEmpty) {
//            writeToFile("null;");
            return -1;
        }

        PointF neckCoordinates = new PointF();
        neckCoordinates.x = (keypoints[5].getPosition().x + keypoints[6].getPosition().x) / 2f;
        neckCoordinates.y = (keypoints[5].getPosition().y + keypoints[6].getPosition().y) / 2f;

        SkeletonPoint neckPoint = new SkeletonPoint(17, neckCoordinates, 0.8f);
        // 80% confidence this is correct, because why not
        Arrays.sort(keypoints);

        pushupStatus = 0;

        final float totalLeftValue = Math.abs(keypoints[6].getPosition().x - keypoints[16].getPosition().x) / Math.abs(keypoints[12].getPosition().x - keypoints[16].getPosition().x);
        final float totalRightValue = Math.abs(keypoints[5].getPosition().x - keypoints[15].getPosition().x) / Math.abs(keypoints[11].getPosition().x - keypoints[15].getPosition().x);
        /*
        Status legend: 
        0 - undetected
        1 - UP
        2 - DOWN
        3 - HALF DOWN
        */

        final double angleValueforSlope = Math.atan2(keypoints[16].getPosition().x,keypoints[6].getPosition().x);

        final TextView printText = activity.findViewById(R.id.printFirst);
        printText.setText(String.valueOf(totalLeftValue));
        final ScrollView scrollView = activity.findViewById(R.id.scroll);
        scrollView.fullScroll(View.FOCUS_DOWN);
        printValues = activity.findViewById(R.id.printSecond);
        final DecimalFormat df = new DecimalFormat("0.##");

        if (angleValueforSlope > 0.65 && angleValueforSlope < 1.3){
//            Down
            stateStatus = 2;
        }
        if (angleValueforSlope > 0.5 && angleValueforSlope < 0.65){
//            Up
            stateStatus = 1;
        }


        statusChange[++index % 3] = stateStatus;
        statusChange[3] = statusChange[0];

        if (statusChange[0] == 2 && statusChange[1] == 2 || statusChange[1] == 2 && statusChange[2] == 2 || statusChange[2] == 2 && statusChange[3] == 2 ){
            pushupStatus = 2;
        }
        if (statusChange[0] == 1 && statusChange[1] == 1 || statusChange[1] == 1 && statusChange[2] == 1 || statusChange[2] == 1 && statusChange[3] == 1){
            pushupStatus = 1;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                printValues.append("\n" +df.format(keypoints[6].getPosition().y) + ","  + df.format(keypoints[6].getPosition().x) + "," + df.format(keypoints[16].getPosition().x) + " Value =" + df.format(angleValueforSlope) + (stateStatus == 1 ? " Up": " Down"));
            }
        });

        return pushupStatus;

    }
}
