package com.example.test.model;

import java.util.List;

public class MangaGroup {
    private String filter;
    private List<Book> bookList;

    public MangaGroup(String filter, List<Book> bookList) {
        this.filter = filter;
        this.bookList = bookList;
    }

    public String getFilter() {
        return filter;
    }

    public List<Book> getMangaList() {
        return bookList;
    }
}
