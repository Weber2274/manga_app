package com.example.test.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.test.model.MangaItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.firebase.*;
import com.google.firebase.firestore.FirebaseFirestore;

public class LikeRepository {
    private static LikeRepository instance;

    // 快取收藏資料
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
    }
    public void clearCache() {
        cache.clear();
    }
}
