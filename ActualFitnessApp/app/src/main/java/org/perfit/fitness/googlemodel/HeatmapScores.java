package org.perfit.fitness.googlemodel;

import java.nio.ByteBuffer;

public class HeatmapScores {

    private final ByteBuffer rawScores;

    private final int numKeypoints;
    private final int height;
    private final int width;

    public HeatmapScores(ByteBuffer rawScores, int height, int width, int numKeypoints) {
        this.rawScores = rawScores;
        this.numKeypoints = numKeypoints;
        this.height = height;
        this.width = width;
    }

    public float getScore(int partId, int x, int y) {
        return rawScores.getFloat(4 * (y * width * numKeypoints + x * numKeypoints + partId));
    }

    public int getNumKeypoints() {
        return numKeypoints;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}

