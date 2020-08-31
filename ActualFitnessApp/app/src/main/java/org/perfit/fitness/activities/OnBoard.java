package org.perfit.fitness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import org.perfit.fitness.R;


public class OnBoard extends AppCompatActivity {

    private LinearLayout mDotLayout;
    private Button letsGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.onboard_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.whiteColor));
        setContentView(R.layout.activity_onboard);

        ViewPager mImageSlider = findViewById(R.id.imageslider);
        mDotLayout = findViewById(R.id.dotLayout);

        letsGo = findViewById(R.id.letsGoButton);
        letsGo.setVisibility(View.GONE);

        SliderAdapter sliderAdapter = new SliderAdapter(this);
        mImageSlider.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        mImageSlider.addOnPageChangeListener(viewListener);

        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent letsGoIntent = new Intent(getApplicationContext(), MainLogin.class);
                startActivity(letsGoIntent);
                finish();
            }
        });
    }

    private void addDotsIndicator(int position){
        TextView[] mdots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i< mdots.length; i++){
            mdots[i]=new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226;"));
            mdots[i].setTextSize(35);
            mdots[i].setTextColor(getResources().getColor(R.color.whiteColor));

            mDotLayout.addView(mdots[i]);
        }
        if (mdots.length>0) {
            mdots[position].setTextColor(getResources().getColor(R.color.dotsColor));
        }
    }

    private final ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            if (position == 2){
                letsGo.setVisibility(View.VISIBLE);
            }
            else
            {
                letsGo.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

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

//    public void fbdetailAccess(){
//        GraphRequest fbrequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject object, GraphResponse response) {
//                try {
//                    String fb_id = object.getString("id");
//                    String name = object.getString("name");
//                    String email = object.getString("email");
//                    String profilepicture = "https://graph.facebook.com/" + fb_id + "/picture?type=normal";
//
//                    sessionManager.createLoginSession(name,email, profilepicture);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        Bundle fb_parameters = new Bundle();
//        fb_parameters.putString("fields","id,name,email");
//        fbrequest.setParameters(fb_parameters);
//        fbrequest.executeAsync();
//        Intent fbinte = new Intent(getApplicationContext(), RegistrationWeight.class);
//        startActivity(fbinte);
//        finish();
//    }
}
