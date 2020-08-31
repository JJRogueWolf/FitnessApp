/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.perfit.fitness.tflite;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.perfit.fitness.googlemodel.Displacements;
import org.perfit.fitness.googlemodel.HeatmapScores;
import org.perfit.fitness.googlemodel.MultiPoseDecoder;
import org.perfit.fitness.googlemodel.Offsets;
import org.perfit.fitness.googlemodel.Pose;
import org.perfit.fitness.utilities.Logger;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A classifier specialized to label images using TensorFlow Lite. */
class Poser {
    private static final Logger LOGGER = new Logger();

    private static final int INPUT_HEIGHT = 353;
    private static final int INPUT_WIDTH = 257;
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
    private final Map<Integer, Object> outputMap = new HashMap<>();
    private final float[][][][] out1 = new float[1][22][22][17];
    private final float[][][][] out2 = new float[1][22][22][34];
    private final float[][][][] out3 = new float[1][22][22][32];
    private final float[][][][] out4 = new float[1][22][22][32];

    /** The loaded TensorFlow Lite model. */
    private MappedByteBuffer tfliteModel;

    /** Optional GPU delegate for accleration. */
    private GpuDelegate gpuDelegate = null;

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
    private Interpreter tflite;

    private Pose pose;

    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
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
    private String getModelPath() {
        return "multi_person_mobilenet_v1.tflite";
        // return "posenet_mv1_075_float_from_checkpoints.tflite";
    }

    /**
     * Run inference using the prepared input in {@link #imgData}. Afterwards, the result will be
     * provided by getProbability().
     *
     * <p>This additional method is necessary, because we don't have a common base for different
     * primitive data types.
     */
    private void runInference() {
        Object[] inputArray = {inputByteBuffer};
        //inputs[0] = inp;

        outputMap.put(0, out1);
        outputMap.put(1, out2);
        outputMap.put(2, out3);
        outputMap.put(3, out4);

        Map<Integer, Object> outputMap = new HashMap<>();
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

        if(poses.size() > 0) {
            pose = poses.get(0);
            // pose.calculateScaledPose(new Size(INPUT_WIDTH, INPUT_HEIGHT), new Size(480, 640),0, 0);
        }
        else
            pose = new Pose();

        // for(Pose pose : poses) {
        //     pose.calculateScaledPose(modelInputSize, preparedImage.getTargetInferenceSize(), preparedImage.getOffsetX(), preparedImage.getOffsetY());
        // }
        // poseString = decoder.decodePose(outputMap);
    }

}
