package com.example.test.model;

public class MangaItem implements ItemList{
    private String title;
    private String imgUrl;

    public MangaItem(String title, String imgUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }


    @Override
    public int getType() {
        return 2;
    }
}
