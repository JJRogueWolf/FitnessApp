package org.perfit.fitness.model;

import com.google.gson.annotations.SerializedName;

class ScoreDetails {

    @SerializedName("user_id")
    private int userId;
    @SerializedName("best_plank_score")
    private int plankScore;
    @SerializedName("total_plank_time")
    private int plankTime;
    @SerializedName("best_squat_score")
    private int squatScore;
    @SerializedName("total_squat_count")
    private int squatCount;
    @SerializedName("total_squat_time")
    private int squatTime;
    @SerializedName("best_pushup_score")
    private int pushupScore;
    @SerializedName("total_pushup_count")
    private int pushupCount;
    @SerializedName("total_pushup_time")
    private int pushupTime;
    @SerializedName("best_flex_score")
    private int flexScore;
    @SerializedName("total_flex_count")
    private int flexCount;
    @SerializedName("total_flex_time")
    private int flexTime;

}
