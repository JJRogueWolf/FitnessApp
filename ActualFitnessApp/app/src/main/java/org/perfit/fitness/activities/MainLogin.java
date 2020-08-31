package org.perfit.fitness.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import org.perfit.fitness.R;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.VolleyCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainLogin extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    private boolean doubleBackToExitPressedOnce = false;

    private CallbackManager callbackManager;
    private LoginButton facebookHiddenButton;
    private static final String EMAIL = "email";
    private static final String PROFILE = "public_profile";

    private SessionManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_login_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_login_color_bottomNavi));
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
//        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        facebookHiddenButton = findViewById(R.id.facebookSignInHidden);
        facebookHiddenButton.setReadPermissions(Arrays.asList(EMAIL, PROFILE));
        Button facebookSignInButton = findViewById(R.id.facebookCustomButton);
        facebookSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                printKeyHash();
                if (isLoggedIn) {
                    LoginManager.getInstance().logOut();
                }
                facebookHiddenButton.performClick();
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loadUserProfile(AccessToken.getCurrentAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainLogin.this, "You canceled login with facebook", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("FacebookError", error.toString());
            }
        });

//        tokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                if (currentAccessToken == null)
//                {
//                    Toast.makeText(MainLogin.this, "Sorry.. Something caused a road block", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                }
//            }
//        };

        SignInButton googlebutton = findViewById(R.id.googleSignInHidden);
        Button normalLoginButton = findViewById(R.id.normalLogin);
        normalLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent normalLoginIntent = new Intent(getApplicationContext(), NormalLogin.class);
                startActivity(normalLoginIntent);
                finish();
            }
        });
        loginManager = new SessionManager(getApplicationContext());
        Button googleSignInButton = findViewById(R.id.googleSignIn);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.googleSignIn) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googlebutton.setSize(SignInButton.SIZE_STANDARD);
    }

    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(),PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Task<GoogleSignInAccount> googleTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(googleSignInResult, googleTask);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void loadUserProfile(final AccessToken newAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    addFacebookUserData(first_name, last_name, email, image_url, String.valueOf(accessToken.getToken()),id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void handleSignInResult(GoogleSignInResult googleResult, Task<GoogleSignInAccount> googleTask) {
        try {
            if (googleResult.isSuccess()) {
                GoogleSignInAccount account = googleTask.getResult(ApiException.class);
                String googleToken = account.getIdToken();
                GoogleSignInAccount googleSignInAccount = googleResult.getSignInAccount();
                String googleId = googleSignInAccount.getId();
                String name = googleSignInAccount.getDisplayName();
                String email = googleSignInAccount.getEmail();
                String googleProfilePhoto = googleSignInAccount.getPhotoUrl() != null ? googleSignInAccount.getPhotoUrl().toString() : "null";
                String[] split = name.split(" ");
                String user_LastName;
                String user_FirstName;
                if (split.length == 1){
                    user_FirstName = name;
                    user_LastName = "";
                } else {
                    user_FirstName = split[0];
                    user_LastName = split[1];
                }
                addGoogleUserData(user_FirstName, user_LastName, email, googleProfilePhoto, googleId, googleToken);
            }
        } catch (Exception e) {
            Log.i("GoogleLoginError", e.getMessage());
            Toast.makeText(this, "Sorry.. I lost my way. Lets try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void addFacebookUserData(final String user_first_name, final String user_last_name, final String user_email, final String user_profile_picture, String fbToken, String fbId) {
        JSONObject addfbParameters = new JSONObject();
        try {
            addfbParameters.put("facebookId", fbId);
            addfbParameters.put("facebookToken", fbToken == null? "NULL": fbToken);
            addfbParameters.put("firstName", user_first_name);
            addfbParameters.put("lastName", user_last_name);
            addfbParameters.put("email", user_email);
            addfbParameters.put("pictureId", user_profile_picture);
            addfbParameters.put("password", "null");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIManager.CallAPI(APIManager.FACEBOOK_LOGIN, addfbParameters, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                try {
                    switch (result.getString("status")) {
                        case "2":
                            loginManager.addLoginSession(result.getInt("user_id"), result.getString("first_name") + " " + result.getString("last_name"), result.getString("email"),
                                    result.getString("picture_id"), result.getString("gender"), result.getString("height"),
                                    result.getString("weight"), result.getInt("age"));
                            loginManager.addPushupCount(result.getInt("latest_pushup_count"));
                            loginManager.addSquatCount(result.getInt("latest_squat_count"));
                            loginManager.addPlankTime(result.getInt("latest_plank_time"));
                            loginManager.addFlexibiltyCount(result.getInt("latest_flex_count"));
                            loginManager.addBestAndTotal((float) result.getDouble("best_pushup_score"), (float) result.getDouble("best_plank_score"),
                                    (float) result.getDouble("best_flex_score"), (float) result.getDouble("best_squat_score"),
                                    result.getInt("total_pushup_count"), result.getInt("total_pushup_time"),
                                    result.getInt("total_squat_count"), result.getInt("total_squat_time"),
                                    result.getInt("total_flex_count"), result.getInt("total_flex_time"),
                                    result.getInt("total_plank_time"),(float)result.getDouble("total_jumping_jacks_score"),result.getInt("total_jumping_jacks_time"), (float) result.getDouble("total_score_metric"));
                            Intent inte = new Intent(getApplicationContext(), BaseDashBoard.class);
                            startActivity(inte);
                            finish();
                            break;
                        case "1":
                            loginManager.createLoginSession(result.getInt("user_id"), user_first_name + user_last_name, user_email, user_profile_picture);
                            Intent intent = new Intent(getApplicationContext(), RegistrationWeight.class);
                            startActivity(intent);
                            finish();
                            break;
                    }
                } catch (Exception e) {
                    Toast.makeText(MainLogin.this, "Sorry something that was not suppose to happen has happened", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailureResponse(String result) {
                Toast.makeText(MainLogin.this, "Unable to Login. Please Check your network Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addGoogleUserData(final String user_first_name, final String user_last_name, final String user_email, final String user_profile_picture, final String googleId, final String googleToken) {
        JSONObject addParameters = new JSONObject();
        try {
            addParameters.put("googleId", googleId);
            addParameters.put("googleToken", googleToken == null ? "NULL" : googleToken);
            addParameters.put("firstName", user_first_name);
            addParameters.put("lastName", user_last_name);
            addParameters.put("email", user_email);
            addParameters.put("pictureId", user_profile_picture);
            addParameters.put("password", "NULL");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIManager.CallAPI(APIManager.GOOGLE_LOGIN, addParameters, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                handleGoogleSuccessResponse(result, user_first_name, user_last_name, user_email, user_profile_picture);
            }

            @Override
            public void onFailureResponse(String result) {
                Toast.makeText(MainLogin.this, "Unable to Login. Please Check your network Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleGoogleSuccessResponse(JSONObject result, String user_first_name, String user_last_name, String user_email, String user_profile_picture) {
        try {
            switch (result.getString("status")) {
                case "2":
                    loginManager.addLoginSession(result.getInt("userid"), result.getString("first_name") + " " + result.getString("last_name"), result.getString("email"),
                            result.getString("picture_id"), result.getString("gender"), result.getString("height"),
                            result.getString("weight"), result.getInt("age"));
                    loginManager.addPushupCount(result.getInt("latest_pushup_count"));
                    loginManager.addSquatCount(result.getInt("latest_squat_count"));
                    loginManager.addPlankTime(result.getInt("latest_plank_time"));
                    loginManager.addFlexibiltyCount(result.getInt("latest_flex_count"));
                    loginManager.addBestAndTotal((float) result.getDouble("best_pushup_score"), (float) result.getDouble("best_plank_score"),
                            (float) result.getDouble("best_flex_score"), (float) result.getDouble("best_squat_score"),
                            result.getInt("total_pushup_count"), result.getInt("total_pushup_time"),
                            result.getInt("total_squat_count"), result.getInt("total_squat_time"),
                            result.getInt("total_flex_count"), result.getInt("total_flex_time"),
                            result.getInt("total_plank_time"),(float)result.getDouble("total_jumping_jacks_score"),result.getInt("total_jumping_jacks_time"),
                            (float) result.getDouble("total_score_metric"));
                    Intent inte = new Intent(getApplicationContext(), BaseDashBoard.class);
                    startActivity(inte);
                    finish();
                    break;
                case "1":
                    loginManager.createLoginSession(result.getInt("userid"), user_first_name + user_last_name, user_email, user_profile_picture);
                    Intent intent = new Intent(getApplicationContext(), RegistrationWeight.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Oh no.. We got hit a road block. Lets try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            System.exit(0);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
