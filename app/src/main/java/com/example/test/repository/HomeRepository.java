package com.example.test.repository;

import android.util.Log;

import com.google.firebase.firestore.*;
import com.example.test.model.Book;

import java.util.ArrayList;
import java.util.List;

public class HomeRepository {
    private static HomeRepository instance;

    private HomeRepository() {}

    public static HomeRepository getInstance() {
        if (instance == null) {
            instance = new HomeRepository();
        }
        return instance;
    }

    public interface OnHomePageLoadedListener {
        void onSuccess(List<Book> allBooks);
        void onError(String error);
    }

    public void fetchMangaFromHomePage(OnHomePageLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comics")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> allBooks = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String title = doc.getString("title");
                        Log.d("HomeRepository", "title: " + title);
                        String imageUrl = "https:" + doc.getString("cover");

                        Book book = new Book(title, imageUrl);
                        allBooks.add(book);
                    }

                    listener.onSuccess(allBooks);
                })
                .addOnFailureListener(e -> {
                    listener.onError("讀取漫畫失敗：" + e.getMessage());
                });
    }
}
