package com.example.test.repository;

import android.util.Log;

import com.example.test.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    public interface OnBooksLoadedListener {
        void onLoaded(List<Book> mangases);
        void onError(String message);
    }

    public void fetchBooksFromUpdatePage(OnBooksLoadedListener listener) {
        new Thread(() -> {
            try {
                // 直接連接更新頁面
                Document doc = Jsoup.connect("https://tw.manhuagui.com/update/")
                        .userAgent("Mozilla/5.0")
                        .get();

                List<Book> mangases = new ArrayList<>();

                // 抓取最近更新的書籍列表（每本書都在 .book-list > li 裡）
                Elements bookElements = doc.select(".latest-list ul li a.cover");

                for (Element el : bookElements) {
                    String title = el.attr("title");  // 書名
                    String img = el.select("img").attr("data-src");  // 圖片網址
                    String update = el.select(".tt").text();    // 更新資訊
                    String href =  el.attr("href"); // 書籍連結
                    String pageUrl = "https://tw.manhuagui.com" + href;

                    if (img == null || img.isEmpty()) {
                        img = el.select("img").attr("src");
                    }
                    if (!img.startsWith("http")) {
                        img = "https:" + img;
                    }
                    Log.d("BookRepository",title);
                    mangases.add(new Book(title + "（" + update + "）", img, pageUrl));
                }


                listener.onLoaded(mangases);
            } catch (IOException e) {
                e.printStackTrace();
                listener.onError("資料抓取失敗: " + e.getMessage());
            }
        }).start();
    }
}
