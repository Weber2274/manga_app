package com.example.test.model;

import java.util.List;

public class MangaDetail {
    private String coverImg;
    private String title;
    private String author;
    private String year;
    private String area;
    private String status;
    private List<String> pageTitle;
    public MangaDetail(String coverImg, String title, String author, String year, String area, String status, List<String> pageTitle) {
        this.coverImg = coverImg;
        this.title = title;
        this.author = author;
        this.year = year;
        this.area = area;
        this.status = status;
        this.pageTitle = pageTitle;
    }

    public String getCoverImg() {
        return coverImg;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getYear() {
        return year;
    }

    public String getArea() {
        return area;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getPageTitle(){return pageTitle;}


}
