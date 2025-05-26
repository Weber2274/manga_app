package com.example.test.model;

public class HistoryItem {
    private String book;
    private String bookUrl;
    private String episode;
    private String episodeUrl;
    private String htime;

    public HistoryItem(String book, String bookUrl, String episode, String episodeUrl, String htime) {
        this.book = book;
        this.bookUrl = bookUrl;
        this.episode = episode;
        this.episodeUrl = episodeUrl;
        this.htime = htime;
    }

    public String getBook() {
        return book;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public String getEpisode() {
        return episode;
    }

    public String getEpisodeUrl() {
        return episodeUrl;
    }

    public String getHtime() {
        return htime;
    }
}
