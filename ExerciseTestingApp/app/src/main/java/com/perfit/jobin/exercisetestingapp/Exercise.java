package com.perfit.jobin.exercisetestingapp;

import android.app.Activity;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Exercise extends AppCompatActivity {
    private String skeletonPoints;
    protected SkeletonPoint[] keypoints;

    public int rotationDegree;

    public Activity activity;
    public static Set<MediaPlayer> activePlayers = new HashSet<MediaPlayer>();

    public long startTime = -1; // = System.nanoTime();
    public long endTime = -1;
    public long lastRightTime = -1, lastDetectedTime = -1;
    public long startGlobalTime = -1;

    public boolean firstPoseDetected = false;

    public int correctCount = 0;
    public int totalCount = 0;
    public boolean plankCheck = false;

    public int stateFlag1 = 0, stateFlag2 = 0, stateFlag3 = 0;
    public int stateCount1 = 0, stateCount2 =0, stateCount3 = 0;

    public int score = -1;
    public int countOrTime= 0;
    public long plankCount = 0;

    public float hipAngle = -1.0f, primaryArmAngle = -1.0f, secondaryArmAngle = -1.0f, downStateArmAngle = -1.0f, secondaryHipAngle = -1.0f, plankArmAngle = -1.0f;

    public int processSinglePose(SkeletonPoint[] poseKeypoints) {
        keypoints = poseKeypoints;
        return 1;
    }

    public void processPoints(SkeletonPoint[] poseKeypoints){

    }

    public int scoreCalculator()
    {
        return -1;
    }

    protected float getLineAngle(int a, int b) {
        float dot = keypoints[a].getPosition().x * keypoints[b].getPosition().x
                + keypoints[a].getPosition().y * keypoints[b].getPosition().y; // dot product
        float det = keypoints[a].getPosition().x * keypoints[b].getPosition().y
                - keypoints[a].getPosition().y * keypoints[b].getPosition().x; // determinant
        return (float) Math.toDegrees(Math.atan2(det, dot));
    }

    protected float getAngleBetweenLines(int a, int b, int c, int d) {
        if (keypoints[a].getPosition().x == -1.0f || keypoints[b].getPosition().x == -1.0f
                || keypoints[c].getPosition().x == -1.0f || keypoints[d].getPosition().x == -1.0f)
            return -1.0f;

        float angle1 = getLineAngle(a, b);
        float angle2 = getLineAngle(c, d);
        return (angle1 + angle2);
    }

    public float getAngleBetweenPoints(int a, int b, int c) {
        if (keypoints[a].getPosition().x == -1.0f || keypoints[b].getPosition().x == -1.0f
                || keypoints[c].getPosition().x == -1.0f)
            return -1.0f;
        double angle = Math.toDegrees(Math.atan2(keypoints[c].getPosition().y - keypoints[b].getPosition().y,
                keypoints[c].getPosition().x - keypoints[b].getPosition().x)
                - Math.atan2(keypoints[a].getPosition().y - keypoints[b].getPosition().y,
                        keypoints[a].getPosition().x - keypoints[b].getPosition().x));
        return (float) angle;
    }

    public float getDistanceBetweenTwoPoints(int a, int b){
        double distance = Math.sqrt(Math.pow(((keypoints[a].getPosition().x) - (keypoints[b].getPosition().x)), 2) + Math.pow(((keypoints[a].getPosition().y) - (keypoints[b].getPosition().y)), 2));
        return (float) distance;
    }

    public float getAngleBetweenPointsNeckFirst(SkeletonPoint neckPoint, int b, int c) {
        if (neckPoint.getPosition().x == -1.0f || keypoints[b].getPosition().x == -1.0f
                || keypoints[c].getPosition().x == -1.0f)
            return -1.0f;
        double angle = Math.toDegrees(Math.atan2(keypoints[c].getPosition().y - keypoints[b].getPosition().y,
                keypoints[c].getPosition().x - keypoints[b].getPosition().x)
                - Math.atan2(neckPoint.getPosition().y - keypoints[b].getPosition().y,
                neckPoint.getPosition().x - keypoints[b].getPosition().x));
        return (float) angle;
    }

    public float getAngleBetweenPointsNeckSecond(int a, SkeletonPoint neckPoint, int c) {
        if (neckPoint.getPosition().x == -1.0f || keypoints[a].getPosition().x == -1.0f
                || keypoints[c].getPosition().x == -1.0f)
            return -1.0f;
        double angle = Math.toDegrees(Math.atan2(keypoints[c].getPosition().y - neckPoint.getPosition().y,
                keypoints[c].getPosition().x - neckPoint.getPosition().x)
                - Math.atan2(keypoints[a].getPosition().y - neckPoint.getPosition().y,
                keypoints[a].getPosition().x - neckPoint.getPosition().x));
        return (float) angle;
    }

    protected void sortKeyPoints() {
        Arrays.sort(keypoints);
    }

}
