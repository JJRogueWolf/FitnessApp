package com.perfit.jobin.exercisetestingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Flex extends Exercise {

    int exerciseIdFlexValue;

    String fullString = "";
    String currentDateandTime = "";
    String state;

    public Flex(Activity classifierActivity, int exerciseIdsquat) {
        super();
        rotationDegree = 0;
        activity = classifierActivity;
        exerciseIdFlexValue = exerciseIdsquat;
        startGlobalTime = System.nanoTime();

        String path = Environment.getExternalStorageDirectory() + File.separator + "PerfitLogs";
        File folder = new File(path);
        folder.mkdir();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMM-HHmm", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());
    }

    public void processPoints(SkeletonPoint[] poseKeypoints) {
        int status = processSinglePose(poseKeypoints);
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
        // 80% confidence this is correct, because why not
        Arrays.sort(keypoints);

        int status = -1;

        final double footPositiveSlope = Math.atan2(keypoints[11].getPosition().x - keypoints[16].getPosition().x, keypoints[16].getPosition().y - keypoints[11].getPosition().y);
        final double footNegativeSlope = Math.atan2(keypoints[12].getPosition().x - keypoints[15].getPosition().x, keypoints[15].getPosition().y - keypoints[12].getPosition().y);

        double leftHandSlope = Math.atan2(keypoints[10].getPosition().y - keypoints[11].getPosition().y, keypoints[10].getPosition().x - keypoints[11].getPosition().x);
        double rightHandSlope = Math.atan2(keypoints[9].getPosition().y - keypoints[12].getPosition().y, keypoints[12].getPosition().x - keypoints[9].getPosition().x);

        final double leftHandSlopedegree = Math.abs(Math.toDegrees(leftHandSlope));
        final double rightHandSlopedegree = Math.abs(Math.toDegrees(rightHandSlope));

        if (((footPositiveSlope < 0.29 && footPositiveSlope > 0.01) || (footNegativeSlope > -0.29 && footNegativeSlope < -0.01)) &&
                ((leftHandSlopedegree > 165/* && leftHandSlopedegree < 175*/) && (rightHandSlopedegree > 165/* && rightHandSlopedegree < 175*/))) {
            status = 0;
            state = "closer";
        }

        if (((footPositiveSlope < 0.6 && footPositiveSlope > 0.3) || (footNegativeSlope < -0.3 && footNegativeSlope > -0.6)) &&
                ((/*leftHandSlopedegree > 120 && */leftHandSlopedegree < 140) && (/*rightHandSlopedegree > 120 && */rightHandSlopedegree < 140))) {
            status = 1;
            state = "wide";
        }

        // 12: RIGHT HIP | 11: LEFT HIP
        final DecimalFormat df = new DecimalFormat("0.##");
        final ScrollView scrollView = activity.findViewById(R.id.scroll);
        scrollView.fullScroll(View.FOCUS_DOWN);
        final TextView printValues = activity.findViewById(R.id.printSecond);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fullString = "";
                fullString += "\n\n9-x = " + keypoints[9].getPosition().x + "   9-y = " + keypoints[9].getPosition().y +
                        "\n10-x = " + keypoints[10].getPosition().x + "   10-y = " + keypoints[10].getPosition().y +
                        "\n11-x = " + keypoints[11].getPosition().x + "   11-y = " + keypoints[11].getPosition().y +
                        "\n12-x = " + keypoints[12].getPosition().x + "   12-y = " + keypoints[12].getPosition().y;
                fullString += ";\n";
                System.out.println(fullString);
                writeToFile(fullString);
                printValues.setText("left = " + df.format(leftHandSlopedegree) + "\nright = " + df.format(rightHandSlopedegree) + "\nstate = " + state);
            }
        });

        return 0;
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
