package com.perfit.jobin.exercisetestingapp;

import android.graphics.PointF;
import android.util.Size;

public class Pose {

    private Keypoint[] keypoints;
    private float score;

    public Pose() {
        this.keypoints = null;
        this.score = 0;
    }

    public Pose(Keypoint[] keypoints, float score) {
        this.keypoints = keypoints;
        this.score = score;
    }

    /**
     * Get all keypoints for this Pose.
     *
     * @return an array of keypoints.
     */
    public Keypoint[] getKeypoints() {
        return keypoints;
    }

    /**
     * Get the score of the Pose
     *
     * @return a float score from 0-1
     */
    public float getScore() {
        return score;
    }

    /**
     * Calculates relative keypoint position to target coordinates.
     *
     * The keypoint positions are all relative to the modelSize initially. To convert them to a
     * target size, use this method.
     *
     * @param modelSize   - the original model size that all keypoint positions are currently based on.
     * @param scaleToSize - the size to scale to
     * @param offsetX     - how much to offset the X position (if the model ran on a subset of the image)
     * @param offsetY     - how much to offset the Y position (if the model ran on a subset of the image)
     */
    public void calculateScaledPose(Size modelSize, Size scaleToSize, int offsetX, int offsetY) {
        for (Keypoint keypoint : keypoints) {
            PointF positionForModelSize = keypoint.getPosition();

            float widthScaled = (float) scaleToSize.getWidth() / modelSize.getWidth();
            float heightScaled = (float) scaleToSize.getHeight() / modelSize.getHeight();

            float xScaled = positionForModelSize.x * widthScaled + offsetX;
            float yScaled = positionForModelSize.y * heightScaled + offsetY;

            keypoint.setPosition(new PointF(xScaled, yScaled));
        }
    }
}