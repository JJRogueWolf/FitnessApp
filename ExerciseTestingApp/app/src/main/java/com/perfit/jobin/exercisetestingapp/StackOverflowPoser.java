// Someone wrote it
// Someone else verified it
// We are using it
package com.perfit.jobin.exercisetestingapp;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.os.Trace;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class StackOverflowPoser implements SkeletonPoser {
    private static final Logger LOGGER = new Logger();

    public static final int INPUT_HEIGHT = 225;
    public static final int INPUT_WIDTH = 225;

    /**
     * An array to hold inference results, to be feed into Tensorflow Lite as outputs. This isn't part
     * of the super class, because we need a primitive array here.
     */
    Map<Integer, Object> outputMap = new HashMap<>();
    float[][][][] out1 = new float[1][22][22][17];
    float[][][][] out2 = new float[1][22][22][34];
    float[][][][] out3 = new float[1][22][22][32];
    float[][][][] out4 = new float[1][22][22][32];

    /** Options for configuring the Interpreter. */
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    /** The loaded TensorFlow Lite model. */
    private MappedByteBuffer tfliteModel;

    /** Optional GPU delegate for accleration. */
    private GpuDelegate gpuDelegate = null;

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
    private Interpreter tflite;

    /** A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs. */
    private ByteBuffer imgData;

    PoseDecoder poseDecoder = new PoseDecoder();
    private SkeletonPoint[] skelPoints;

    /** Initializes a {@code Poser}. */
    public StackOverflowPoser(Activity activity) throws IOException {
        tfliteModel = loadModelFile(activity);

        /*
        switch (device) {
            case NNAPI:
                tfliteOptions.setUseNNAPI(true);
                break;
            case GPU:
                gpuDelegate = new GpuDelegate();
                tfliteOptions.addDelegate(gpuDelegate);
                break;
            case CPU:
                break;
        }
        */
        tfliteOptions.setNumThreads(1);
        tflite = new Interpreter(tfliteModel, tfliteOptions);

        Tensor tensor = tflite.getInputTensor(0);
        int[] shape = tensor.shape();
        int inputSize = shape[1];
        int inputChannels = shape[3];
        imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * inputChannels * 4);
        imgData.order(ByteOrder.nativeOrder());

        LOGGER.d("Created a Tensorflow Lite Image Poser.");
    }

    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /** Writes Image data into a {@code ByteBuffer}. */
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        long startTime = SystemClock.uptimeMillis();
        ImageFeeder.feedInputTensor(tflite, bitmap, 125.0f, 125.0f, imgData);

        long endTime = SystemClock.uptimeMillis();
        LOGGER.v("Timecost to put values into ByteBuffer: " + (endTime - startTime));
    }

    /** Runs inference and returns the classification results. */
    public SkeletonPoint[] skeletonImage(final Bitmap bitmap) {
        // Log this method so that it can be analyzed with systrace.
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_WIDTH, INPUT_HEIGHT, false);
        convertBitmapToByteBuffer(resizedBitmap);
        Trace.endSection();

        // Run the inference call.
        Trace.beginSection("runInference");
        long startTime = SystemClock.uptimeMillis();
        runInference();
        long endTime = SystemClock.uptimeMillis();
        Trace.endSection();
        LOGGER.v("Timecost to run model inference: " + (endTime - startTime));

        // Stringify results

        Trace.endSection();
        // return poseString;
        return skelPoints;
    }

    /** Closes the interpreter and model to release resources. */
    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
        if (gpuDelegate != null) {
            gpuDelegate.close();
            gpuDelegate = null;
        }
        tfliteModel = null;
    }

    /**
     * Get the name of the model file stored in Assets.
     *
     * @return
     */
    protected String getModelPath() {
        return "posenet_mv1_075_float_from_checkpoints.tflite";
    }

    protected void runInference() {
        Object[] inputArray = {imgData};

        outputMap.put(0, out1);
        outputMap.put(1, out2);
        outputMap.put(2, out3);
        outputMap.put(3, out4);

        tflite.runForMultipleInputsOutputs(inputArray,outputMap);

        poseDecoder = new PoseDecoder();
        skelPoints = poseDecoder.decodePose(outputMap);
    }

}
