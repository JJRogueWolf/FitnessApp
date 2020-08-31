package org.perfit.fitness.googlemodel;

public class Pose {

    private final Keypoint[] keypoints;
    private final float score;

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

}