package org.perfit.fitness.model;

import com.google.gson.annotations.SerializedName;

public class UserDetails {

    @SerializedName("user_id")
    private int userid;

    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    private String email;
    private String password;
    private int age;
    private float height;
    private float weight;
    private String gender;
    @SerializedName("google_token")
    private String googleToken;
    @SerializedName("facebook_token")
    private String facebookToken;
    @SerializedName("picture_id")
    private String pictureId;

//    Score
    @SerializedName("best_plank_score")
    private float plankScore;
    @SerializedName("total_plank_time")
    private float plankTime;
    @SerializedName("best_squat_score")
    private float squatScore;
    @SerializedName("total_squat_count")
    private float squatCount;
    @SerializedName("total_squat_time")
    private float squatTime;
    @SerializedName("best_pushup_score")
    private float pushupScore;
    @SerializedName("total_pushup_count")
    private float pushupCount;
    @SerializedName("total_pushup_time")
    private float pushupTime;
    @SerializedName("best_flex_score")
    private float flexScore;
    @SerializedName("total_flex_count")
    private float flexCount;
    @SerializedName("total_flex_time")
    private float flexTime;


    public UserDetails(){}

//    public UserDetails(int id, float weight){}
//
//    public UserDetails(int id, String firstName, String lastName, String email, int pictureId){}
//
//    public UserDetails(int id, String firstName, String lastName, String email, int pictureId, String password, int age, float height, float weight, String gender, String googleToken, String facebookToken){
//        this.userid =id;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.password = password;
//        this.age = age;
//        this.height =height;
//        this.weight = weight;
//        this.gender = gender;
//        this.googleToken = googleToken;
//        this.facebookToken = facebookToken;
//        this.pictureId = pictureId;
//    }

    public int getUserId() {
        return this.userid;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }


    public String getPassword() {
        return this.password;
    }

    public int getAge() {
        return this.age;
    }

    public float getHeight() {
        return this.height;
    }

    public float getWeight() {
        return this.weight;
    }

    public String getGoogleToken() {
        return this.googleToken;
    }

    public String getFacebookToken() {
        return this.facebookToken;
    }

    public String getPictureId() {
        return this.pictureId;
    }

    //    Scores


}
