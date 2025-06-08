package com.example.test.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test.model.Book;
import com.example.test.model.CategoryTitleItem;
import com.example.test.model.ItemList;
import com.example.test.model.MangaGroup;
import com.example.test.model.MangaItem;
import com.example.test.repository.HomeRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<MangaItem>> flatListLiveData;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private HomeRepository homeRepository;
    private MutableLiveData<String> errorLiveData;

    public HomeViewModel() {
        flatListLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }

    public LiveData<List<MangaItem>> getMangas() {
        return flatListLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadMangas() {
        if (Boolean.TRUE.equals(isLoading.getValue())) return;

        isLoading.setValue(true);

        HomeRepository.getInstance().fetchMangaFromHomePage(new HomeRepository.OnHomePageLoadedListener() {
            @Override
            public void onSuccess(List<Book> books) {
                isLoading.postValue(false);
                List<MangaItem> flatList = new ArrayList<>();

                // 只處理第一組 MangaGroup，並略過分類標題
                if (!books.isEmpty()) {
                    for (Book manga : books) {
                        flatList.add(new MangaItem(manga.getTitle(), manga.getImageUrl()));
                        Log.d("HomeViewModel", "取得資料成功，圖片網址: " + manga.getImageUrl());
                    }
                }

                Log.d("HomeViewModel", "總共資料筆數: " + flatList.size());
                flatListLiveData.postValue(flatList);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }
}
