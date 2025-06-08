package com.example.test.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.test.model.MangaItem;
import com.example.test.repository.HistoryRepository;
import com.example.test.repository.LikeRepository;

import java.util.ArrayList;
import java.util.List;

public class HistoryViewModel extends ViewModel {

    private final MutableLiveData<List<MangaItem>> mangaItems = new MutableLiveData<>();
    private final HistoryRepository historyRepository = HistoryRepository.getInstance();

    public HistoryViewModel() {
        mangaItems.setValue(new ArrayList<>());
    }
    public LiveData<List<MangaItem>> getMangaItems() {
        Log.d("LikeViewModel", "取得的資料數量: " + mangaItems.getValue().size());
        return mangaItems;
    }

    public void loadMangaData(Context context) {
        historyRepository.fetchAllMangaFromShelf(new HistoryRepository.OnLikeLoadedListener() {
            @Override
            public void onSuccess(List<MangaItem> mangaItemsList) {
                mangaItems.postValue(mangaItemsList);
            }

            @Override
            public void onError(String error) {
                mangaItems.postValue(null);
            }
        });
    }

    public void refreshMangaData(Context context) {
        HistoryRepository.getInstance().clearCache();
        loadMangaData(context);
    }
}