package com.example.test.repository;

import android.util.Log;

import com.example.test.model.MangaDetail;
import com.example.test.model.MangaItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MangaDetailRepository {
    public interface OnDetailLoadedListener{
        void onSuccess(MangaDetail mangaDetail);
        void onError(String error);
    }

    public void fetchMangaDetail(String title, OnDetailLoadedListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        db.setFirestoreSettings(settings);
        db.collection("comics")
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    MangaDetail mangaDetail = null;
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String id = document.getId();
                        String imageUrl = "https:" + document.getString("cover");
                        String year = document.getString("year");
                        String area = document.getString("area");
                        String author = document.getString("author");
                        String status = document.getString("status");
                        mangaDetail = new MangaDetail(id,imageUrl,title,author,year,area,status);
                    }

                    listener.onSuccess(mangaDetail);
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                });
    }
}
