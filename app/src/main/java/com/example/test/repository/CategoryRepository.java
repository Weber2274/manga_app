package com.example.test.repository;

import android.util.Log;

import com.example.test.model.Book;
import com.example.test.model.MangaGroup;
import com.example.test.model.MangaItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CategoryRepository {
    public interface OnCategoryLoadedListener{
        void onSuccess(List<MangaItem> mangaItems);
        void onError(String error);
    }
    public void fetchMangaFromCategoryPage(String url, OnCategoryLoadedListener listener){
        new Thread(() -> {
           try {
               Document document = Jsoup.connect(url)
                       .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36")
                       .referrer("https://www.google.com/")
                       .header("Accept-Language", "zh-TW,zh;q=0.9")
                       .header("Connection", "keep-alive")
                       .timeout(15000)
                       .get();

               Random random = new Random();
               List<MangaItem> mangaItemList = new ArrayList<>();
               Elements mangas = document.select("div.book-list ul#contList > li");
               for (Element element : mangas) {
                   String title = element.select("a.bcover").attr("title");
                   String imgUrl = element.select("a.bcover img").attr("data-src");

                   if (imgUrl == null || imgUrl.isEmpty()) {
                       imgUrl = element.select("a.bcover img").attr("src");
                   }
                   if (!imgUrl.startsWith("http")) {
                       imgUrl = "https:" + imgUrl;
                   }
                   //Log.d("imgUrl", imgUrl);
                   String href = element.select("a.bcover").attr("href");
                   String pageUrl = "https://tw.manhuagui.com" + href;

                   mangaItemList.add(new MangaItem(title, imgUrl, pageUrl));
                   Log.d("CategoryRepository", imgUrl);
                   int delay = 1000 + random.nextInt(1000);
                   Thread.sleep(delay);
               }
               listener.onSuccess(mangaItemList);
           } catch (IOException e) {
               e.printStackTrace();
               listener.onError("資料抓取失敗: " + e.getMessage());
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
        }).start();
    }
}
