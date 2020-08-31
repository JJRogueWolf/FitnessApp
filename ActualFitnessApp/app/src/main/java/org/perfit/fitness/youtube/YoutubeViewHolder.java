package org.perfit.fitness.youtube;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeThumbnailView;

import org.perfit.fitness.R;

public class YoutubeViewHolder extends RecyclerView.ViewHolder {

    public final YouTubeThumbnailView videoThumbnail;
    public final TextView videoTitle;
    public final TextView videoDesc;

    public YoutubeViewHolder(View itemView){
        super(itemView);
        videoThumbnail = itemView.findViewById(R.id.youtube_thumbnail);
        videoTitle = itemView.findViewById(R.id.youtube_title);
        videoDesc = itemView.findViewById(R.id.youtube_desc);
    }
}
