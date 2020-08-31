/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfit.fitness.posenet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.os.SystemClock
import android.util.Log
import org.perfit.fitness.tflite.SkeletonPoint
import org.perfit.fitness.tflite.SkeletonPoser
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.exp

enum class BodyPart {
  NOSE,
  LEFT_EYE,
  RIGHT_EYE,
  LEFT_EAR,
  RIGHT_EAR,
  LEFT_SHOULDER,
  RIGHT_SHOULDER,
  LEFT_ELBOW,
  RIGHT_ELBOW,
  LEFT_WRIST,
  RIGHT_WRIST,
  LEFT_HIP,
  RIGHT_HIP,
  LEFT_KNEE,
  RIGHT_KNEE,
  LEFT_ANKLE,
  RIGHT_ANKLE
}

class Position {
  var x: Int = 0
  var y: Int = 0
}

class KeyPoint {
  var bodyPart: BodyPart = BodyPart.NOSE
  var position: Position = Position()
  var score: Float = 0.0f
}

class Person {
  var keyPoints = listOf<KeyPoint>()
  var score: Float = 0.0f
}

class Posenet(context: Context): SkeletonPoser {
  override fun close() {
    tflite!!.close()
  }

  /** Model input shape for images.   */
  val MODEL_WIDTH = 257
  val MODEL_HEIGHT = 257

  /** An Interpreter for the TFLite model.   */
  private var tflite: Interpreter? = null

  init {
    tflite = Interpreter(loadModelFile("posenet_model.tflite", context))
  }

  /** Returns value within [0,1].   */
  private fun sigmoid(x: Float): Float {
    return (1.0f / (1.0f + exp(-x)))
  }

  /**
   * Scale the image to a byteBuffer of [-1,1] values.
   */
  private fun initInputArray(bitmap: Bitmap): ByteBuffer {
    val bytesPerChannel = 4
    val inputChannels = 3
    val batchSize = 1
    val inputBuffer = ByteBuffer.allocateDirect(
      batchSize * bytesPerChannel * bitmap.height * bitmap.width * inputChannels
    )
    inputBuffer.order(ByteOrder.nativeOrder())
    inputBuffer.rewind()

    val mean = 128.0f
    val std = 128.0f
    for (row in 0 until bitmap.height) {
      for (col in 0 until bitmap.width) {
        val pixelValue = bitmap.getPixel(col, row)
        inputBuffer.putFloat(((pixelValue shr 16 and 0xFF) - mean) / std)
        inputBuffer.putFloat(((pixelValue shr 8 and 0xFF) - mean) / std)
        inputBuffer.putFloat(((pixelValue and 0xFF) - mean) / std)
      }
    }
    return inputBuffer
  }

  /** Preload and memory map the model file, returning a MappedByteBuffer containing the model. */
  private fun loadModelFile(path: String, context: Context): MappedByteBuffer {
    val fileDescriptor = context.assets.openFd(path)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    return inputStream.channel.map(
      FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength
    )
  }

  /**
   * Initializes an outputMap of 1 * x * y * z FloatArrays for the model processing to populate.
   */
  private fun initOutputMap(interpreter: Interpreter): HashMap<Int, Any> {
    val outputMap = HashMap<Int, Any>()

    // 1 * 17 * 17 * 17 contains heatmaps
    val heatmapsShape = interpreter.getOutputTensor(0).shape()
    outputMap[0] = Array(heatmapsShape[0]) {
      Array(heatmapsShape[1]) {
        Array(heatmapsShape[2]) { FloatArray(heatmapsShape[3]) }
      }
    }

    // 1 * 17 * 17 * 34 contains offsets
    val offsetsShape = interpreter.getOutputTensor(1).shape()
    outputMap[1] = Array(offsetsShape[0]) {
      Array(offsetsShape[1]) { Array(offsetsShape[2]) { FloatArray(offsetsShape[3]) } }
    }

    // 1 * 17 * 17 * 32 contains forward displacements
    val displacementsFwdShape = interpreter.getOutputTensor(2).shape()
    outputMap[2] = Array(offsetsShape[0]) {
      Array(displacementsFwdShape[1]) {
        Array(displacementsFwdShape[2]) { FloatArray(displacementsFwdShape[3]) }
      }
    }

    // 1 * 17 * 17 * 32 contains backward displacements
    val displacementsBwdShape = interpreter.getOutputTensor(3).shape()
    outputMap[3] = Array(displacementsBwdShape[0]) {
      Array(displacementsBwdShape[1]) {
        Array(displacementsBwdShape[2]) { FloatArray(displacementsBwdShape[3]) }
      }
    }

    return outputMap
  }

