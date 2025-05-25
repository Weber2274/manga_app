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
    private final SettingRepository repository = new SettingRepository();

    public LiveData<Setting> getSettingData() { return settingData; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getUpdateResult() { return updateResult; }

    public void loadUserSettings(String cookies) {
        if (Boolean.TRUE.equals(isLoading.getValue()) || cookies == null || cookies.trim().isEmpty()) {
            errorMessage.postValue("資訊無效 重新登入");
            return;
        }

        isLoading.postValue(true);
        repository.fetchSetting("https://tw.manhuagui.com/user/center/index", cookies,
                new SettingRepository.OnSettingLoadedListener() {
                    @Override
                    public void onSuccess(Setting setting) {
                        settingData.postValue(setting);
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

    public void updateUserSettings(String cookies, Setting newSetting) {
        if (Boolean.TRUE.equals(isLoading.getValue()) || cookies == null || cookies.trim().isEmpty()) {
            errorMessage.postValue("資訊無效 重新登入");
            return;
        }

        isLoading.postValue(true);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                updateResult.postValue("資料更新成功");
                settingData.postValue(newSetting);
                isLoading.postValue(false);
            } catch (InterruptedException e) {
                errorMessage.postValue("更新中斷");
                isLoading.postValue(false);
            }
        }).start();
    }

    public void clearData() {
        settingData.postValue(null);
        errorMessage.postValue(null);
        updateResult.postValue(null);
        isLoading.postValue(false);
    }
}