package org.perfit.fitness.googlemodel;

class PartScore extends Part {

    private final float score;

    public PartScore(int keypointId, int x, int y, float score) {
        super(keypointId, x, y);
        this.score = score;
    }

    public float getScore() {
        return score;
    }
}