package org.perfit.fitness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.perfit.fitness.R;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.VolleyCallback;

import java.util.HashMap;

public class MoreComing extends AppCompatActivity {

    private AppCompatButton moreComing;
    private SessionManager completeupdateManager;

    private float pushupScore, squatScore, flexScore, plankScore, jumpScore;
    private int pushupCount, squatCount, flexCount, pushupTime, squatTime, flexTime, plankTime, jumpTime, userIdScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_more_coming);
        moreComing = findViewById(R.id.back_to_home);
        completeupdateManager = new SessionManager(getApplicationContext());

        HashMap<String,Integer> getscore = completeupdateManager.getCountTime();
        pushupCount = getscore.get(SessionManager.KEY_PUSHUP_COUNT);
        pushupTime = getscore.get(SessionManager.KEY_PUSHUP_TIME);
        squatCount = getscore.get(SessionManager.KEY_SQUAT_COUNT);
        squatTime = getscore.get(SessionManager.KEY_SQUAT_TIME);
        flexCount = getscore.get(SessionManager.KEY_FLEXIBILITY_COUNT);
        flexTime = getscore.get(SessionManager.KEY_FLEXIBILITY_TIME);
        plankTime = getscore.get(SessionManager.KEY_PLANK_TIME);
        jumpTime = getscore.get(SessionManager.KEY_JUMPING_TIME);

        HashMap<String,Float> getvalues = completeupdateManager.getScore();
        pushupScore = getvalues.get(SessionManager.KEY_PUSHUP_SCORE);
        plankScore = getvalues.get(SessionManager.KEY_PLANK_SCORE);
        squatScore = getvalues.get(SessionManager.KEY_SQUAT_SCORE);
        flexScore = getvalues.get(SessionManager.KEY_FLEXIBILITY_SCORE);
        jumpScore = getvalues.get(SessionManager.KEY_JUMPING_SCORE);

        HashMap<String,Integer> userIdhere = completeupdateManager.getUserId();
        userIdScore = userIdhere.get(SessionManager.KEY_USERID);

        moreComing.setVisibility(View.GONE);
        callScoreUpdateAPI();
    }
    private void callScoreUpdateAPI(){

        JSONObject upDateParameters = new JSONObject();
        try {
            upDateParameters.put("userId",userIdScore);
            upDateParameters.put("plankScore",plankScore);
            upDateParameters.put("plankTime",plankTime);
            upDateParameters.put("jumpingJacksScore",jumpScore);
            upDateParameters.put("jumpingJacksTime",jumpTime);
            upDateParameters.put("squatScore",squatScore);
            upDateParameters.put("squatCount",squatCount);
            upDateParameters.put("squatTime",squatTime);
            upDateParameters.put("pushupScore",pushupScore);
            upDateParameters.put("pushupCount",pushupCount);
            upDateParameters.put("pushupTime",pushupTime);
            upDateParameters.put("flexScore",flexScore);
            upDateParameters.put("flexCount",flexCount);
            upDateParameters.put("flexTime",flexTime);
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
                Toast.makeText(MoreComing.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                completeupdateManager.logoutUser();
            }
        });

    }

    private void handleSuccessResponce(JSONObject result){
        try {
            switch (result.getString("status")) {
                case "2":
                    Toast.makeText(MoreComing.this, "sorry, Something went wrong", Toast.LENGTH_SHORT).show();
                    break;
                case "1":
                    completeupdateManager.addBestAndTotal(
                            (float)result.getDouble("best_pushup_score"),
                            (float)result.getDouble("best_plank_score"),
                            (float)result.getDouble("best_flex_score"),
                            (float)result.getDouble("best_squat_score"),
                            result.getInt("total_pushup_count"),
                            result.getInt("total_pushup_time"),
                            result.getInt("total_squat_count"),
                            result.getInt("total_squat_time"),
                            result.getInt("total_flex_count"),
                            result.getInt("total_flex_time"),
                            result.getInt("total_plank_time"),
                            (float)result.getDouble("total_jumping_jacks_score"),
                            result.getInt("total_jumping_jacks_time"),
                            (float)result.getDouble("total_score_metric"));
                    moreComing.setVisibility(View.VISIBLE);
//                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    moreComing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent back_to_home = new Intent(getApplicationContext(), BaseDashBoard.class);
                            startActivity(back_to_home);
                            finish();
                        }
                    });
                    break;
                default:
                    Log.i("Error", "Server");
                    Toast.makeText(getApplicationContext(), "Unknown Error", Toast.LENGTH_LONG).show();
                    moreComing.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unknown error", Toast.LENGTH_SHORT).show();
            Log.i("Error",e.getMessage());
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce){
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
}
