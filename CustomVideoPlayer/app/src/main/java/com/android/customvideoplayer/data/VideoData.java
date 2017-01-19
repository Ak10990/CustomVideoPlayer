package com.android.customvideoplayer.data;

/**
 * Created by akanksha on 26/7/16.
 */
public class VideoData {

    private String videoUrl;
    private int thumbUrl;

    public VideoData(String videoUrl, int thumbUrl) {
        this.videoUrl = videoUrl;
        this.thumbUrl = thumbUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getThumbUrl() {
        return thumbUrl;
    }
}
