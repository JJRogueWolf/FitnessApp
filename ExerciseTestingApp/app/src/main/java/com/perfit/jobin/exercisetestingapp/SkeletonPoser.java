package com.perfit.jobin.exercisetestingapp;

import android.graphics.Bitmap;

public interface SkeletonPoser {

    SkeletonPoint[] skeletonImage(final Bitmap bitmap);
    void close();
}
