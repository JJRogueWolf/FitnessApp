package org.perfit.fitness.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.perfit.fitness.R;
import org.perfit.fitness.camera.ClassifierActivity;
import org.perfit.fitness.utilities.Utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

import static org.perfit.fitness.utilities.Utilities.beginGif;
import static org.perfit.fitness.utilities.Utilities.exerciseName;
import static org.perfit.fitness.utilities.Utilities.gifList;

public class VideoPlayer extends AppCompatActivity {

    private int Video_code;
    private int Gif_code;
    private int checksingle;
    private YouTubePlayerView ytplayer;
    private GifImageView workouts;
    private LinearLayout skipVideo;
    private TextView timerStart;
    private TextView exerciseNameVP;
    private TextView getReadyForSquats;
    private TextToSpeech textToSpeech;
    private GifImageView ytGifViewforNext;

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
        setContentView(R.layout.activity_video_player);
        SessionManager videoPlayerManager = new SessionManager(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        Video_code = bundle.getInt("FirstVideo");
        Gif_code = bundle.getInt("FirstGif");
        workouts = findViewById(R.id.workoutGifs);
        getReadyForSquats = findViewById(R.id.textView8);
        TextView exitButon = findViewById(R.id.exitButton);
        ytGifViewforNext = findViewById(R.id.yt_video_gif);
        exerciseNameVP = findViewById(R.id.exercise_name);
        timerStart = findViewById(R.id.getsetgo );
        skipVideo = findViewById(R.id.skipVideo);
        ytplayer = findViewById(R.id.ytplayer);

        timerStart.setVisibility(View.GONE);
        getReadyForSquats.setVisibility(View.GONE);
        ytGifViewforNext.setVisibility(View.GONE);
        ytGifViewforNext.setImageResource(beginGif.get(Gif_code));
        exerciseNameVP.setText(exerciseName.get(Gif_code));
        exerciseNameVP.setVisibility(View.GONE);

        HashMap<String,Boolean> skipVideos = videoPlayerManager.getFirstUserCheck();
        boolean allVideoSkipVideoP = skipVideos.get(SessionManager.KEY_SKIP_ALL_VIDEO);

        HashMap<String,String> getvideoList = videoPlayerManager.sendvideoUrls();
        String introVideoUrl = getvideoList.get(SessionManager.KEY_INTRO_VIDEO);
        String squatVideoUrl = getvideoList.get(SessionManager.KEY_SQUAT_VIDEO);
        String flexVideoUrl = getvideoList.get(SessionManager.KEY_FLEXIBILITY_VIDEO);
        String jumpVideoUrl = getvideoList.get(SessionManager.KEY_JUMPING_JACKS_VIDEO);
        String pushupVideoUrl = getvideoList.get(SessionManager.KEY_PUSHUP_VIDEO);
        String plankVideoUrl = getvideoList.get(SessionManager.KEY_PLANK_VIDEO);

        ArrayList<String> videoList = new ArrayList<>(
                Arrays.asList(squatVideoUrl, pushupVideoUrl, plankVideoUrl, jumpVideoUrl)
//            Arrays.asList("r_SqxYmtTN0", "iH1damrrNTI", "i5oBH7eQ96Q", "DDn97ax1bEM")
        );

        int sizeList = videoList.size();

        if (allVideoSkipVideoP) {
            exitButon.setVisibility(View.GONE);
            ytPlayer(videoList.get(Video_code), gifList.get(Gif_code));
        }
        else
        {
            ytplayer.setVisibility(View.GONE);
            skipVideo.setVisibility(View.GONE);
            exitButon.setVisibility(View.GONE);
            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i != TextToSpeech.ERROR){
                        textToSpeech.setLanguage(Locale.US);
                        textToSpeech.speak("Let's begin " + exerciseName.get(Video_code),TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            });
            exerciseNameVP.setVisibility(View.VISIBLE);
            ytGifViewforNext.setVisibility(View.VISIBLE);
            getReadyForSquats.setVisibility(View.VISIBLE);
            timerStart.setVisibility(View.VISIBLE);
            new CountDownTimer(4000,10){
                @Override
                public void onTick(long l) {
                    int countdownvalue =(int) l /1000;
                    DecimalFormat df = new DecimalFormat("0");
                    String tickValue = df.format(countdownvalue);
                    timerStart.setText(tickValue);
                }

                @Override
                public void onFinish() {
                    Intent startTrackingIntent = new Intent(getApplicationContext(), ClassifierActivity.class);
                    Bundle put_code = new Bundle();
                    put_code.putInt("gif_id_code",Gif_code);
                    put_code.putInt("exercise_value",Video_code);
                    startTrackingIntent.putExtras(put_code);
                    startTrackingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(startTrackingIntent);
                    finish();
                }
            }.start();
        }
        workouts.setVisibility(View.GONE);

        exitButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VideoPlayer.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), BaseDashBoard.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        });
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VideoPlayer.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(),BaseDashBoard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    private void ytPlayer(final String videoId, final int gifId){
        getLifecycle().addObserver(ytplayer);
        ytplayer.setVisibility(View.VISIBLE);
        skipVideo.setVisibility(View.VISIBLE);
        workouts.setImageResource(gifId);

        ytplayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(final YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId,0);
                skipVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        youTubePlayer.pause();
                        ytplayer.setVisibility(View.GONE);
                        skipVideo.setVisibility(View.GONE);
                        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int i) {
                                if (i != TextToSpeech.ERROR){
                                    textToSpeech.setLanguage(Locale.US);
                                    textToSpeech.speak("Let's begin " + exerciseName.get(Video_code),TextToSpeech.QUEUE_FLUSH, null, null);
                                }
                            }
                        });
                        exerciseNameVP.setVisibility(View.VISIBLE);
                        ytGifViewforNext.setVisibility(View.VISIBLE);
                        getReadyForSquats.setVisibility(View.VISIBLE);
                        timerStart.setVisibility(View.VISIBLE);
                        new CountDownTimer(4000,10){
                            @Override
                            public void onTick(long l) {
                                int countdownvalue =(int) l /1000;
                                DecimalFormat df = new DecimalFormat("0");
                                String tickValue = df.format(countdownvalue);
                                timerStart.setText(tickValue);
                            }

                            @Override
                            public void onFinish() {
                                Intent startTrackingIntent = new Intent(getApplicationContext(), ClassifierActivity.class);
                                Bundle put_code = new Bundle();
                                put_code.putInt("gif_id_code",Gif_code);
                                put_code.putInt("exercise_value",Video_code);
                                startTrackingIntent.putExtras(put_code);
                                startTrackingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(startTrackingIntent);
                                finish();
                            }
                        }.start();
                    }
                });
            }

            @Override
            public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState playerState) {
                if (playerState == PlayerConstants.PlayerState.ENDED) {
                    ytplayer.setVisibility(View.GONE);
                    skipVideo.setVisibility(View.GONE);
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if (i != TextToSpeech.ERROR){
                                textToSpeech.setLanguage(Locale.US);
                                textToSpeech.speak("Let's begin " + exerciseName.get(Video_code),TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    });
                    exerciseNameVP.setVisibility(View.VISIBLE);
                    ytGifViewforNext.setVisibility(View.VISIBLE);
                    getReadyForSquats.setVisibility(View.VISIBLE);
                    timerStart.setVisibility(View.VISIBLE);
                    new CountDownTimer(4000,10){
                        @Override
                        public void onTick(long l) {
                            int countdownvalue =(int) l /1000;
                            DecimalFormat df = new DecimalFormat("0");
                            String tickValue = df.format(countdownvalue);
                            timerStart.setText(tickValue);
                        }

                        @Override
                        public void onFinish() {
                            Intent startTrackingIntent = new Intent(getApplicationContext(), ClassifierActivity.class);
                            Bundle put_code = new Bundle();
                            put_code.putInt("gif_id_code",Gif_code);
                            put_code.putInt("exercise_value",Video_code);
                            startTrackingIntent.putExtras(put_code);
                            startTrackingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(startTrackingIntent);
                            finish();
                        }
                    }.start();
                }

            }
        });
    }

//    public static ArrayList<String> videoList = new ArrayList<>(
//            Arrays.asList(squatVideoUrl, flexVideoUrl, pushupVideoUrl, plankVideoUrl)
////            Arrays.asList("r_SqxYmtTN0", "iH1damrrNTI", "i5oBH7eQ96Q", "DDn97ax1bEM")
//    );

    @Override
    public void onDestroy() {
        super.onDestroy();
        ytplayer.release();
    }
}
