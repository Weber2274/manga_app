package com.example.test.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test.model.MangaItem;
import com.example.test.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<List<MangaItem>> mangaListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final CategoryRepository repository = CategoryRepository.getInstance();

    public void loadCategory(String categoryName) {
        loadingLiveData.setValue(true);
        repository.fetchMangaGroupedByCategory(categoryName, new CategoryRepository.OnCategoryGroupedListener() {
            @Override
            public void onSuccess(List<MangaItem> groupedData) {
                mangaListLiveData.setValue(groupedData);
                loadingLiveData.setValue(false);
            }

            @Override
            public void onError(String error) {
                errorLiveData.setValue(error);
                loadingLiveData.setValue(false);
            }
        });
    }

    public LiveData<List<MangaItem>> getMangaList() {
        return mangaListLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }
}
