package com.example.test.model;

public class CategoryTitleItem implements ItemList{
    private String categoryTitle;

    public CategoryTitleItem(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    @Override
    public int getType() {
        return 1;
    }
}
