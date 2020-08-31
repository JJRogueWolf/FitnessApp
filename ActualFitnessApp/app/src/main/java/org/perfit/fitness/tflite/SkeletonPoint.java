package org.perfit.fitness.tflite;

import android.graphics.PointF;

import org.perfit.fitness.googlemodel.Keypoint;

import java.util.Map;

public class SkeletonPoint implements Comparable<SkeletonPoint> {
    private int id;
    private PointF position;
    private float score;

    public SkeletonPoint() {
    }

    public SkeletonPoint(int id, PointF position, float score) {
        this.id = id;
        this.position = position;
        this.score = score;
    }

    public SkeletonPoint(Map<String, Object> skelObject) {
        this.id = (int) skelObject.get("partId");
        this.position = new PointF((float) skelObject.get("x"), (float) skelObject.get("y"));
        this.score = (float) skelObject.get("score");
    }

    public SkeletonPoint(Keypoint googleKeypoint) {
        this.id = googleKeypoint.getId();
        this.position = new PointF(googleKeypoint.getPosition().x, googleKeypoint.getPosition().y);
        this.score = googleKeypoint.getScore();
    }


    public PointF getPosition() {
        return position;
    }

    /**
     * 0- nose
     * 1- leftEye
     * 2- rightEye
     * 3- leftEar
     * 4- rightEar
     * 5- leftShoulder
     * 6- rightShoulder
     * 7- leftElbow
     * 8- rightElbow
     * 9- leftWrist
     * 10- rightWrist
     * 11- leftHip
     * 12- rightHip
     * 13- leftKnee
     * 14- rightKnee
     * 15- leftAnkle
     * 16- rightAnkle
     */


    private int getId() {
        return id;
    }

    @Override
    public int compareTo(SkeletonPoint o) {
        return getId() - o.getId();
    }
}
