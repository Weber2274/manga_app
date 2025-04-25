package com.example.test.model;

public class MangaItem implements ItemList{
    private String title;
    private String imgUrl;
    private String pageUrl;

    public MangaItem(String title, String imgUrl, String pageUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.pageUrl = pageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    @Override
    public int getType() {
        return 2;
    }
}
