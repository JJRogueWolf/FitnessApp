package org.perfit.fitness.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.perfit.fitness.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Achievement extends Fragment {

    private View rootView;

    private SessionManager achievementManager;

    private ImageButton popMenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievement, container, false);
        achievementManager = new SessionManager(getContext());

        TextView squatsCountDisp = view.findViewById(R.id.squat_count_main);
//        flexibilityCountDisp = view.findViewById(R.id.flexibility_count_main);
        TextView pushupCountDisp = view.findViewById(R.id.pushup_count_main);
        TextView plankCountDisp = view.findViewById(R.id.plank_count_main);
        TextView jumpCountDisp = view.findViewById(R.id.jump_time_main);

        TextView planktotaltime = view.findViewById(R.id.plankTotalTime);
        TextView plankbestscore = view.findViewById(R.id.plankBestScore);
        TextView jumpingjackstotaltime = view.findViewById(R.id.jjTotalTime);
        TextView jumpingjacksbestscore = view.findViewById(R.id.jjBestScore);
//        flextotaltime = view.findViewById(R.id.flexTotalTime);
//        flexbestscore = view.findViewById(R.id.flexBestScore);
//        flextotalcount = view.findViewById(R.id.flexTotalCount);
        TextView pushuptotaltime = view.findViewById(R.id.pushupTotalTime);
        TextView pushuptotalcount = view.findViewById(R.id.pushupTotalCount);
        TextView pushupbestscore = view.findViewById(R.id.pushupBestScore);
        TextView squattotaltime = view.findViewById(R.id.squatsTotalTime);
        TextView squattotalcount = view.findViewById(R.id.squatsTotalCount);
        ProgressBar badgeProgress = view.findViewById(R.id.reward_progress);
        TextView squatbestscore = view.findViewById(R.id.squatsBestScore);


        popMenu = view.findViewById(R.id.performance_pop);
        popMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), popMenu);
                popup.getMenuInflater().inflate(R.menu.actions, popup.getMenu());
                popup.setGravity(Gravity.END);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        achievementManager.logoutUser();
                        getActivity().finish();
                        return true;
                    }
                });

                popup.show();
            }
        });
        ImageView shareButton = view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap bitmap = getScreenShot(rootView);
                shareImage(store(bitmap, "/screenshot.png"));
            }
        });

//        mContext = getContext();
//        mAwardPager = view.findViewById(R.id.awardPager);
//        mCardAdapter = new CardPagerAdapterS();
//
//        for (int i = 0; i < cardAwardImages.length; i++) {
//            mCardAdapter.addCardIems(new CarditemInteger(cardAwardImages[i]));
//        }
//
//        mCardShadowTransfrom = new ShadowTransform(mAwardPager, mCardAdapter);
//        mAwardPager.setAdapter(mCardAdapter);
//        mAwardPager.setPageTransformer(false, mCardShadowTransfrom);
//        mAwardPager.setOffscreenPageLimit(3);

        HashMap<String, Integer> allTime = achievementManager.getTimeAge();
        int plank_durationin_seconds = allTime.get(SessionManager.KEY_PLANK_TIME);
        int jump_duration_seconds = allTime.get(SessionManager.KEY_JUMPING_TIME);

        HashMap<String, Integer> allScores = achievementManager.getCountTime();
        int squat_count = allScores.get(SessionManager.KEY_SQUAT_COUNT);
//        int flexibility_count = allScores.get(SessionManager.KEY_FLEXIBILITY_COUNT);
        int pushup_count = allScores.get(SessionManager.KEY_PUSHUP_COUNT);


        HashMap<String, Integer> alltimecount = achievementManager.getCountTime();
        int totalplanktime = alltimecount.get(SessionManager.KEY_TOTAL_PLANK_TIME);
        int totalpushupcount = alltimecount.get(SessionManager.KEY_TOTAL_PUSHUP_COUNT);
        int totalpushuptime = alltimecount.get(SessionManager.KEY_TOTAL_PUSHUP_TIME);
        int totaljumpingjackstime = alltimecount.get(SessionManager.KEY_TOTAL_JUMPING_JACKS_TIME);
//        int totalflexcount = alltimecount.get(SessionManager.KEY_TOTAL_FLEX_COUNT);
//        int totalflextime = alltimecount.get(SessionManager.KEY_TOTAL_FLEX_TIME);
        int totalsquatcount = alltimecount.get(SessionManager.KEY_TOTAL_SQUAT_COUNT);
        int totalsquattime = alltimecount.get(SessionManager.KEY_TOTAL_SQUAT_TIME);

        DecimalFormat timeTwoFormat = new DecimalFormat("00");
        int plankMin = (totalplanktime / 60) % 60;
        int jumpingJacksMin = (totaljumpingjackstime / 60) % 60;

        planktotaltime.setText(timeTwoFormat.format(TimeUnit.MINUTES.toHours(plankMin)) + ":" + timeTwoFormat.format(plankMin) + ":" + String.format("%02d", (totalplanktime % 60)));
        jumpingjackstotaltime.setText(timeTwoFormat.format(TimeUnit.MINUTES.toHours(jumpingJacksMin)) + ":" + timeTwoFormat.format(jumpingJacksMin) + ":" + String.format("%02d", (totaljumpingjackstime % 60)));
        pushuptotalcount.setText(totalpushupcount == -1 || totalpushupcount == 0? "-" : String.valueOf(totalpushupcount));

        int pushupMin = (totalpushuptime / 60) % 60;
        pushuptotaltime.setText(timeTwoFormat.format(TimeUnit.MINUTES.toHours(pushupMin)) + ":" + timeTwoFormat.format(pushupMin) + ":" + String.format("%02d", (totalpushuptime % 60)));
