package org.perfit.fitness.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SessionManager {

    private final SharedPreferences ref;
    private final SharedPreferences.Editor editor;
    private final Context _context;
    private static final String PREF_NAME = "LoginDetails";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_USERID = "userId";
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_SKIP_ALL_VIDEO = "first_time";

    public static final String KEY_HEIGHT = "height";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_JUMPING_SCORE = "jumping_score";
    public static final String KEY_JUMPING_TIME = "jumping_time";
    public static final String KEY_PLANK_SCORE = "plank_count";
    public static final String KEY_PLANK_TIME = "plank_time";
    public static final String KEY_SQUAT_SCORE = "squat_score";
    public static final String KEY_SQUAT_COUNT = "squat_count";
    public static final String KEY_SQUAT_TIME = "squat_time";
    public static final String KEY_PUSHUP_SCORE = "pushup_score";
    public static final String KEY_PUSHUP_COUNT = "pushup_count";
    public static final String KEY_PUSHUP_TIME = "pushup_time";
    public static final String KEY_FLEXIBILITY_SCORE = "flexibility_score";
    public static final String KEY_FLEXIBILITY_COUNT = "flexibility_count";
    public static final String KEY_FLEXIBILITY_TIME = "flexibility_time";
    public static final String KEY_HIGHSCORE = "highscore";
    public static final String KEY_AGE = "age";

//    Image Path
    public static String KEY_SQUATS_IMAGEPATH = "squat_imagepath";
    public static String KEY_PUSHUP_IMAGEPATH = "pushup_imagepath";
    public static String KEY_PLANK_IMAGEPATH = "plank_imagepath";
    public static String KEY_JUMPING_IMAGEPATH = "jumping_imagepath";

    public static final String KEY_IMAGEPATH = "image_path";

    public static final String KEY_BEST_PLANK_SCORE = "best_plank_score";
    public static final String KEY_BEST_PUSHUP_SCORE = "best_pushup_score";
    public static final String KEY_BEST_SQUAT_SCORE = "best_squat_score";
    public static final String KEY_BEST_FLEX_SCORE = "best_score_flex";
    public static final String KEY_BEST_JUMPING_JACKS_SCORE = "best_jumping_jacks_score";
    public static final String KEY_TOTAL_PUSHUP_TIME = "total_pushup_time";
    public static final String KEY_TOTAL_PLANK_TIME = "total_plank_time";
    public static final String KEY_TOTAL_SQUAT_TIME = "total_squat_time";
    public static final String KEY_TOTAL_JUMPING_JACKS_TIME = "total_jumping_jacks_time";
    private static final String KEY_TOTAL_FLEX_TIME = "total_flex_time";
    public static final String KEY_TOTAL_PUSHUP_COUNT = "total_pushup_count";
    public static final String KEY_TOTAL_SQUAT_COUNT = "total_squat_count";
    private static final String KEY_TOTAL_FLEX_COUNT = "total_flex_count";

    public static final String KEY_PROFILE_PICTURE = "profilepicture";

    public static final String KEY_INTRO_VIDEO = "intro_video";
    public static final String KEY_SQUAT_VIDEO = "squat_video";
    public static final String KEY_FLEXIBILITY_VIDEO = "flexibilty_video";
    public static final String KEY_PUSHUP_VIDEO = "pushup_video";
    public static final String KEY_PLANK_VIDEO = "plank_video";
    public static final String KEY_JUMPING_JACKS_VIDEO = "jumping_jacks_video";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        int PRIVATE_MODE = 0;
        ref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = ref.edit();
    }

    public void addSquatImagePath(String filename){
        editor.putString(KEY_SQUATS_IMAGEPATH,filename);
        editor.commit();
    }

    public void addPushupImagePath(String filename){
        editor.putString(KEY_PUSHUP_IMAGEPATH,filename);
        editor.commit();
    }

    public void addPlankImagePath(String filename){
        editor.putString(KEY_PLANK_IMAGEPATH,filename);
        editor.commit();
    }

    public void addJumpingImagePath(String filename){
        editor.putString(KEY_JUMPING_IMAGEPATH,filename);
        editor.commit();
    }

    public void addgender(String gender) {
        editor.putString(KEY_GENDER, gender);
        editor.commit();
    }

    public void addHeight(String height) {
        editor.putString(KEY_HEIGHT, height);
        editor.commit();
    }

    public void addWeight(String weight) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_WEIGHT, weight);
        editor.commit();
    }

    public void addSquatCount(int count) {
        editor.putInt(KEY_SQUAT_COUNT, count);
        editor.commit();
    }


    public void addPushupCount(int count) {
        editor.putInt(KEY_PUSHUP_COUNT, count);
        editor.commit();
    }


    public void addFlexibiltyCount(int count) {
        editor.putInt(KEY_FLEXIBILITY_COUNT, count);
        editor.commit();
    }

    public void addFlexibilityTime(int time) {
        editor.putInt(KEY_FLEXIBILITY_TIME, time);
        editor.commit();
    }

    public void addAge(int age) {
        editor.putInt(KEY_AGE, age);
        editor.commit();
    }

    public void addPlankTime(int time) {
        editor.putInt(KEY_PLANK_TIME, time);
        editor.commit();
    }

    public void addJumpingTime(int time) {
        editor.putInt(KEY_JUMPING_TIME, time);
        editor.commit();
    }

    public void addPushupTime(int time) {
        editor.putInt(KEY_PUSHUP_TIME, time);
        editor.commit();
    }

    public void addSquatTime(int time) {
        editor.putInt(KEY_SQUAT_TIME, time);
        editor.commit();
    }

    public void addSquatScore(float score) {
        editor.putFloat(KEY_SQUAT_SCORE, score);
        editor.commit();
    }

    public void addPushupScore(float score) {
        editor.putFloat(KEY_PUSHUP_SCORE, score);
        editor.commit();
    }

    public void addFlexibiltyScore(float score) {
        editor.putFloat(KEY_FLEXIBILITY_SCORE, score);
        editor.commit();
    }

    public void addPlankScore(float score) {
        editor.putFloat(KEY_PLANK_SCORE, score);
        editor.commit();
    }

    public void addJumpingScore(float score){
        editor.putFloat(KEY_JUMPING_SCORE, score);
        editor.commit();
    }

    public void addSkipAllVideoBoolean(boolean first) {
        editor.putBoolean(KEY_SKIP_ALL_VIDEO, first);
        editor.commit();
    }

    public void addVideoUrls(String introUrl, String squatUrl, String flexibilityUrl, String pushupUrl, String plankUrl, String jumpingJacksUrl){
        editor.putString(KEY_INTRO_VIDEO, introUrl);
        editor.putString(KEY_SQUAT_VIDEO, squatUrl);
        editor.putString(KEY_FLEXIBILITY_VIDEO, flexibilityUrl);
        editor.putString(KEY_PUSHUP_VIDEO, pushupUrl);
        editor.putString(KEY_PLANK_VIDEO, plankUrl);
        editor.putString(KEY_JUMPING_JACKS_VIDEO, jumpingJacksUrl);
        editor.commit();
    }


    public void addBestAndTotal(float pushscore, float plankscore, float flexscore, float squatscore,
                                int pushupcount, int pushuptime, int squatcount, int squattime, int flexcount,
                                int flextime, int planktime, float jumpscore, int jumptime, float totalscoremetric) {
        editor.putFloat(KEY_BEST_PUSHUP_SCORE, pushscore);
        editor.putFloat(KEY_BEST_PLANK_SCORE, plankscore);
        editor.putFloat(KEY_BEST_SQUAT_SCORE, squatscore);
        editor.putFloat(KEY_BEST_FLEX_SCORE, flexscore);
        editor.putInt(KEY_TOTAL_PUSHUP_COUNT, pushupcount);
        editor.putInt(KEY_TOTAL_PUSHUP_TIME, pushuptime);
        editor.putInt(KEY_TOTAL_SQUAT_COUNT, squatcount);
        editor.putInt(KEY_TOTAL_SQUAT_TIME, squattime);
        editor.putInt(KEY_TOTAL_FLEX_COUNT, flexcount);
        editor.putInt(KEY_TOTAL_FLEX_TIME, flextime);
        editor.putInt(KEY_TOTAL_PLANK_TIME, planktime);
        editor.putInt(KEY_TOTAL_JUMPING_JACKS_TIME,jumptime);
        editor.putFloat(KEY_BEST_JUMPING_JACKS_SCORE,jumpscore);
        editor.putFloat(KEY_HIGHSCORE, totalscoremetric);
        editor.commit();
    }

    public void initializeAllCounts() {
        editor.putInt(KEY_PLANK_TIME, 0);
        editor.putInt(KEY_SQUAT_COUNT, -1);
        editor.putInt(KEY_PUSHUP_COUNT, -1);
        editor.putInt(KEY_FLEXIBILITY_COUNT, -1);
        editor.putInt(KEY_JUMPING_TIME,0);
        editor.commit();
    }

    public void addLoginSession(int userId, String name, String email, String profile_picture, String gender, String height, String weight, int age) {
        editor.putBoolean(IS_LOGIN, true);
//        editor.putBoolean(KEY_FIRST_TIME, firstTime);
        editor.putInt(KEY_USERID, userId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROFILE_PICTURE, profile_picture);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_HEIGHT, height);
        editor.putString(KEY_WEIGHT, weight);
        editor.putInt(KEY_AGE, age);
        editor.commit();
    }

    public void addUserId(int userid) {
        editor.putInt(KEY_USERID, userid);
        editor.commit();
    }

    public void createLoginSession(int userId, String name, String email, String profile_picture) {
        editor.putInt(KEY_USERID, userId);
        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing profile picture link in pref
        editor.putString(KEY_PROFILE_PICTURE, profile_picture);

        // commit changes
        editor.commit();
    }

    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, GetStarted.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        } else {
            Intent i = new Intent(_context, BaseDashBoard.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

        }

    }

    HashMap<String,String> sendvideoUrls(){
        HashMap<String,String> videolist = new HashMap<>();
        videolist.put(KEY_INTRO_VIDEO, ref.getString(KEY_INTRO_VIDEO, null));
        videolist.put(KEY_SQUAT_VIDEO, ref.getString(KEY_SQUAT_VIDEO, null));
        videolist.put(KEY_FLEXIBILITY_VIDEO, ref.getString(KEY_FLEXIBILITY_VIDEO, null));
        videolist.put(KEY_PUSHUP_VIDEO, ref.getString(KEY_PUSHUP_VIDEO, null));
        videolist.put(KEY_PLANK_VIDEO, ref.getString(KEY_PLANK_VIDEO, null));
        videolist.put(KEY_JUMPING_JACKS_VIDEO, ref.getString(KEY_JUMPING_JACKS_VIDEO, null));
        return videolist;
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        // user name
        user.put(KEY_NAME, ref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, ref.getString(KEY_EMAIL, null));

        user.put(KEY_HEIGHT, ref.getString(KEY_HEIGHT, null));

        user.put(KEY_WEIGHT, ref.getString(KEY_WEIGHT, null));

        user.put(KEY_GENDER, ref.getString(KEY_GENDER, null));

//        user.put(KEY_SCORE, ref.getString(KEY_SCORE, null));

        user.put(KEY_PROFILE_PICTURE, ref.getString(KEY_PROFILE_PICTURE, null));

        // return user
        return user;
    }

    public HashMap<String,Boolean> getFirstUserCheck(){
        HashMap<String,Boolean> usercheck = new HashMap<>();
        usercheck.put(KEY_SKIP_ALL_VIDEO, ref.getBoolean(KEY_SKIP_ALL_VIDEO, true));
        return usercheck;
    }

    public HashMap<String, Integer> getCountTime() {
        HashMap<String, Integer> score = new HashMap<>();
        score.put(KEY_PLANK_TIME, ref.getInt(KEY_PLANK_TIME, 0));
        score.put(KEY_SQUAT_TIME, ref.getInt(KEY_SQUAT_TIME, 0));
        score.put(KEY_PUSHUP_TIME, ref.getInt(KEY_PUSHUP_TIME, 0));
        score.put(KEY_FLEXIBILITY_TIME, ref.getInt(KEY_FLEXIBILITY_TIME, 0));
        score.put(KEY_JUMPING_TIME, ref.getInt(KEY_JUMPING_TIME, 0));
        score.put(KEY_PUSHUP_COUNT, ref.getInt(KEY_PUSHUP_COUNT, -1));
        score.put(KEY_SQUAT_COUNT, ref.getInt(KEY_SQUAT_COUNT, -1));
        score.put(KEY_TOTAL_JUMPING_JACKS_TIME,ref.getInt(KEY_TOTAL_JUMPING_JACKS_TIME,0));
        score.put(KEY_FLEXIBILITY_COUNT, ref.getInt(KEY_FLEXIBILITY_COUNT, -1));
        score.put(KEY_TOTAL_PLANK_TIME, ref.getInt(KEY_TOTAL_PLANK_TIME, 0));
        score.put(KEY_TOTAL_PUSHUP_TIME, ref.getInt(KEY_TOTAL_PUSHUP_TIME, 0));
        score.put(KEY_TOTAL_PUSHUP_COUNT, ref.getInt(KEY_TOTAL_PUSHUP_COUNT, -1));
        score.put(KEY_TOTAL_SQUAT_TIME, ref.getInt(KEY_TOTAL_SQUAT_TIME, 0));
        score.put(KEY_TOTAL_SQUAT_COUNT, ref.getInt(KEY_TOTAL_SQUAT_COUNT, -1));
        score.put(KEY_TOTAL_FLEX_TIME, ref.getInt(KEY_TOTAL_FLEX_TIME, 0));
        score.put(KEY_TOTAL_FLEX_COUNT, ref.getInt(KEY_TOTAL_FLEX_COUNT, -1));

        return score;
    }

    HashMap<String, Float> getScore() {
        HashMap<String, Float> values = new HashMap<>();
        values.put(KEY_PLANK_SCORE, ref.getFloat(KEY_PLANK_SCORE, -1));
        values.put(KEY_PUSHUP_SCORE, ref.getFloat(KEY_PUSHUP_SCORE, -1));
        values.put(KEY_SQUAT_SCORE, ref.getFloat(KEY_SQUAT_SCORE, -1));
        values.put(KEY_FLEXIBILITY_SCORE, ref.getFloat(KEY_FLEXIBILITY_SCORE, -1));
        values.put(KEY_JUMPING_SCORE, ref.getFloat(KEY_JUMPING_SCORE,-1));
        values.put(KEY_BEST_PUSHUP_SCORE, ref.getFloat(KEY_BEST_PUSHUP_SCORE, -1));
        values.put(KEY_BEST_PLANK_SCORE, ref.getFloat(KEY_BEST_PLANK_SCORE, -1));
        values.put(KEY_BEST_SQUAT_SCORE, ref.getFloat(KEY_BEST_SQUAT_SCORE, -1));
        values.put(KEY_BEST_FLEX_SCORE, ref.getFloat(KEY_BEST_FLEX_SCORE, -1));
        values.put(KEY_BEST_JUMPING_JACKS_SCORE, ref.getFloat(KEY_BEST_JUMPING_JACKS_SCORE, -1));
        values.put(KEY_HIGHSCORE, ref.getFloat(KEY_HIGHSCORE, -1));
        return values;
    }

    public HashMap<String, Integer> getTimeAge() {
        HashMap<String, Integer> time = new HashMap<>();
        time.put(KEY_AGE, ref.getInt(KEY_AGE, 0));
        time.put(KEY_PLANK_TIME, ref.getInt(KEY_PLANK_TIME, 0));
        time.put(KEY_SQUAT_TIME, ref.getInt(KEY_SQUAT_TIME, 0));
        time.put(KEY_PUSHUP_TIME, ref.getInt(KEY_PUSHUP_TIME, 0));
        time.put(KEY_JUMPING_TIME, ref.getInt(KEY_JUMPING_TIME, 0));
        time.put(KEY_FLEXIBILITY_TIME, ref.getInt(KEY_FLEXIBILITY_TIME, 0));
        return time;
    }

    public HashMap<String, Integer> getUserId() {
        HashMap<String, Integer> sentUserID = new HashMap<>();
        sentUserID.put(KEY_USERID, ref.getInt(KEY_USERID, -1));
        return sentUserID;
    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        clearPref();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainLogin.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    private void clearPref() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

    }


    private boolean isLoggedIn() {
        return ref.getBoolean(IS_LOGIN, false);
    }
}
