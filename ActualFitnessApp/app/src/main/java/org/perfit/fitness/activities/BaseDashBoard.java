package org.perfit.fitness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;
import org.perfit.fitness.R;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.VolleyCallback;

public class BaseDashBoard extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private SessionManager recoendationManager;

    public final static int FRAG_TRAIINING = 0;
    public final static int FRAG_RECOMMENDATION = 1;
    public final static int FRAG_ACHIEVEMENT = 2;

    private final Recommendations recommendations = new Recommendations();
    private final Training training = new Training();
    private final Achievement achievement = new Achievement();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active;

    int getFragmentNumber = 0;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.navigation_dashboard:
//
//                    fragmentManager.beginTransaction()
//                            .hide(active)
//                            .show(dashBoard)
//                            .commit();
//                    active = dashBoard;
//                    return true;
                case R.id.navigation_workouts:

                    fragmentManager.beginTransaction()
                            .hide(active)
                            .show(recommendations)
                            .commit();
                    active = recommendations;
                    return true;
                case R.id.navigation_training:
                    fragmentManager.beginTransaction()
                            .hide(active)
                            .show(training)
                            .commit();
                    active = training;
                    return true;
                case R.id.navigation_performance:
                    fragmentManager.beginTransaction()
                            .hide(active)
                            .show(achievement)
                            .commit();
                    active = achievement;
                    return true;
//                case R.id.navigation_profile:
//                    fragmentManager.beginTransaction()
//                            .hide(active)
//                            .show(profile)
//                            .commit();
//                    active = profile;
//                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(getResources().getColor(R.color.baseboard_nav_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.baseboard_nav_color));
        setContentView(R.layout.activity_base_dash_board);
        recoendationManager = new SessionManager(getApplicationContext());

        callApiRecomendation();


        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_training);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        fragmentManager.beginTransaction().add(R.id.mainContainer, dashBoard).hide(dashBoard).commit();
        fragmentManager.beginTransaction().add(R.id.mainContainer, recommendations).hide(recommendations).commit();
        fragmentManager.beginTransaction().add(R.id.mainContainer, training).hide(training).commit();
        fragmentManager.beginTransaction().add(R.id.mainContainer, achievement).hide(achievement).commit();

        getFragmentNumber = getIntent().getIntExtra("fragToSee", FRAG_TRAIINING);
        if (getFragmentNumber == FRAG_ACHIEVEMENT){
            fragmentManager.beginTransaction().show(achievement).commit();
            active = achievement;
        } else if (getFragmentNumber == FRAG_TRAIINING){
            fragmentManager.beginTransaction().show(training).commit();
            active = training;
        } else {
            fragmentManager.beginTransaction().show(recommendations).commit();
            active = recommendations;
        }
//        fragmentManager.beginTransaction().add(R.id.mainContainer, profile).hide(profile).commit();
    }

    private void callApiRecomendation(){
        JSONObject recomParameters = new JSONObject();

        APIManager.CallAPI(APIManager.LOAD_VIDEOS, recomParameters, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                handleSuccessResponce(result);
            }

            @Override
            public void onFailureResponse(String result) {

            }
        });
    }

    private void handleSuccessResponce(JSONObject result){
        try {
            if ("1".equals(result.getString("status"))) {
                recoendationManager.addVideoUrls(result.getString("intro"),
                        result.getString("squat"), result.getString("flexibility"),
                        result.getString("pushup"), result.getString("plank"), result.getString("jumping_jacks"));
            } else {
                Log.i("Error", "Server");
            }
        } catch (Exception e) {
            Log.i("Error",e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce){
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
        },2000);
    }

}
