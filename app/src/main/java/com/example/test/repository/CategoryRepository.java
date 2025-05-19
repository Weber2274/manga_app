package com.example.test.repository;

import android.util.Log;

import com.example.test.model.MangaItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CategoryRepository {
    private static CategoryRepository instance;
    private final Map<String, List<MangaItem>> cache = new HashMap<>();

    private CategoryRepository() {
    }

    public static synchronized CategoryRepository getInstance() {
        if (instance == null) {
            instance = new CategoryRepository();
        }
        return instance;
    }

    public interface OnCategoryLoadedListener {
        void onSuccess(List<MangaItem> mangaItems);
        void onError(String error);
    }

    public void fetchMangaFromCategoryPage(String url, OnCategoryLoadedListener listener) {
        if (cache.containsKey(url)) {
            listener.onSuccess(cache.get(url));
            return;
        }

        new Thread(() -> {
            try {
                Document document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .referrer("https://www.google.com/")
                        .header("Accept-Language", "zh-TW,zh;q=0.9")
                        .header("Connection", "keep-alive")
                        .timeout(15000)
                        .get();

                List<MangaItem> mangaItemList = new ArrayList<>();
                Elements mangas = document.select("div.book-list ul#contList > li");
                int i = 0;
                for (Element element : mangas) {
                    String title = element.select("a.bcover").attr("title");
                    String imgUrl = element.select("a.bcover img").attr("data-src");
                    if (imgUrl == null || imgUrl.isEmpty()) {
                        imgUrl = element.select("a.bcover img").attr("src");
                    }
                    if (!imgUrl.startsWith("http")) {
                        imgUrl = "https:" + imgUrl;
                    }
                    String href = element.select("a.bcover").attr("href");
                    String pageUrl = "https://tw.manhuagui.com" + href;
                    mangaItemList.add(new MangaItem(title, imgUrl, pageUrl));

                }

                cache.put(url, mangaItemList);
                listener.onSuccess(mangaItemList);
            } catch (IOException e) {
                listener.onError("資料抓取失敗: " + e.getMessage());
            }
        }).start();
    }
}
