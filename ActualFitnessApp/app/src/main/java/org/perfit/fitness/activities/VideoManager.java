package org.perfit.fitness.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.perfit.fitness.R;

public class VideoManager extends AppCompatActivity {


//    public VideoManager (Context context){
//        this.mContext = context;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle get_code = getIntent().getExtras();
        int videoId = get_code.getInt("video_code");
        int gifId = get_code.getInt("gif_code");
        Intent nextVideo = new Intent(VideoManager.this,VideoPlayer.class);
        Bundle bundle = new Bundle();
        bundle.putInt("FirstVideo", videoId);
        bundle.putInt("FirstGif", gifId);
        nextVideo.putExtras(bundle);
        startActivity(nextVideo);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoManager.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), BaseDashBoard.class);
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
}
