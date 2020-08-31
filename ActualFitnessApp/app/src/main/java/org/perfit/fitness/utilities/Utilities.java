package org.perfit.fitness.utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import org.perfit.fitness.R;

import java.util.ArrayList;
import java.util.Arrays;

public class Utilities {

    public static final ArrayList<Integer> gifList = new ArrayList<>(
            Arrays.asList(R.drawable.squat, R.drawable.pushup, R.drawable.plank_pose_bg, R.drawable.squat)
//            Arrays.asList(R.drawable.pushup, R.drawable.squat, R.drawable.plank, R.drawable.flexibility)
    );

    public static final ArrayList<String> exerciseName = new ArrayList<>(
            Arrays.asList("Squats", "Pushup", "Plank", "Jumping Jack")
//            Arrays.asList("Pushup", "Squats", "Plank", "Flexibility")
    );

    public static final ArrayList<Integer> beginGif = new ArrayList<>(
            Arrays.asList(R.drawable.squat_gif_whitebg, R.drawable.push_gif_whitebg, R.drawable.plank_gif_whitebg, R.drawable.jumpingjack_white)
    );

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
