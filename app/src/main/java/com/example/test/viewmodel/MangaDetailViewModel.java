package com.example.test.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test.model.MangaDetail;
import com.example.test.repository.MangaDetailRepository;

public class MangaDetailViewModel extends ViewModel {

    private MutableLiveData<MangaDetail> mangaDetailLiveData;
    private MangaDetailRepository repository;
    private MutableLiveData<String> errorLiveData;
    private MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private boolean isLoading = false;
    public LiveData<Boolean> getLoading() {
        return loadingLiveData;
    }

    public MangaDetailViewModel(){
        mangaDetailLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        repository = new MangaDetailRepository();
    }

    public LiveData<MangaDetail> getMangaDetailLiveData() {
        return mangaDetailLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    public void loadMangaDetail(String title){
        if (isLoading) return;
        isLoading = true;
        loadingLiveData.postValue(true);
        repository.fetchMangaDetail(title, new MangaDetailRepository.OnDetailLoadedListener() {
            @Override
            public void onSuccess(MangaDetail mangaDetail) {
                isLoading = false;
                loadingLiveData.postValue(false);
                mangaDetailLiveData.postValue(mangaDetail);
            }

            @Override
            public void onError(String error) {
                isLoading = false;
                loadingLiveData.postValue(false);
                errorLiveData.postValue(error);
            }
        });
    }
}
