package org.perfit.fitness.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.perfit.fitness.R;

public class GetStarted extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.getstarted_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.getstarted_nav_color));
        setContentView(R.layout.activity_get_started);
        ImageView getstarted = findViewById(R.id.getstartedButton);

        if (!hasPermission()){
            requestPermission();
        }

        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (checkPermission())
                    {
                    Intent goToOnboard = new Intent(getApplicationContext(),OnBoard.class);
                    startActivity(goToOnboard);
                    finish();
                    } else {
                        requestStoragePermission(); // Code for permission
                    }
                }
                else
                {

                    Toast.makeText(GetStarted.this, "Please provide required permissions for better Functioning", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(GetStarted.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(GetStarted.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(GetStarted.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(GetStarted.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

//    @Override
//    public void onBackPressed() {
////        AlertDialog.Builder builder = new AlertDialog.Builder(GetStarted.this);
////        builder.setTitle(R.string.app_name);
////        builder.setMessage("Are you sure you want to exit?")
////                .setCancelable(false)
////                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {
////                        Intent intent = new Intent(Intent.ACTION_MAIN);
////                        intent.addCategory(Intent.CATEGORY_HOME);
////                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                        startActivity(intent);
////                    }
////                })
////                .setNegativeButton("No", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {
////                        dialogInterface.cancel();
////                    }
////                });
////        AlertDialog alert = builder.create();
////        alert.show();
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
}
