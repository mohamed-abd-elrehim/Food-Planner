package com.example.mealmate.model;
public class MediaItems {
    private String url;
    private boolean isVideo;

    public MediaItems(String url, boolean isVideo) {
        this.url = url;
        this.isVideo = isVideo;
    }

    public String getUrl() {
        return url;
    }

    public boolean isVideo() {
        return isVideo;
    }
}
