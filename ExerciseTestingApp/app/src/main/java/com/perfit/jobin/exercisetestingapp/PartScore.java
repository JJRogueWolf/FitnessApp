package com.perfit.jobin.exercisetestingapp;

public class PartScore extends Part {

    private float score;

    public PartScore(int keypointId, int x, int y, float score) {
        super(keypointId, x, y);
        this.score = score;
    }

    public float getScore() {
        return score;
    }
}