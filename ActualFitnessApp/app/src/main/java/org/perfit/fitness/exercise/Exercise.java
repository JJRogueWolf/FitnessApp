package org.perfit.fitness.exercise;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Chronometer;

import org.json.JSONException;
import org.json.JSONObject;
import org.perfit.fitness.tflite.SkeletonPoint;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.VolleyCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class Exercise {
    SkeletonPoint[] keypoints;

    // Added by Sai
    int humanPoseCount = 0;
    int correctPositionCount = 0;
    long timeLimitBeforeDetection = 30000000000L;
    long timeLimitBeforeEncouragement = 2000000000L;
    long timeLimitAfterDetection =  10000000000L;
    long firstCountIncreasedTime = -1;
    long lastCountIncreasedTime = -1;
    long timeLastSpoken = -1L;
    boolean setupExerciseScreenDone = false;
    public TextToSpeech textToSpeech;
    String introSpeech;
//    Debug
    public static int getUserID;
    public static String debugImageProcessTime;
    public static String printList;
    public static int currentExerciseId;
    public static int updateStringCheck = 0;

    public int rotationDegree;
    final Activity activity;
    long startGlobalTime;

    Chronometer mChronometer;

    float score = -1;
    public int countOrTime= 0;

    public Exercise(Activity activity) {
        startGlobalTime = System.nanoTime();
        this.activity = activity;

        textToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    public void processPoints(SkeletonPoint[] poseKeypoints) {
        keypoints = poseKeypoints;
//        if(keypoints != null) Arrays.sort(keypoints);
    }

    public boolean isSkeletonEmpty() {
        boolean allElementsEmpty = true;

        if (keypoints == null)
            return false;

        for (SkeletonPoint skelPoint : keypoints
        ) {
            if (skelPoint != null) {
                allElementsEmpty = false;
                break;
            }
        }

        return allElementsEmpty;
    }

    public void speakText(final String message) {
        speakTextWithFrequency(message, 3000000000L);
        // Don't speak if last spoken was 3s ago
    }

    void speakTextWithFrequency(final String message, long speechFrequency) {
        // Don't speak if last spoken was 3s ago
        if((timeLastSpoken !=-1L) && ((System.nanoTime() - timeLastSpoken) < speechFrequency))
            return;

        if(!textToSpeech.isSpeaking()) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            timeLastSpoken = System.nanoTime();
        }
    }

    private float getLineAngle(int a, int b) {
        float dot = keypoints[a].getPosition().x * keypoints[b].getPosition().x
                + keypoints[a].getPosition().y * keypoints[b].getPosition().y; // dot product
        float det = keypoints[a].getPosition().x * keypoints[b].getPosition().y
                - keypoints[a].getPosition().y * keypoints[b].getPosition().x; // determinant
        return (float) Math.toDegrees(Math.atan2(det, dot));
    }

    float getLineAngleLegNeckSecond(int a, SkeletonPoint neckPoint){
        float dx = neckPoint.getPosition().x - keypoints[a].getPosition().x;
        float dy = neckPoint.getPosition().y - keypoints[a].getPosition().y;
        return (float) Math.toDegrees(Math.atan2(dy,dx));
    }

    float getAngleBetweenPointsNeckFirst(SkeletonPoint neckPoint, int b, int c) {
        if (neckPoint.getPosition().x == -1.0f || keypoints[b].getPosition().x == -1.0f
                || keypoints[c].getPosition().x == -1.0f)
            return -1.0f;
        double angle = Math.toDegrees(Math.atan2(keypoints[c].getPosition().y - keypoints[b].getPosition().y,
                keypoints[c].getPosition().x - keypoints[b].getPosition().x)
                - Math.atan2(neckPoint.getPosition().y - keypoints[b].getPosition().y,
                neckPoint.getPosition().x - keypoints[b].getPosition().x));
        return (float) angle;
    }

    public void callAPIDataCollection() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userId", getUserID);
            parameters.put("exerciseId", currentExerciseId);
            parameters.put("toWrite", printList + " || " + debugImageProcessTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
            parameters.put("dateTime", sdf.format(new Date()));

//            currentDateandTime = sdf.format(new Date());
//            writeToFile("Skeletal Points ==>\n" + skeletalPoints + "\nImage Process Time ==>\n" + debugImageProcessTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIManager.CallAPI(APIManager.DATA_COLLECTION, parameters, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                printList = "";
                debugImageProcessTime = "";
                try {
                    switch (result.getString("status")) {
                        case "1":
                        default:
                            Log.e("Status", "Log: " + result.getString("status"));
                            break;
                    }
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }
            }

            @Override
            public void onFailureResponse(String result) {
                printList = "";
                debugImageProcessTime = "";
                Log.e("Connection Failed", "Error: " + result);
            }
        });
    }
}
