package org.perfit.fitness.googlemodel;

class Part {
    private final int heatMapScoresX;
    private final int heatMapScoresY;
    private final int keypointId;

    Part(int keypointId, int x, int y){
        this.keypointId = keypointId;
        this.heatMapScoresX = x;
        this.heatMapScoresY = y;
    }

    public int getHeatMapScoresX() {
        return heatMapScoresX;
    }

    public int getHeatMapScoresY() {
        return heatMapScoresY;
    }

    public int getKeypointId() {
        return keypointId;
    }
}