//        flextotalcount.setText(String.valueOf(totalflexcount));
//
//        int flexMin = (totalflextime / 60) % 60;
//        flextotaltime.setText(timeTwoFormat.format(TimeUnit.MINUTES.toHours(flexMin)) + ":" + timeTwoFormat.format(flexMin) + ":" + String.format("%02d", (totalflextime % 60)));
        squattotalcount.setText(totalsquatcount == -1 || totalsquatcount == 0? "-" : String.valueOf(totalsquatcount));

        int squatMin = (totalsquattime / 60) % 60;
        squattotaltime.setText(timeTwoFormat.format(TimeUnit.MINUTES.toHours(squatMin)) + ":" + timeTwoFormat.format(squatMin) + ":" + String.format("%02d", (totalsquattime % 60)));

        HashMap<String, Float> allhighscore = achievementManager.getScore();
        float bestplankscore = allhighscore.get(SessionManager.KEY_BEST_PLANK_SCORE);
        float bestjumpingjacksscore = allhighscore.get(SessionManager.KEY_BEST_JUMPING_JACKS_SCORE);
        float bestpushupscore = allhighscore.get(SessionManager.KEY_BEST_PUSHUP_SCORE);
        float bestflexscore = allhighscore.get(SessionManager.KEY_BEST_FLEX_SCORE);
        float bestsquatscore = allhighscore.get(SessionManager.KEY_BEST_SQUAT_SCORE);
        float highScore = allhighscore.get(SessionManager.KEY_HIGHSCORE);

        DecimalFormat dff = new DecimalFormat("0.00");
        if (bestplankscore != -1 && bestplankscore != 0) {
            plankbestscore.setText(dff.format(bestplankscore));
        } else {
            plankbestscore.setText("-");
        }
        if (bestjumpingjacksscore != -1 && bestjumpingjacksscore != 0) {
            jumpingjacksbestscore.setText(dff.format(bestjumpingjacksscore));
        } else {
            jumpingjacksbestscore.setText("-");
        }
        if (bestpushupscore != -1 && bestpushupscore != 0) {
            pushupbestscore.setText(dff.format(bestpushupscore));
        } else {
            pushupbestscore.setText("-");
        }
//        if (bestflexscore != 0) {
//            flexbestscore.setText(dff.format(bestflexscore));
//        } else {
//            flexbestscore.setText("-");
//        }
        if (bestsquatscore != -1 && bestsquatscore != 0) {
            squatbestscore.setText(dff.format(bestsquatscore));
        } else {
            squatbestscore.setText("-");
        }

        if (squat_count != -1 && squat_count != 0) {
            squatsCountDisp.setText(String.valueOf(squat_count));
        } else {
            squatsCountDisp.setText("-");
        }
//        if (flexibility_count != 0) {
//            flexibilityCountDisp.setText(String.valueOf(flexibility_count));
//        } else {
//            flexibilityCountDisp.setText("-");
//        }
        if (pushup_count != -1 && pushup_count != 0) {
            pushupCountDisp.setText(String.valueOf(pushup_count));
        } else {
            pushupCountDisp.setText("-");
        }
        if (plank_durationin_seconds != 0) {
            int min = (plank_durationin_seconds / 60) % 60;
            plankCountDisp.setText(min + ":" + String.format("%02d", (plank_durationin_seconds % 60)));
        } else {
            plankCountDisp.setText("-");
        }
        if (jump_duration_seconds != 0) {
            int min = (jump_duration_seconds / 60) % 60;
            jumpCountDisp.setText(min + ":" + String.format("%02d", (jump_duration_seconds % 60)));
        } else {
            jumpCountDisp.setText("-");
        }

        ArrayList<Integer> highScoreLimitList = new ArrayList<>();
        highScoreLimitList.add(100);
        highScoreLimitList.add(1000);
        highScoreLimitList.add(10000);

        if (highScore <= highScoreLimitList.get(0)) {
            int scoreToInt = (int) highScore;
            badgeProgress.setMax(highScoreLimitList.get(0));
            badgeProgress.setProgress(scoreToInt);
        } else if (highScore <= highScoreLimitList.get(1)) {
            int scoreToInt = (int) highScore;
            badgeProgress.setMax(highScoreLimitList.get(1));
            badgeProgress.setProgress(scoreToInt);
        } else if (highScore <= highScoreLimitList.get(2)) {
            int scoreToInt = (int) highScore;
            badgeProgress.setMax(highScoreLimitList.get(2));
            badgeProgress.setProgress(scoreToInt);
        }

        return view;
    }

    private static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private static File store(Bitmap bm, String fileName) {
        String dirPath = Environment.getExternalStorageDirectory().toString() + "/Screenshots";
        File dir = new File(dirPath);
        File screenshotFile;
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            screenshotFile = new File(dir, fileName);
            screenshotFile.createNewFile();
            // if (file.exists ()) file.delete ();
            // file.mkdirs();

            FileOutputStream fOut = new FileOutputStream(screenshotFile);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();

            return screenshotFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void shareImage(File file) {
        Uri uri = FileProvider.getUriForFile(getContext(), "com.auzora.perfit.provider", file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Check out my stats on Perfit Fitness, a new app that tracks your fitness levels! Download from https://play.google.com/store/apps/details?id=com.auzora.perfit");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No App Available", Toast.LENGTH_SHORT).show();
            Log.e("Error", e.toString());
        }
    }
}
