package org.perfit.fitness.tflite;

import android.graphics.Bitmap;

public interface SkeletonPoser {

    SkeletonPoint[] skeletonImage(final Bitmap bitmap);
    void close();
}
