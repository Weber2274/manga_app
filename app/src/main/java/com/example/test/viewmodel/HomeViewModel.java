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
    private MutableLiveData<List<ItemList>> flatListLiveData;
    private HomeRepository homeRepository;
    private MutableLiveData<String> errorLiveData;
    public HomeViewModel() {
//        homeRepository = new HomeRepository();
        flatListLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }
    public LiveData<List<ItemList>> getMangas() {
        return flatListLiveData;
    }
    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void loadMangas() {
        HomeRepository.getInstance().fetchMangaFromHomePage(new HomeRepository.OnHomePageLoadedListener() {
            @Override
            public void onSuccess(List<MangaGroup> categories) {
                List<ItemList> flatList = new ArrayList<>();
                for(MangaGroup group : categories){
                    flatList.add(new CategoryTitleItem(group.getFilter()));

                    for(Book manga : group.getMangaList()){
                        flatList.add(new MangaItem(manga.getTitle(), manga.getImageUrl(), manga.getPageUrl()));
                        Log.d("HomeViewModel", "取得資料成功，分類數量: " + manga.getImageUrl());

                    }

                }
                Log.d("HomeViewModel", "總共資料筆數: " + flatList.size());
                flatListLiveData.postValue(flatList);

            }
            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }
}
