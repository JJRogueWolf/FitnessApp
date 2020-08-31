package org.perfit.fitness.youtube;

public class  YoutubeVideoModel {
    private String videoId, title, desc;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescrip() {
        return desc;
    }

    public void setDescrip(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "YoutubeVideoModel{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + desc + '\'' +
                '}';
    }
}
