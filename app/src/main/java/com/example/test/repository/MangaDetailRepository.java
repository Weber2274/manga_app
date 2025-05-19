package com.example.test.repository;

import android.util.Log;

import com.example.test.model.MangaDetail;

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
                    Log.d("ChapterPage", "Title: " + title);
                }

                Element authorElements = doc.selectFirst("li:contains(漫畫作者) span:contains(漫畫作者)");
//                Elements authorElements = doc.select("span:contains(漫畫作者) a");
                if (authorElements != null) {
//                    author = authorElements.first().text();
                    Element authorLink = authorElements.selectFirst("a");
                    author = authorLink.text();
                    Log.d("ChapterPage", "Author: " + author);
                }


                Element yearElements = doc.selectFirst("li:contains(出品年代)");
//                Elements yearElements = doc.select("span:contains(出品年代) a");
                if (yearElements != null) {
//                    year = yearElements.first().text();
                    Element yearLink = yearElements.selectFirst("a");
                    year = yearLink.text();
                    Log.d("ChapterPage", "Year: " + year);
                }

                Element regionElements = doc.selectFirst("li:contains(漫畫地區) span:contains(漫畫地區)");
//                Elements regionElements = doc.select("span:contains(漫畫地區) a");
                if (regionElements != null) {
//                    region = regionElements.first().text();  // 日本漫畫
                    Element regionLink = regionElements.selectFirst("a");
                    region = regionLink.text();
                    Log.d("ChapterPage", "Region: " + region);
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
                    Log.d("ChapterPage", "Status: " + status);
                }
                List<String> pageTitleList = new ArrayList<>();
//                Elements pageTitles = doc.select("div.chapter-page a[title]");
//                for (Element a : pageTitles) {
//                    String pageRange = a.attr("title");
//                    pageTitleList.add(pageRange);
//                    Log.d("ChapterPage", "Title: " + pageRange);
//                }
                MangaDetail mangaDetail = new MangaDetail(imgUrl,title,author,year,region,status,pageTitleList);
//                Thread.sleep(200 + new Random().nextInt(500));
                Log.d("DetailRepo",author);
                listener.onSuccess(mangaDetail);

            } catch (IOException e) {
                listener.onError("資料抓取失敗: " + e.getMessage());
            }
        }).start();
    }
}
