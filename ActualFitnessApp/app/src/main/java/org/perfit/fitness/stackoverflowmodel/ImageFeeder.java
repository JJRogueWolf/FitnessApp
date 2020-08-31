package org.perfit.fitness.stackoverflowmodel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageFeeder {

    public static void feedInputTensor(Interpreter tflite, Bitmap bitmapRaw, float mean, float std, ByteBuffer imgData) {
        Tensor tensor = tflite.getInputTensor(0);
        int[] shape = tensor.shape();
        int inputSize = shape[1];

        imgData.rewind();
        imgData.order(ByteOrder.nativeOrder());

        Bitmap bitmap = bitmapRaw;
        if (bitmapRaw.getWidth() != inputSize || bitmapRaw.getHeight() != inputSize) {
            Matrix matrix = getTransformationMatrix(bitmapRaw.getWidth(), bitmapRaw.getHeight(),
                    inputSize, inputSize, false);
            bitmap = Bitmap.createBitmap(inputSize, inputSize, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmapRaw, matrix, null);
        }

        // PoseNet has FLOAT32 type
        if (tensor.dataType() == DataType.FLOAT32) {
            for (int i = 0; i < inputSize; ++i) {
                for (int j = 0; j < inputSize; ++j) {
                    int pixelValue = bitmap.getPixel(j, i);
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - mean) / std);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - mean) / std);
                    imgData.putFloat(((pixelValue & 0xFF) - mean) / std);
                }
            }
        } else {
            for (int i = 0; i < inputSize; ++i) {
                for (int j = 0; j < inputSize; ++j) {
                    int pixelValue = bitmap.getPixel(j, i);
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                }
            }
        }

    }

    private static Matrix getTransformationMatrix(final int srcWidth,
                                                  final int srcHeight,
                                                  final int dstWidth,
                                                  final int dstHeight,
                                                  final boolean maintainAspectRatio) {
        final Matrix matrix = new Matrix();

        if (srcWidth != dstWidth || srcHeight != dstHeight) {
            final float scaleFactorX = dstWidth / (float) srcWidth;
            final float scaleFactorY = dstHeight / (float) srcHeight;

            if (maintainAspectRatio) {
                final float scaleFactor = Math.max(scaleFactorX, scaleFactorY);
                matrix.postScale(scaleFactor, scaleFactor);
            } else {
                matrix.postScale(scaleFactorX, scaleFactorY);
            }
        }

        matrix.invert(new Matrix());
        return matrix;
    }
}
