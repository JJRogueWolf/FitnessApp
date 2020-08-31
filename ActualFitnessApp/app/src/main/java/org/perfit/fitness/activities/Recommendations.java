package org.perfit.fitness.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.perfit.fitness.R;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.VolleyCallback;
import org.perfit.fitness.youtube.YoutubeVideoAdapter;
import org.perfit.fitness.youtube.YoutubeVideoModel;

import java.util.ArrayList;
import java.util.HashMap;

public class Recommendations extends Fragment {

    private int userId;
    private final String[] videoId_API = new String[6];
    private final String[] videoTitle_API = new String[6];
    private final String[] videoDesc_API = new String[6];
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workouts, container, false);
        SessionManager recommendationManager = new SessionManager(getContext());
        HashMap<String, Integer> userIdForRecommendation = recommendationManager.getUserId();
        userId = userIdForRecommendation.get(SessionManager.KEY_USERID);
        callApiforVideoRecommendation();
        recyclerView = view.findViewById(R.id.youtube_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    private void callApiforVideoRecommendation() {
        JSONObject videoParameter = new JSONObject();
        try {
            videoParameter.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIManager.CallAPI(APIManager.LOAD_RECOMMENDATION_VIDEOS, videoParameter, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                handleSuccessResponse(result);
            }

            @Override
            public void onFailureResponse(String result) {
                handleFailureResponse(result);
            }
        });
    }

    private void handleSuccessResponse(JSONObject result) {
        try {
            switch (result.getString("status")) {
                case "1":
                case "2":
                    videoId_API[4] = result.getJSONObject("plank_1").getString("plank_url_1");
                    videoDesc_API[4] = result.getJSONObject("plank_1").getString("plank_short_1");
                    videoTitle_API[4] = result.getJSONObject("plank_1").getString("plank_title_1");
                    videoId_API[2] = result.getJSONObject("pushup_1").getString("pushup_url_1");
                    videoDesc_API[2] = result.getJSONObject("pushup_1").getString("pushup_short_1");
                    videoTitle_API[2] = result.getJSONObject("pushup_1").getString("pushup_title_1");
                    videoId_API[0] = result.getJSONObject("squat_1").getString("squat_url_1");
                    videoDesc_API[0] = result.getJSONObject("squat_1").getString("squat_short_1");
                    videoTitle_API[0] = result.getJSONObject("squat_1").getString("squat_title_1");
                    videoId_API[5] = result.getJSONObject("plank_2").getString("plank_url_2");
                    videoDesc_API[5] = result.getJSONObject("plank_2").getString("plank_short_2");
                    videoTitle_API[5] = result.getJSONObject("plank_2").getString("plank_title_2");
                    videoId_API[3] = result.getJSONObject("pushup_2").getString("pushup_url_2");
                    videoDesc_API[3] = result.getJSONObject("pushup_2").getString("pushup_short_2");
                    videoTitle_API[3] = result.getJSONObject("pushup_2").getString("pushup_title_2");
                    videoId_API[1] = result.getJSONObject("squat_2").getString("squat_url_2");
                    videoDesc_API[1] = result.getJSONObject("squat_2").getString("squat_short_2");
                    videoTitle_API[1] = result.getJSONObject("squat_2").getString("squat_title_2");
                    populateRecyclerView();
                    break;
                default:
                    Log.i("Error", "Server");
                    Toast.makeText(getContext(), "Unknown Error", Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unknown error", Toast.LENGTH_SHORT).show();
            Log.i("Error", e.getMessage());
        }
    }

    private void handleFailureResponse(String result) {
        Log.e("Error", result);
    }

    private void populateRecyclerView() {
        final ArrayList<YoutubeVideoModel> youtubeVideoModelArrayList = generatedByVideoList();
        YoutubeVideoAdapter adapter = new YoutubeVideoAdapter(youtubeVideoModelArrayList);
        recyclerView.setAdapter(adapter);

        final GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    watchYoutubeVideo(videoId_API[recyclerView.getChildPosition(child)]);
//                    Toast.makeText(getContext(), "Position" + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private ArrayList<YoutubeVideoModel> generatedByVideoList() {
        ArrayList<YoutubeVideoModel> youtubeVideoModelArrayList = new ArrayList<>();
        for (int i = 0; i < videoId_API.length; i++) {
            YoutubeVideoModel youtubeVideoModel = new YoutubeVideoModel();
            youtubeVideoModel.setVideoId(videoId_API[i]);
            youtubeVideoModel.setTitle(videoTitle_API[i]);
            youtubeVideoModel.setDescrip(videoDesc_API[i]);

            youtubeVideoModelArrayList.add(youtubeVideoModel);
        }
        return youtubeVideoModelArrayList;
    }

    private void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}
