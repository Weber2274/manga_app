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

public class LikeRepository {
    private static LikeRepository instance;

    // 快取收藏資料
    private final List<MangaItem> cache = new ArrayList<>();

    private LikeRepository() {
    }

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
        if (!cache.isEmpty()) {
            listener.onSuccess(cache);
            return;
        }

        new Thread(() -> {
            SharedPreferences prefs = context.getSharedPreferences("Myprefs", Context.MODE_PRIVATE);
            String cookie = prefs.getString("cookie", "");
            List<MangaItem> allManga = new ArrayList<>();

            try {
                // 1. 抓第一頁來取得總記錄與第一頁資料
                String baseUrl = "https://tw.manhuagui.com/user/book/shelf/";
                Document firstPage = Jsoup.connect(baseUrl + "1")
                        .userAgent("Mozilla/5.0")
                        .header("Accept-Language", "zh-TW,zh;q=0.9")
                        .header("Connection", "keep-alive")
                        .header("Cookie", cookie)
                        .timeout(15000)
                        .get();

                // 2. 擷取總記錄數
                Element totalRecordElement = firstPage.selectFirst("span:matchesOwn(共\\d+記錄)");
                int totalRecords = 0;
                if (totalRecordElement != null) {
                    String text = totalRecordElement.text(); // e.g., 共118記錄
                    totalRecords = Integer.parseInt(text.replaceAll("\\D+", ""));
                }
                int totalPages = (int) Math.ceil(totalRecords / 20.0);
                Log.d("LikeRepository", "總記錄：" + totalRecords + "，共 " + totalPages + " 頁");

                // 3. 開始抓每一頁資料
                for (int page = 1; page <= totalPages; page++) {
                    Log.d("LikeRepository", "抓取第 " + page + " 頁");
                    Document document = page == 1 ? firstPage :
                            Jsoup.connect(baseUrl + page)
                                    .userAgent("Mozilla/5.0")
                                    .header("Accept-Language", "zh-TW,zh;q=0.9")
                                    .header("Connection", "keep-alive")
                                    .header("Cookie", cookie)
                                    .timeout(15000)
                                    .get();

                    Elements mangas = document.select("div.dy_content_li");

                    for (Element element : mangas) {
                        String title = element.select("img").attr("alt");
                        String imgUrl = element.select("img").attr("src");
                        if (imgUrl != null && !imgUrl.startsWith("http")) {
                            imgUrl = "https:" + imgUrl;
                        }
                        String href = element.select("a").attr("href");
                        String pageUrl = "https://tw.manhuagui.com" + href;
                        allManga.add(new MangaItem(title, imgUrl, pageUrl));
                    }
                }

                cache.clear();
                cache.addAll(allManga);
                listener.onSuccess(allManga);

            } catch (IOException e) {
                listener.onError("資料抓取失敗: " + e.getMessage());
            }
        }).start();
    }
    public void clearCache() {
        cache.clear();
    }
}
