package org.perfit.fitness.googlemodel;

import android.graphics.PointF;

import java.nio.ByteBuffer;

public class Offsets {

    private final ByteBuffer rawOffsets;
    private final int numParts;
    private final int height;
    private final int width;

    public Offsets(ByteBuffer rawOffsets, int height, int width, int numParts) {
        this.rawOffsets = rawOffsets;
        this.numParts = numParts;
        this.height = height;
        this.width = width;
    }

    public float getOffsetX(int partId, int x, int y) {
        return rawOffsets.getFloat(4 * (y * width * numParts * 2 + x * numParts * 2 + partId + numParts));

    }

    public float getOffsetY(int partId, int x, int y) {
        return rawOffsets.getFloat(4 * (y * width * numParts * 2 + x * numParts * 2 + partId));
    }

    public PointF getOffsetPoint(int partId, int x, int y) {
        float offsetX = getOffsetX(partId, x, y);
        float offsetY = getOffsetY(partId, x, y);
        return new PointF(offsetX, offsetY);
    }
}
