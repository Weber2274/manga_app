package com.example.test.model;

public class LikeItem {
    private String title;
    private String imageUrl;
    private String pageUrl;

    public LikeItem(String title, String imageUrl, String pageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.pageUrl = pageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }
}