  /**
   * Estimates the pose for a single person.
   * args:
   *      bitmap: image bitmap of frame that should be processed
   * returns:
   *      person: a Person object containing data about keypoint locations and confidence scores
   */
  override fun skeletonImage(bitmap: Bitmap): Array<SkeletonPoint?> {
    var t1: Long = SystemClock.elapsedRealtimeNanos()
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, MODEL_WIDTH, MODEL_HEIGHT, false)
    val inputArray = arrayOf(initInputArray(resizedBitmap))
    var t2: Long = SystemClock.elapsedRealtimeNanos()
    Log.i("posenet", String.format("Scaling to [-1,1] took %.2f ms", 1.0f * (t2 - t1) / 1_000_000))

    val outputMap = initOutputMap(tflite!!)
    var skelPoints: Array<SkeletonPoint?>

    t1 = SystemClock.elapsedRealtimeNanos()
    tflite!!.runForMultipleInputsOutputs(inputArray, outputMap)
    t2 = SystemClock.elapsedRealtimeNanos()
    Log.i("posenet", String.format("Interpreter took %.2f ms", 1.0f * (t2 - t1) / 1_000_000))



    val heatmaps = outputMap[0] as Array<Array<Array<FloatArray>>>
    val offsets = outputMap[1] as Array<Array<Array<FloatArray>>>

    val height = heatmaps[0].size
    val width = heatmaps[0][0].size
    val numKeypoints = heatmaps[0][0][0].size

    // Finds the (row, col) locations of where the keypoints are most likely to be.
    val keypointPositions = Array(numKeypoints) { Pair(0, 0) }
    for (keypoint in 0 until numKeypoints) {
      var maxVal = heatmaps[0][0][0][keypoint]
      var maxRow = 0
      var maxCol = 0
      for (row in 0 until height) {
        for (col in 0 until width) {
          heatmaps[0][row][col][keypoint] = sigmoid(heatmaps[0][row][col][keypoint])
          if (heatmaps[0][row][col][keypoint] > maxVal) {
            maxVal = heatmaps[0][row][col][keypoint]
            maxRow = row
            maxCol = col
          }
        }
      }
      keypointPositions[keypoint] = Pair(maxRow, maxCol)
    }

    // Calculating the x and y coordinates of the keypoints with offset adjustment.
    val xCoords = IntArray(numKeypoints)
    val yCoords = IntArray(numKeypoints)
    val confidenceScores = FloatArray(numKeypoints)
    keypointPositions.forEachIndexed { idx, position ->
      val positionY = keypointPositions[idx].first
      val positionX = keypointPositions[idx].second
      yCoords[idx] = (
        position.first / (height - 1).toFloat() * bitmap.height +
          offsets[0][positionY][positionX][idx]
        ).toInt()
      xCoords[idx] = (
        position.second / (width - 1).toFloat() * bitmap.width +
          offsets[0][positionY]
          [positionX][idx + numKeypoints]
        ).toInt()
      confidenceScores[idx] = heatmaps[0][positionY][positionX][idx]
    }

    val person = Person()
    val keypointList = Array(numKeypoints) { KeyPoint() }
    var totalScore = 0.0f
    enumValues<BodyPart>().forEachIndexed { idx, it ->
      keypointList[idx].bodyPart = it
      keypointList[idx].position.x = xCoords[idx]
      keypointList[idx].position.y = yCoords[idx]
      keypointList[idx].score = confidenceScores[idx]
      totalScore += confidenceScores[idx]
    }

    person.keyPoints = keypointList.toList()
    person.score = totalScore / numKeypoints

    // Skeleton detected
    if(person.score > 0.4) {
      skelPoints = Array(17) { SkeletonPoint() }
      for (kp in person.keyPoints) {
          skelPoints[kp.bodyPart.ordinal] = SkeletonPoint(kp.bodyPart.ordinal, PointF(kp.position.x.toFloat(), kp.position.y.toFloat()), kp.score)
      }
    }
    // Skeleton not detected
    else {
      skelPoints = Array(17) { null }
    }
    return skelPoints
  }

}
