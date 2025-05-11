package com.example.test.repository;

import android.util.Log;

import com.example.test.model.MangaDetail;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;

public class MangaDetailRepository {
    public interface OnDetailLoadedListener{
        void onSuccess(MangaDetail mangaDetail);
        void onError(String error);
    }

    public void fetchMangaDetail(String url, OnDetailLoadedListener listener){
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .referrer("https://www.google.com/")
                        .header("Accept-Language", "zh-TW,zh;q=0.9")
                        .header("Connection", "keep-alive")
                        .timeout(15000)
                        .get();


                String imgUrl = doc.select(".hcover img").attr("src");
                if (!imgUrl.startsWith("http")) {
                    imgUrl = "https:" + imgUrl;
                }
                String title = "";
                String author = "";
                String year = "";
                String region = "";
                String status = "";

                Elements titleElements = doc.select(".book-title h1");
                if (!titleElements.isEmpty()) {
                    title = titleElements.first().text();
                }

                Elements authorElements = doc.select("li:contains(漫畫作者) span:contains(漫畫作者) a");
                if (!authorElements.isEmpty()) {
                    author = authorElements.first().text();  // 吾峠呼世晴
                }


                Elements yearElements = doc.select("li:contains(出品年代) a");
                if (!yearElements.isEmpty()) {
                    year = yearElements.first().text();
                }

                Elements regionElements = doc.select("li:contains(漫畫地區) span:contains(漫畫地區) a");
                if (!regionElements.isEmpty()) {
                    region = regionElements.first().text();  // 日本漫畫
                }


                Element statusLi = doc.selectFirst("li.status");
                if (statusLi != null) {
                    Element dgreenSpan = statusLi.selectFirst("span.dgreen");
                    Element dredSpan = statusLi.selectFirst("span.red");
                    if (dgreenSpan != null) {
                        status = dgreenSpan.text();
                    }else{
                        status = dredSpan.text();
                    }
                }
                MangaDetail mangaDetail = new MangaDetail(imgUrl,title,author,year,region,status);
                Thread.sleep(500 + new Random().nextInt(500));
                Log.d("DetailRepo",author);
                listener.onSuccess(mangaDetail);

            } catch (IOException | InterruptedException e) {
                listener.onError("資料抓取失敗: " + e.getMessage());
            }
        }).start();
    }
}
