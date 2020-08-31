package org.perfit.fitness.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.perfit.fitness.R;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.VolleyCallback;

public class NormalLogin extends AppCompatActivity {

    private EditText inputPassword, inputEmail;
    private TextInputLayout emailLayout, passwordLayot;
    private SessionManager inputManager;
    private String validEmail, validPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.normal_login));
        getWindow().setStatusBarColor(getResources().getColor(R.color.normal_login));
        setContentView(R.layout.activity_normal__login);
        Gson gson = new Gson();
        inputManager = new SessionManager(getApplicationContext());
        ImageButton inputConfirm = findViewById(R.id.login_confirm);
        inputEmail = findViewById(R.id.normal_login_input_email);
        inputPassword = findViewById(R.id.normal_login_input_password);
        emailLayout = findViewById(R.id.normal_login_email_layout);
        passwordLayot = findViewById(R.id.normal_login_password_layout);
        Button gotToSignup = findViewById(R.id.gotoSignup);
        Button forgotPassword = findViewById(R.id.forgot_password);
        ImageButton login_exit = findViewById(R.id.login_close);

        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });

        inputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });

        login_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMainIntent = new Intent(getApplicationContext(),MainLogin.class);
                startActivity(toMainIntent);
                finish();
            }
        });

        gotToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(getApplicationContext(), SigningPage.class);
                startActivity(signUpIntent);
                finish();
            }
        });

        inputEmail.addTextChangedListener(new MyTextWatch(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatch(inputPassword));

        inputConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitInput();
            }
        });
    }

    private void submitInput(){
        if(!validateEmail()){
        }else if (!validatePassword()){
        } else {
            callLoginApi();
        }
    }

    private boolean validateEmail(){
        validEmail = inputEmail.getText().toString();
        if (TextUtils.isEmpty(validEmail)) {
            emailLayout.setError(getString(R.string.email_empty));
            requestFocus(inputEmail);
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(validEmail).matches()){
            emailLayout.setError(getString(R.string.email_format));
            return false;
        }
        else {
            emailLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword(){
        validPassword = inputPassword.getText().toString();
        if (TextUtils.isEmpty(validPassword)){
            passwordLayot.setError(getString(R.string.email_empty));
            requestFocus(inputPassword);
            return false;
        }
        if (validPassword.trim().length() < 3 ){
            passwordLayot.setError(getString(R.string.lessWord));
            requestFocus(inputPassword);
            return false;
        }
        else
        {
            passwordLayot.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void callLoginApi(){
        JSONObject requestParameters = new JSONObject();
        try {
            requestParameters.put("email",validEmail);
            requestParameters.put("password", validPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIManager.CallAPI(APIManager.EMAIL_LOGIN, requestParameters, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                handleLoginSuccessResponse(result);
            }

            @Override
            public void onFailureResponse(String result) {
                handleLoginFailureResponse(result);
            }
        });
    }

    private void handleLoginSuccessResponse(JSONObject result) {
        try {
            switch (result.getString("status")) {
                case "2":
                    inputManager.createLoginSession(result.getInt("user_id"), result.getString("first_name") + result.getString("last_name"), result.getString("email"), null);
                    Intent intent = new Intent(getApplicationContext(), RegistrationWeight.class);
                    startActivity(intent);
                    finish();
                    break;
                case "1":
                    inputManager.addLoginSession(result.getInt("user_id"),result.getString("first_name")+" " + result.getString("last_name"),result.getString("email"),
                            result.getString("picture_id"),result.getString("gender"),result.getString("height"),
                            result.getString("weight"),result.getInt("age"));
                    inputManager.addPushupCount(result.getInt("latest_pushup_count"));
                    inputManager.addSquatCount(result.getInt("latest_squat_count"));
                    inputManager.addPlankTime(result.getInt("latest_plank_time"));
                    inputManager.addFlexibiltyCount(result.getInt("latest_flex_count"));
                    inputManager.addBestAndTotal((float) result.getDouble("best_pushup_score"),(float) result.getDouble("best_plank_score"),
                            (float) result.getDouble("best_flex_score"), (float) result.getDouble("best_squat_score"),
                            result.getInt("total_pushup_count"), result.getInt("total_pushup_time"),
                            result.getInt("total_squat_count"),result.getInt("total_squat_time"),
                            result.getInt("total_flex_count"),result.getInt("total_flex_time"),
                            result.getInt("total_plank_time"), (float)result.getDouble("total_jumping_jacks_score"),result.getInt("total_jumping_jacks_time"),
                            (float) result.getDouble("total_score_metric"));
//                    inputManager.addSkipAllVideoBoolean(result.getBoolean("fresh_check"));
                    Intent i = new Intent(getApplicationContext(), BaseDashBoard.class);
                    startActivity(i);
                    finish();
                    break;
                default:
                    Log.i("Error", "Server");
                    Toast.makeText(getApplicationContext(), "No account associated to this email is been found", Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unknown error", Toast.LENGTH_SHORT).show();
            Log.i("Error",e.getMessage());
        }
    }

    private void handleLoginFailureResponse(String error) {
        Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        Log.e("Server Error", error);
    }

    private class MyTextWatch implements TextWatcher{

        final View view;

        private MyTextWatch(View view){
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()){
                case R.id.normal_login_input_email:
                    validateEmail();
                    break;
                case R.id.normal_login_input_password:
                    validatePassword();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent toLoginIntent = new Intent(getApplicationContext(),MainLogin.class);
        startActivity(toLoginIntent);
        finish();
    }
}
