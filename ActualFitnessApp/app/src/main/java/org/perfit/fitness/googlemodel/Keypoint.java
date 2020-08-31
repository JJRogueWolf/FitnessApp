package org.perfit.fitness.googlemodel;

import android.graphics.PointF;

public class Keypoint implements Comparable<Keypoint> {
    private int id;
    private PointF position;
    private float score;

    public Keypoint(int id, PointF position, float score) {
        this.id = id;
        this.position = position;
        this.score = score;
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public float getScore() {
        return score;
    }

    public int getId() {
        return id;
    }

    public float calculateSquaredDistanceFromCoordinates(PointF coordinates) {
        float dx = position.x - coordinates.x;
        float dy = position.y - coordinates.y;
        return dx * dx + dy * dy;
    }

    @Override
    public int compareTo(Keypoint o) {
        return getId() - o.getId();
    }
}