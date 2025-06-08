package com.example.test.model;

public class Book {
    private String title;
    private String imageUrl;

    public Book(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
}
