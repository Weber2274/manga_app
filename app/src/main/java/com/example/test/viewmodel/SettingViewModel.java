package com.example.test.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test.model.Setting;
import com.example.test.repository.SettingRepository;

public class SettingViewModel extends ViewModel {
    private final MutableLiveData<Setting> settingData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> updateResult = new MutableLiveData<>();
    private final SettingRepository repo = new SettingRepository();

    public LiveData<Setting> getSettingData() { return settingData; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getUpdateResult() { return updateResult; }

    public void loadData() {
        if (Boolean.TRUE.equals(isLoading.getValue())) return;

        isLoading.postValue(true);
        repo.loadData(new SettingRepository.OnSettingLoadedListener() {
            @Override
            public void onSuccess(Setting s) {
                settingData.postValue(s);
                isLoading.postValue(false);
                errorMessage.postValue(null);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
                isLoading.postValue(false);
            }
        });
    }

    public void updateData(Setting s) {
        if (Boolean.TRUE.equals(isLoading.getValue()) || s == null) return;

        isLoading.postValue(true);
        repo.updateData(s, new SettingRepository.OnSettingUpdatedListener() {
            @Override
            public void onSuccess(String msg) {
                updateResult.postValue(msg);
                settingData.postValue(s);
                isLoading.postValue(false);
                errorMessage.postValue(null);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
                isLoading.postValue(false);
                updateResult.postValue(null);
            }
        });
    }

    public void clearData() {
        settingData.postValue(null);
        errorMessage.postValue(null);
        updateResult.postValue(null);
        isLoading.postValue(false);
    }
}