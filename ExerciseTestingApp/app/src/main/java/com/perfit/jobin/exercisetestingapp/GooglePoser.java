package com.perfit.jobin.exercisetestingapp;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.os.Trace;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GooglePoser implements SkeletonPoser {
    private static final Logger LOGGER = new Logger();

    public static final int INPUT_HEIGHT = 353;
    public static final int INPUT_WIDTH = 257;
//    public static final int INPUT_HEIGHT = 225;
//    public static final int INPUT_WIDTH = 225;
    private static final int NUM_CHANNELS = 3;
    private static final int HEIGHT = 23;
    private static final int WIDTH = 17;
    private static final int NUM_PARTS = 17;
    private static final int NUM_EDGES = 16;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    private static final int LOCAL_MAX_RADIUS = 1;
    private static final int OUTPUT_STRIDE = 16;

    private int[] intValues;

    private ByteBuffer inputByteBuffer;
    private ByteBuffer outputHeatmaps;
    private ByteBuffer outputOffsets;
    private ByteBuffer outputDisplacementsFwd;
    private ByteBuffer outputDisplacementsBwd;
    private ByteBuffer outputSegments;

    /**
     * An array to hold inference results, to be feed into Tensorflow Lite as outputs. This isn't part
     * of the super class, because we need a primitive array here.
     */
    Map<Integer, Object> outputMap = new HashMap<>();
    /** Options for configuring the Interpreter. */
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    /** The loaded TensorFlow Lite model. */
    private MappedByteBuffer tfliteModel;

    /** Optional GPU delegate for accleration. */
    private GpuDelegate gpuDelegate = null;

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
    protected Interpreter tflite;

    protected Pose pose;
    private SkeletonPoint[] skelPoints;

    /** Initializes a {@code Poser}. */
    public GooglePoser(Activity activity) throws IOException {
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

        this.intValues = new int[INPUT_WIDTH * INPUT_HEIGHT];

        this.inputByteBuffer = ByteBuffer.allocateDirect(4 * INPUT_WIDTH * INPUT_HEIGHT * NUM_CHANNELS);
        inputByteBuffer.order(ByteOrder.nativeOrder());

        outputHeatmaps = ByteBuffer.allocateDirect(4 * HEIGHT * WIDTH * NUM_PARTS);
        outputOffsets = ByteBuffer.allocateDirect(4 * HEIGHT * WIDTH * NUM_PARTS * 2);
        outputDisplacementsFwd = ByteBuffer.allocateDirect(4 * HEIGHT * WIDTH * NUM_EDGES * 2);
        outputDisplacementsBwd = ByteBuffer.allocateDirect(4 * HEIGHT * WIDTH * NUM_EDGES * 2);
        outputSegments = ByteBuffer.allocateDirect(4 * HEIGHT * WIDTH);

        outputHeatmaps.order(ByteOrder.nativeOrder());
        outputOffsets.order(ByteOrder.nativeOrder());
        outputDisplacementsFwd.order(ByteOrder.nativeOrder());
        outputDisplacementsBwd.order(ByteOrder.nativeOrder());
        outputSegments.order(ByteOrder.nativeOrder());

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
    /** Runs inference and returns the classification results. */
    public SkeletonPoint[] skeletonImage(final Bitmap bitmap) {
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("skeletonImagePreprocess");
        rewindOutputs();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_WIDTH, INPUT_HEIGHT, false);
        preprocess(resizedBitmap);
        Trace.endSection();

        // Run the inference call.
        Trace.beginSection("runInference");
        long startTime = SystemClock.uptimeMillis();
        runInference();
        long endTime = SystemClock.uptimeMillis();
        Trace.endSection();
        LOGGER.v("Timecost to run model inference: " + (endTime - startTime));
        Trace.endSection();

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

    private void rewindOutputs() {
        outputHeatmaps.rewind();
        outputOffsets.rewind();
        outputDisplacementsFwd.rewind();
        outputDisplacementsBwd.rewind();
        outputSegments.rewind();
    }

    private void preprocess(Bitmap bitmap) {
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        inputByteBuffer.rewind();

        for (int row = 0; row < INPUT_HEIGHT; row++) {
            for (int col = 0; col < INPUT_WIDTH; col++) {
                int pixel = intValues[row * INPUT_WIDTH + col];

                float blue = (float) ((pixel & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                float green = (float) (((pixel >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                float red = (float) (((pixel >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;

                inputByteBuffer.putFloat(red);
                inputByteBuffer.putFloat(green);
                inputByteBuffer.putFloat(blue);
            }
        }
    }

    /**
     * Get the name of the model file stored in Assets.
     *
     * @return
     */
    protected String getModelPath() {
        return "multi_person_mobilenet_v1.tflite";
    }

    /**
     * Run inference using the prepared input in {@link #inputByteBuffer}. Afterwards, the result will be
     * provided by getProbability().
     *
     * <p>This additional method is necessary, because we don't have a common base for different
     * primitive data types.
     */
    protected void runInference() {
        Object[] inputArray = {inputByteBuffer};
        //inputs[0] = inp;

        outputMap = new HashMap<>();
        outputMap.put(0, outputHeatmaps);
        outputMap.put(1, outputOffsets);
        outputMap.put(2, outputDisplacementsFwd);
        outputMap.put(3, outputDisplacementsBwd);
        outputMap.put(4, outputSegments);

        tflite.runForMultipleInputsOutputs(inputArray,outputMap);

        HeatmapScores heatmapScores = new HeatmapScores(outputHeatmaps, HEIGHT, WIDTH, NUM_PARTS);
        Displacements displacementFwd = new Displacements(outputDisplacementsFwd, HEIGHT, WIDTH, NUM_EDGES);
        Displacements displacementBwd = new Displacements(outputDisplacementsBwd, HEIGHT, WIDTH, NUM_EDGES);
        Offsets offsets = new Offsets(outputOffsets, HEIGHT, WIDTH, NUM_PARTS);
        MultiPoseDecoder decoder = new MultiPoseDecoder(heatmapScores, offsets, displacementFwd, displacementBwd);


        List<Pose> poses = decoder.decodeMultiplePoses(OUTPUT_STRIDE, 1, .85f, 20, LOCAL_MAX_RADIUS);

        skelPoints = new SkeletonPoint[PoseTypes.PART_NAMES.length];
        if((poses.size() > 0) && (poses.get(0).getKeypoints() != null) ) {
            for(Keypoint kp : poses.get(0).getKeypoints()) {
                if(kp != null)
                    skelPoints[kp.getId()] = new SkeletonPoint(kp);
            }
        }
    }

}

