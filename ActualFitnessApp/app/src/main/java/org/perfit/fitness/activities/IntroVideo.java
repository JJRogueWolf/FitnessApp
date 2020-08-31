package org.perfit.fitness.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.perfit.fitness.R;

import java.util.HashMap;

public class IntroVideo extends AppCompatActivity {
    private YouTubePlayerView youtubePlay;
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_intro_video);
        SessionManager introVideoManager = new SessionManager(getApplicationContext());

        HashMap<String,Boolean> skipAllVideo = introVideoManager.getFirstUserCheck();
        boolean skipAllVideoBool = skipAllVideo.get(SessionManager.KEY_SKIP_ALL_VIDEO);

        HashMap<String,String> introVideoUrls = introVideoManager.sendvideoUrls();
        videoId = introVideoUrls.get(SessionManager.KEY_INTRO_VIDEO);

        youtubePlay = findViewById(R.id.introVideo);
//        Bundle get_code = getIntent().getExtras();
//        videoId = get_code.getInt("video_code");
//        gifId = get_code.getInt("gif_code");

        if (skipAllVideoBool) {
            youtubePlay.setVisibility(View.VISIBLE);
            youtubePlay.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    youTubePlayer.loadVideo(videoId, 0);

                }

                @Override
                public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
                    if (state == PlayerConstants.PlayerState.ENDED) {
                        Intent nextVideo = new Intent(IntroVideo.this, VideoManager.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("video_code", 0);
                        bundle.putInt("gif_code", 0);
                        nextVideo.putExtras(bundle);
                        youtubePlay.release();
                        nextVideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(nextVideo);
                        finish();
                    }
                }
            });
        }
        else
        {
            youtubePlay.setVisibility(View.GONE);
            Intent nextVideo = new Intent(IntroVideo.this, VideoManager.class);
            Bundle bundle = new Bundle();
            bundle.putInt("video_code", 0);
            bundle.putInt("gif_code", 0);
            nextVideo.putExtras(bundle);
            startActivity(nextVideo);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(IntroVideo.this);
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
        youtubePlay.release();
    }
}
