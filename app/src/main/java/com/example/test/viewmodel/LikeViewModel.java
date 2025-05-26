package com.example.test.viewmodel;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.test.model.MangaItem;
import com.example.test.repository.LikeRepository;

import java.util.ArrayList;
import java.util.List;

public class LikeViewModel extends ViewModel {

    private final MutableLiveData<List<MangaItem>> mangaItems = new MutableLiveData<>();
    private final LikeRepository likeRepository = LikeRepository.getInstance();

    public LikeViewModel() {
        mangaItems.setValue(new ArrayList<>());
    }
    public LiveData<List<MangaItem>> getMangaItems() {
        Log.d("LikeViewModel", "取得的資料數量: " + mangaItems.getValue().size());
        return mangaItems;
    }

    public void loadMangaData(Context context) {
        likeRepository.fetchAllMangaFromShelf(context, new LikeRepository.OnLikeLoadedListener() {
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
        LikeRepository.getInstance().clearCache();
        loadMangaData(context);
    }
}
