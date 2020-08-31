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

import org.json.JSONException;
import org.json.JSONObject;
import org.perfit.fitness.R;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.VolleyCallback;

public class SigningPage extends AppCompatActivity {

    private TextInputLayout firstname_layout, lastname_layout,email_layout, password_layout;
    private EditText firstname,lastname, email,password;
    private SessionManager signUpManager;
    private String validSignUpEmail, validSignupPassword, validSignUpFirstName, validSignUpLastName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(getResources().getColor(R.color.normal_login));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.normal_login));
        setContentView(R.layout.activity_signin_page);
        signUpManager = new SessionManager(getApplicationContext());
        firstname_layout = findViewById(R.id.normal_signup_firstname_layout);
        lastname_layout = findViewById(R.id.normal_signup_lastname_layout);
        email_layout = findViewById(R.id.normal_signup_email_layout);
        password_layout = findViewById(R.id.normal_signup_password_layout);
        firstname = findViewById(R.id.normal_signup_input_firstname);
        lastname = findViewById(R.id.normal_signup_input_lastname);
        email = findViewById(R.id.normal_signup_input_email);
        password = findViewById(R.id.normal_signup_input_password);
        Button gotoLogin = findViewById(R.id.gotologin);
        ImageButton closeButton = findViewById(R.id.signup_close);
        ImageButton confirmSignup = findViewById(R.id.signup_confirm);

        firstname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });

        lastname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoLoginIntent = new Intent(getApplicationContext(), NormalLogin.class);
                startActivity(gotoLoginIntent);
                finish();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMainLoginIntent = new Intent(getApplicationContext(), MainLogin.class);
                startActivity(toMainLoginIntent);
                finish();
            }
        });

        email.addTextChangedListener(new MyTextWatch(email));
        password.addTextChangedListener(new MyTextWatch(password));
        firstname.addTextChangedListener(new MyTextWatch(firstname));
        lastname.addTextChangedListener(new MyTextWatch(lastname));

        confirmSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpForm();
            }
        });
    }

    private void signUpForm(){
        if(!validateFirstName()){
        }else if (!validateLastName()){
        }else if (!validateEmail()){
        }else if (!validatePassword()){
        }else{
            callSignUpAPI();
        }
    }

    private boolean validateEmail(){
        validSignUpEmail = email.getText().toString();
        if (TextUtils.isEmpty(validSignUpEmail)) {
            email_layout.setError(getString(R.string.email_empty));
            requestFocus(email);
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(validSignUpEmail).matches()){
            email_layout.setError(getString(R.string.email_format));
            return false;
        }
        else {
            email_layout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword(){
        validSignupPassword = password.getText().toString();
        if (TextUtils.isEmpty(validSignupPassword)){
            password_layout.setError(getString(R.string.email_empty));
            requestFocus(password);
            return false;
        }
        else if (validSignupPassword.trim().length() < 3 ){
            password_layout.setError(getString(R.string.lessWord));
            requestFocus(password);
            return false;
        }else {
            password_layout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateFirstName(){
        validSignUpFirstName = firstname.getText().toString();
        if (TextUtils.isEmpty(validSignUpFirstName)){
            firstname_layout.setError(getString(R.string.email_empty));
            requestFocus(firstname);
            return false;
        }
        else if (validSignUpFirstName.trim().length() < 3){
            firstname_layout.setError(getString(R.string.lessWord));
            requestFocus(firstname);
            return false;
        }else {
            firstname_layout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastName(){
        validSignUpLastName = lastname.getText().toString();
        if (TextUtils.isEmpty(validSignUpLastName)){
            lastname_layout.setError(getString(R.string.email_empty));
            requestFocus(lastname);
            return false;
        }
        else if (validSignUpLastName.trim().length() < 3){
            lastname_layout.setError(getString(R.string.lessWord));
            requestFocus(lastname);
            return false;
        }else {
            lastname_layout.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void callSignUpAPI() {
        JSONObject requestParameters = new JSONObject();
        try {
            requestParameters.put("firstName",validSignUpFirstName);
            requestParameters.put("lastName",validSignUpLastName);
            requestParameters.put("email",validSignUpEmail);
            requestParameters.put("password", validSignupPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIManager.CallAPI(APIManager.EMAIL_SIGNUP, requestParameters, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                handleLoginSuccessResponse(result);
            }

            @Override
            public void onFailureResponse(String result) {
                handleLoginFailureResponse();
            }
        });
    }
    private void handleLoginSuccessResponse(JSONObject result) {
        try {
            switch (result.getString("status")) {
                case "2":
                    Toast.makeText(SigningPage.this, "There is already an account associated with this email.", Toast.LENGTH_SHORT).show();
                    break;
                case "1":
                    String userName = firstname.getText().toString() + " " + lastname.getText().toString();
                    String userEmail = email.getText().toString();
//                    SharedPreferences.Editor editor = getSharedPreferences("details", MODE_PRIVATE).edit();
//                    editor.putString("email", result.getString("username"));
//                    editor.putString("userid", result.getString("userid"));
//                    editor.apply();
//                    AppController.userId = result.getString("userid");
                    signUpManager.createLoginSession(result.getInt("user_id"),userName,userEmail,null);
                    signUpManager.addUserId(result.getInt("user_id"));
                    Intent i = new Intent(getApplicationContext(), RegistrationWeight.class);
                    startActivity(i);
                    finish();
                    break;
                default:
                    Log.i("Error", "Server");
                    Toast.makeText(getApplicationContext(), "Please check your login details", Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Sorry, I lost my way. Lets try again", Toast.LENGTH_SHORT).show();
            Log.i("Error",e.getMessage());
        }
    }

    private void handleLoginFailureResponse() {
        Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_SHORT).show();
    }

    private class MyTextWatch implements TextWatcher {

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
                case R.id.normal_signup_input_firstname:
                    validateFirstName();
                    break;
                case R.id.normal_signup_input_lastname:
                    validateLastName();
                    break;
                case R.id.normal_signup_input_email:
                    validateEmail();
                    break;
                case R.id.normal_signup_input_password:
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
