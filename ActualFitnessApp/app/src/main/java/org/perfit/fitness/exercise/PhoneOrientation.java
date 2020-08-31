package org.perfit.fitness.exercise;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevalpatel2106.rulerpicker.RulerValuePicker;

import org.perfit.fitness.R;
import org.perfit.fitness.activities.BaseDashBoard;
import org.perfit.fitness.activities.IntroVideo;
import org.perfit.fitness.activities.VideoManager;

import java.text.DecimalFormat;
import java.util.Locale;

public class PhoneOrientation extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private float currentRollValue = -999;
    private CountDownTimer orientationCountDownTimer;
//    private ImageView orientationBgCircle;
    private TextToSpeech textToSpeech;
    private int incrementVariable = 0;
    private boolean nextTriggered = false;

    long timeLastSpoken = -1L;

    private String prevT2S, speakTextContent;

    private TextView orientationCorrectionText/*,orientationTimerText*/;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_phone_orientation);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        timeLastSpoken = System.nanoTime();

        orientationCorrectionText = findViewById(R.id.orientation_correction_text);

//        orientationTimerText = findViewById(R.id.countDown_timer);
//        orientationBgCircle = findViewById(R.id.count_down_timer_bg_circle);


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                DecimalFormat df = new DecimalFormat("##.###");
                currentRollValue = orientation[2]; // orientation contains: azimut, pitch and roll
                isInCorrectAngle();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void isInCorrectAngle(){
        if ((currentRollValue > 1.4 && currentRollValue < 1.6) || (currentRollValue < -1.4 && currentRollValue > -1.6)) {
            orientationCorrectionText.setText("Perfect, Orientation is now okay.");
//            orientationBgCircle.setVisibility(View.VISIBLE);
//            orientationTimerText.setVisibility(View.VISIBLE);
            incrementVariable++;

            if ((incrementVariable > 200) && (!nextTriggered)){
                nextTriggered = true;
                goNext();
            }
        }
        else
        {
            orientationCorrectionText.setText(R.string.phone_too_tilted);
//            orientationTimerText.setVisibility(View.INVISIBLE);
//            orientationBgCircle.setVisibility(View.INVISIBLE);
            speakTextForOrientation("Make sure your phone is upright!");
            incrementVariable = 0;
        }
    }

    private void speakTextForOrientation (String text){

        if (!textToSpeech.isSpeaking() && (timeLastSpoken !=-1L) && (System.nanoTime() - timeLastSpoken)  > 5000000000L) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            timeLastSpoken = System.nanoTime();
        }
    }

    private void goNext() {
//        sensorManager.unregisterListener(this, accelerometer);
//        sensorManager.unregisterListener(this, magnetometer);

        Intent orientationOkay = new Intent(getApplicationContext(), IntroVideo.class);
        startActivity(orientationOkay);
        finish();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhoneOrientation.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), BaseDashBoard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (textToSpeech != null){
            textToSpeech.shutdown();
        }
    }
}
