package com.example.test.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test.model.ItemList;
import com.example.test.model.MangaItem;
import com.example.test.repository.CategoryRepository;
import com.example.test.repository.HomeRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private MutableLiveData<List<MangaItem>> mangaListLiveData;
    private CategoryRepository categoryRepository;
    private MutableLiveData<String> errorLiveData;
    public CategoryViewModel() {
        categoryRepository = new CategoryRepository();
        mangaListLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }
    public LiveData<List<MangaItem>> getCategory() {
        return mangaListLiveData;
    }
    public LiveData<String> getError() {
        return errorLiveData;
    }
    private boolean isLoading = false;
    public void loadCategories(String url){
        if (isLoading) return;
        isLoading = true;
        mangaListLiveData.postValue(null);
        categoryRepository.fetchMangaFromCategoryPage(url, new CategoryRepository.OnCategoryLoadedListener() {
            @Override
            public void onSuccess(List<MangaItem> mangaItems) {
                isLoading = false;
                mangaListLiveData.postValue(mangaItems);
            }

            @Override
            public void onError(String error) {
                isLoading = false;
                errorLiveData.postValue(error);
            }
        });
    }
}
