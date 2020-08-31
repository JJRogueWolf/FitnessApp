package org.perfit.fitness.googlemodel;

import android.graphics.PointF;

import java.nio.ByteBuffer;

public class Displacements {

    private final ByteBuffer rawDisplacements;

    private final int numEdges;
    private final int height;
    private final int width;

    public Displacements(ByteBuffer rawDisplacements, int height, int width, int numEdges) {
        this.rawDisplacements = rawDisplacements;
        this.numEdges = numEdges;
        this.height = height;
        this.width = width;
    }

    private float getDisplacementX(int edgeId, int x, int y) {
        return rawDisplacements.getFloat(4 * (y * width * numEdges * 2 + x * numEdges * 2 + (edgeId + numEdges)));
    }

    private float getDisplacementY(int edgeId, int x, int y) {
        return rawDisplacements.getFloat(4 * (y * width * numEdges * 2 + x * numEdges * 2 + edgeId));

    }

    public PointF getDisplacement(int edgeId, int x, int y) {
        float displacementX = getDisplacementX(edgeId, x, y);
        float displacementY = getDisplacementY(edgeId, x, y);

        return new PointF(displacementX, displacementY);
    }
}
