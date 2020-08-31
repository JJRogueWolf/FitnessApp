package com.perfit.jobin.exercisetestingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import static com.otaliastudios.cameraview.CameraView.PERMISSION_REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

    Button pushUp, squat, plank, flex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        pushUp = findViewById(R.id.pushUp);
        squat = findViewById(R.id.squat);
        plank = findViewById(R.id.plank);
        flex = findViewById(R.id.flex);

        pushUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    Intent startTrackingIntent = new Intent(getApplicationContext(), ClassifierActivity.class);
                    Bundle put_code = new Bundle();
                    put_code.putInt("gif_id_code", 0);
                    put_code.putInt("exercise_value", 0);
                    startTrackingIntent.putExtras(put_code);
                    startActivity(startTrackingIntent);
                    finish();
                } else {
                    requestStoragePermission();
                }
            }
        });

        squat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    Intent startTrackingIntent = new Intent(getApplicationContext(), ClassifierActivity.class);
                    Bundle put_code = new Bundle();
                    put_code.putInt("gif_id_code", 1);
                    put_code.putInt("exercise_value", 1);
                    startTrackingIntent.putExtras(put_code);
                    startActivity(startTrackingIntent);
                    finish();
                } else {
                    requestStoragePermission();
                }
            }
        });

        plank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    Intent startTrackingIntent = new Intent(getApplicationContext(), ClassifierActivity.class);
                    Bundle put_code = new Bundle();
                    put_code.putInt("gif_id_code", 2);
                    put_code.putInt("exercise_value", 2);
                    startTrackingIntent.putExtras(put_code);
                    startActivity(startTrackingIntent);
                    finish();

                } else {
                    requestStoragePermission();
                }
            }
        });

        flex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    Intent startTrackingIntent = new Intent(getApplicationContext(), ClassifierActivity.class);
                    Bundle put_code = new Bundle();
                    put_code.putInt("gif_id_code", 3);
                    put_code.putInt("exercise_value", 3);
                    startTrackingIntent.putExtras(put_code);
                    startActivity(startTrackingIntent);
                    finish();

                } else {
                    requestStoragePermission();
                }
            }
        });
    }

    private boolean checkPermission() {


        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
}
