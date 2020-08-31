package org.perfit.fitness.youtube;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.perfit.fitness.R;
import org.perfit.fitness.dashboard.DashBoard;

import java.util.ArrayList;

public class YoutubeVideoAdapter extends RecyclerView.Adapter<YoutubeViewHolder> {
    private final ArrayList<YoutubeVideoModel> youtubeVideoModelArraylist;

    public YoutubeVideoAdapter(ArrayList<YoutubeVideoModel> youtubeVideoModelArraylist){
        this.youtubeVideoModelArraylist = youtubeVideoModelArraylist;
    }

    @NonNull
    @Override
    public YoutubeViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.youtube_list_view, parent,false);
//        YoutubeViewHolder youtubeViewHolder = new YoutubeViewHolder(view);
//        youtubeViewHolder.recoVideoLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "Click Working", Toast.LENGTH_SHORT).show();
//            }
//        });
//        return youtubeViewHolder;
        return new YoutubeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubeViewHolder holder, int position) {
        final YoutubeVideoModel youtubeVideoModel = youtubeVideoModelArraylist.get(position);
        holder.videoTitle.setText(youtubeVideoModel.getTitle());
        holder.videoDesc.setText(youtubeVideoModel.getDescrip());


        /*  initialize the thumbnail image view , we need to pass Developer Key */
        holder.videoThumbnail.initialize(DashBoard.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                //when initialization is sucess, set the video id to thumbnail to load
                youTubeThumbnailLoader.setVideo(youtubeVideoModel.getVideoId());

                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        //print or show error when thumbnail load failed
                        Log.e("Error","Youtube Thumbnail Load Failed");
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                //print or show error when initialization failed
                Log.e("Error","Youtube Initialization Failed");
            }
        });
    }

    @Override
    public int getItemCount() {
        return youtubeVideoModelArraylist != null ? youtubeVideoModelArraylist.size() : 0;
    }
}
