//package com.example.test.viewmodel;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.example.test.model.LikeItem;
//import com.example.test.repository.LikeRepository;
//
//import java.util.List;
//
//public class LikeViewModel {
//    private final MutableLiveData<List<LikeItem>> mangaListLiveData;
//    private final LikeRepository likeRepository;
//    private final MutableLiveData<String> errorLiveData;
//    private boolean isLoading = false;
//
//    public LikeViewModel() {
//        likeRepository = LikeRepository.getInstance(); // ✅ 修正這裡
//        mangaListLiveData = new MutableLiveData<List<LikeItem>>();
//        errorLiveData = new MutableLiveData<>();
//    }
//
//    public LiveData<List<LikeItem>> getCategory() {
//        return mangaListLiveData;
//    }
//
//    public LiveData<String> getError() {
//        return errorLiveData;
//    }
//
//    public void loadCategories(String url) {
//        if (isLoading) return;
//        isLoading = true;
//        mangaListLiveData.postValue(null);
//
//        likeRepository.fetchMangaFromLikePage(url, new LikeRepository.OnLikeLoadedListener() {
//            @Override
//            public void onSuccess(List<LikeItem> mangaItems) {
//                isLoading = false;
//                mangaListLiveData.postValue(mangaItems);
//            }
//
//            @Override
//            public void onError(String error) {
//                isLoading = false;
//                errorLiveData.postValue(error);
//            }
//        });
//    }
//}
