package com.example.test.repository;

import android.util.Log;

import com.example.test.model.MangaItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private static CategoryRepository instance;

    private CategoryRepository() {}

    public static synchronized CategoryRepository getInstance() {
        if (instance == null) {
            instance = new CategoryRepository();
        }
        return instance;
    }

    public interface OnCategoryGroupedListener {
        void onSuccess(List<MangaItem> groupedData);
        void onError(String error);
    }

    public void fetchMangaGroupedByCategory(String categoryName, OnCategoryGroupedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        db.setFirestoreSettings(settings);
        db.collection("comics")
                .whereEqualTo("categoryTitle", categoryName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<MangaItem> mangaList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String title = document.getString("title");
                        Log.d("Repository", "title: " + title);
                        String imageUrl = "https:" + document.getString("cover");
                        Log.d("Repository", "imageUrl: " + imageUrl);
                        mangaList.add(new MangaItem(title, imageUrl));
                    }

                    listener.onSuccess(mangaList);
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                });
        }
}
