package com.example.test.repository;

import android.content.Context;
import android.util.Log;

import com.example.test.model.MangaItem;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

    public void fetchAllMangaFromShelf(Context context, OnLikeLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            listener.onSuccess(new ArrayList<>());
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("favorites")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        DocumentReference comicRef = doc.getDocumentReference("comic");
                        if (comicRef != null) {
                            tasks.add(comicRef.get());
                        }
                    }

                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
                        List<MangaItem> list = new ArrayList<>();

                        for (Object result : results) {
                            if (result instanceof DocumentSnapshot) {
                                DocumentSnapshot comicDoc = (DocumentSnapshot) result;

                                String title = comicDoc.getString("title");
                                String cover = "https:" + comicDoc.getString("cover");
                                Log.d("LikeRepository", title);

                                if (title != null && cover != null) {
                                    MangaItem item = new MangaItem(title, cover);
                                    list.add(item);
                                }
                            }
                        }

                        cache.clear();
                        cache.addAll(list);
                        listener.onSuccess(list);

                    }).addOnFailureListener(e -> {
                        Log.e("LikeRepository", "漫畫資料讀取失敗", e);
                        listener.onError("漫畫資料讀取失敗：" + e.getMessage());
                    });

                }).addOnFailureListener(e -> {
                    Log.e("LikeRepository", "收藏清單讀取失敗", e);
                    listener.onError("收藏清單讀取失敗：" + e.getMessage());
                });
    }
    public void clearCache() {
        cache.clear();
    }
}