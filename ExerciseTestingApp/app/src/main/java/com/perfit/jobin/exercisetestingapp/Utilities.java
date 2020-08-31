package com.perfit.jobin.exercisetestingapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class Utilities {
//    public static ArrayList<String> videoList = new ArrayList<>(
//            Arrays.asList("iH1damrrNTI", "DDn97ax1bEM", "r_SqxYmtTN0")
////            Arrays.asList("r_SqxYmtTN0", "iH1damrrNTI", "i5oBH7eQ96Q", "DDn97ax1bEM")
//    );
//    public static int[] localVideoList = new int[] {R.raw.amazing, R.raw.congratulations};
//
//    public static ArrayList<Integer> gifList = new ArrayList<>(
//            Arrays.asList(R.drawable.squat, R.drawable.flexibility, R.drawable.pushup)
////            Arrays.asList(R.drawable.pushup, R.drawable.squat, R.drawable.plank, R.drawable.flexibility)
//    );
//    public static ArrayList<String> exerciseName = new ArrayList<>(
//            Arrays.asList("Squats", "Flexibility", "Pushup")
////            Arrays.asList("Pushup", "Squats", "Plank", "Flexibility")
//    );

    public static String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    public static boolean hasRequiredPermissions(Context context) {
        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void checkPermission(Activity activity) {
        int PERMISSION_ALL = 1;

        if(!hasPermissions(activity, PERMISSIONS)){
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
        }
    }
}
