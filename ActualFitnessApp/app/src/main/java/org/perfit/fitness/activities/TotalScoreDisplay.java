package org.perfit.fitness.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.perfit.fitness.R;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.ImageUploadData;
import org.perfit.fitness.utilities.UploadImage;
import org.perfit.fitness.utilities.VolleyCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TotalScoreDisplay extends AppCompatActivity {
    private static int next_id;
    private TextView relaxTimer;
    private TextToSpeech textToSpeech;

    long timeLastSpoken = -1L;
    private CountDownTimer countDownTimer;
    private SessionManager totalScoreManager;

    public static String debugImageProcessTime;

    private float pushupScore, squatScore, flexScore, plankScore, jumpScore;
    private int pushupCount, squatCount, flexCount, pushupTime, squatTime, flexTime, plankTime, jumpTime, userIdScore;
    private String skeletalPoints;
    private String currentDateandTime;
    private static int userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_total_score_display);
        Button tryAgainExer = findViewById(R.id.tryAgainExercise);
        totalScoreManager = new SessionManager(getApplicationContext());
        TextView countDisp = findViewById(R.id.countDisplay);
        TextView whoaa_disp = findViewById(R.id.whoaa);
        TextView feedback_disp = findViewById(R.id.feedback);
        relaxTimer = findViewById(R.id.relaxTimer);
        HashMap<String, Integer> getUserId = totalScoreManager.getUserId();
        userid = getUserId.get(SessionManager.KEY_USERID);

        TextView nextIn = findViewById(R.id.countText);
        Bundle get_value = getIntent().getExtras();
        int countOrTime = get_value.getInt("countOrTime");
        next_id = get_value.getInt("id");
