package com.example.test.repository;

import android.content.Context;
import android.util.Log;

import com.example.test.model.MangaItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class LikeRepository {
    private static LikeRepository instance;

    // 快取已收藏漫畫
    private final List<MangaItem> cache = new ArrayList<>();

    public static synchronized LikeRepository getInstance() {
        if (instance == null) {
            instance = new LikeRepository();
        }
        return instance;
    }

    public interface OnLikeLoadedListener {
        void onSuccess(List<MangaItem> mangaItems);
        void onError(String error);
    }

    //從 Firebase 中取得該使用者的收藏漫畫
    public void fetchAllMangaFromShelf(Context context, OnLikeLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            listener.onSuccess(new ArrayList<>()); // 如果沒登入，回傳空清單
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("favorites")
                .get()
                .addOnSuccessListener(snapshot -> {
                   List<MangaItem> list = new ArrayList<>();
                   for(DocumentSnapshot doc : snapshot.getDocuments()){
                       String title = doc.getString("title");
                       String cover = doc.getString("cover");

                       if (title != null && cover != null) {
                           MangaItem item = new MangaItem(title, cover);
                           item.setTitle(title);
                           item.setImgUrl(cover);
                           list.add(item);
                       }
                   }

                   cache.clear();
                   cache.addAll(list);
                   listener.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                   Log.e("LikeRepository", "讀取收藏失敗");
                    listener.onError("讀取收藏失敗：" + e.getMessage());
                });
    }
    public void clearCache() {
        cache.clear();
    }
}