//        skeletalPoints = get_value.getString("skeletalPointList");
//        callAPIDataCollection();

        String path =
                Environment.getExternalStorageDirectory() + File.separator + "PerfitLogs/" + userid + "/" + next_id;
        // Create the folder.
        File folder = new File(path);
        folder.mkdir();

        DecimalFormat decimalF = new DecimalFormat("#.##");
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    if (!textToSpeech.isSpeaking()) {
                        if (next_id == 3) {
//                            textToSpeech.speak("Congrats, Now you have completed our fitness test", TextToSpeech.QUEUE_FLUSH, null, null);
                        } else {
                            textToSpeech.speak("Relax for 10 seconds", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    }
                }
            }
        });

        tryAgainExer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                Intent goafter = new Intent(getApplicationContext(), VideoManager.class);
                totalScoreManager.addSkipAllVideoBoolean(false);
                Bundle put_exerciseid = new Bundle();
                put_exerciseid.putInt("video_code", next_id);
                put_exerciseid.putInt("gif_code", next_id);
                goafter.putExtras(put_exerciseid);
                goafter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goafter);
                finish();
            }
        });

        if (next_id < 2) {
            nextIn.setText("Count");
            countDisp.setText(String.valueOf(countOrTime));
        } else {
            long min = countOrTime / 60;
            nextIn.setText("Time");
            countDisp.setText(min + ":" + String.format("%02d", countOrTime % 60));
        }

        if (countOrTime < 4) {
            whoaa_disp.setText("Hmmm...");
            feedback_disp.setText("Need Improvement");
        } else {
            whoaa_disp.setText("Whoaaa");
            feedback_disp.setText("Good Job..");
        }

        countDownTimer = new CountDownTimer(11000, 10) {

            @Override
            public void onTick(long l) {
                int countdownvalue = (int) l / 1000;
                DecimalFormat df = new DecimalFormat("0");
                String tickValue = df.format(countdownvalue);
                relaxTimer.setText(tickValue + "s");
//                if (next_id < 2) {
//                    relaxTimer.setText(tickValue + "s");
//                } else {
//                    relaxTimer.setText(tickValue + "s");
//                }

            }

            @Override
            public void onFinish() {
                if (next_id < 3) {
//                if (next_id != 0) {
                    Intent goafter = new Intent(getApplicationContext(), VideoManager.class);
                    Bundle put_exerciseid = new Bundle();
                    put_exerciseid.putInt("video_code", next_id + 1);
                    put_exerciseid.putInt("gif_code", next_id + 1);
                    goafter.putExtras(put_exerciseid);
                    goafter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goafter);
                    finish();
                } else {
                    totalScoreManager.addSkipAllVideoBoolean(true);
                    saveTheScore();
                }
            }
        }.start();

        if (next_id == 3) {
            sendImage(SessionManager.KEY_SQUATS_IMAGEPATH);
            sendImage(SessionManager.KEY_PUSHUP_IMAGEPATH);
            sendImage(SessionManager.KEY_PLANK_IMAGEPATH);
            sendImage(SessionManager.KEY_JUMPING_IMAGEPATH);
            Log.e("Error:", " Default image switch");
        }
    }

    private void saveTheScore() {
        HashMap<String, Integer> getScore = totalScoreManager.getCountTime();
        pushupCount = getScore.get(SessionManager.KEY_PUSHUP_COUNT);
        pushupTime = getScore.get(SessionManager.KEY_PUSHUP_TIME);
        squatCount = getScore.get(SessionManager.KEY_SQUAT_COUNT);
        squatTime = getScore.get(SessionManager.KEY_SQUAT_TIME);
        flexCount = getScore.get(SessionManager.KEY_FLEXIBILITY_COUNT);
        flexTime = getScore.get(SessionManager.KEY_FLEXIBILITY_TIME);
        plankTime = getScore.get(SessionManager.KEY_PLANK_TIME);
        jumpTime = getScore.get(SessionManager.KEY_JUMPING_TIME);


        HashMap<String, Float> getValues = totalScoreManager.getScore();
        pushupScore = getValues.get(SessionManager.KEY_PUSHUP_SCORE);
        plankScore = getValues.get(SessionManager.KEY_PLANK_SCORE);
        squatScore = getValues.get(SessionManager.KEY_SQUAT_SCORE);
        flexScore = getValues.get(SessionManager.KEY_FLEXIBILITY_SCORE);
        jumpScore = getValues.get(SessionManager.KEY_JUMPING_SCORE);

        HashMap<String, Integer> userIdHere = totalScoreManager.getUserId();
        userIdScore = userIdHere.get(SessionManager.KEY_USERID);

        callScoreUpdateAPI();
    }

    private void callScoreUpdateAPI() {

        JSONObject upDateParameters = new JSONObject();
        try {
            upDateParameters.put("userId", userIdScore);
            upDateParameters.put("plankScore", plankScore);
            upDateParameters.put("plankTime", plankTime);
            upDateParameters.put("squatScore", squatScore);
            upDateParameters.put("squatCount", squatCount);
            upDateParameters.put("squatTime", squatTime);
            upDateParameters.put("pushupScore", pushupScore);
            upDateParameters.put("pushupCount", pushupCount);
            upDateParameters.put("pushupTime", pushupTime);
            upDateParameters.put("flexScore", flexScore);
            upDateParameters.put("flexCount", flexCount);
            upDateParameters.put("flexTime", flexTime);
            upDateParameters.put("jumpingJacksTime", jumpTime);
            upDateParameters.put("jumpingJacksScore", jumpScore);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIManager.CallAPI(APIManager.PUSH_SESSION, upDateParameters, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                handleSuccessResponce(result);
            }

            @Override
            public void onFailureResponse(String result) {
                Toast.makeText(TotalScoreDisplay.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                Intent logOutIntent = new Intent(getApplicationContext(), MainLogin.class);
                totalScoreManager.logoutUser();
                logOutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logOutIntent);
                finish();
            }
        });

    }

    private void handleSuccessResponce(JSONObject result) {
        try {
            switch (result.getString("status")) {
                case "2":
                    Toast.makeText(TotalScoreDisplay.this, "sorry, Something went wrong", Toast.LENGTH_SHORT).show();
                    break;
                case "1":
                    totalScoreManager.addBestAndTotal(
                            (float) result.getDouble("best_pushup_score"),
                            (float) result.getDouble("best_plank_score"),
                            (float) result.getDouble("best_flex_score"),
                            (float) result.getDouble("best_squat_score"),
                            result.getInt("total_pushup_count"),
                            result.getInt("total_pushup_time"),
                            result.getInt("total_squat_count"),
                            result.getInt("total_squat_time"),
                            result.getInt("total_flex_count"),
                            result.getInt("total_flex_time"),
                            result.getInt("total_plank_time"),
                            (float) result.getDouble("total_jumping_jacks_score"),
                            result.getInt("total_jumping_jacks_time"),
                            (float) result.getDouble("total_score_metric"));
                    Intent goEnd = new Intent(getApplicationContext(), BaseDashBoard.class);
                    goEnd.putExtra("fragToSee", BaseDashBoard.FRAG_ACHIEVEMENT);
                    goEnd.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goEnd);
                    finish();
//                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Log.i("Error", "Server");
                    Toast.makeText(getApplicationContext(), "Unknown Error", Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unknown error", Toast.LENGTH_SHORT).show();
            Log.i("Error", e.getMessage());
        }
    }

    private void sendImage(String fileName) {
        try {
            ImageUploadData imageUploadData = new ImageUploadData();
            imageUploadData.userId = String.valueOf(userid);
            imageUploadData.exerciseId = String.valueOf(next_id);
            imageUploadData.imagePath = fileName;
            imageUploadData.upLoadServerUri = APIManager.UPLOAD_IMAGES;
            imageUploadData.volleyCallback = new VolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject result) {

                }

                @Override
                public void onFailureResponse(String result) {

                }
            };
            new UploadImage().execute(imageUploadData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(String data) {
        try {
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "PerfitLogs/" + userid + "/" + next_id + "/" + currentDateandTime + ".txt", true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }
    //    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce){
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//            System.exit(0);
//        }
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        },2000);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.stop();
        textToSpeech.shutdown();
    }

    @Override
    public void onBackPressed() {
    }
}
