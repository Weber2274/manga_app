package com.example.test.repository;

import android.util.Log;

import com.example.test.model.Book;
import com.example.test.model.MangaGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeRepository {
    public interface OnCategoriesLoadedListener {
        void onSuccess(List<MangaGroup> categories);
        void onError(String error);
    }
    public void fetchMangaFromHomePage(OnCategoriesLoadedListener listener) {
        new Thread(() -> {
            try {
                // 直接連接更新頁面
                Document doc = Jsoup.connect("https://tw.manhuagui.com/")
                        .userAgent("Mozilla/5.0")
                        .get();

                List<MangaGroup> mangaGroupList = new ArrayList<>();

                Elements tags = doc.select("#cmt-tab li");
                Elements mangas = doc.select("#cmt-cont ul.cover-list li");
                int mangaPerCategory = mangas.size() / tags.size();

                for(int i = 0; i < tags.size(); i++){
                    String tag = tags.get(i).text();
                    List<Book> bookList = new ArrayList<>();
                    int startIndex = i * mangaPerCategory;
                    int endIndex = (i + 1) * mangaPerCategory;
                    if (i == tags.size() - 1) {
                        endIndex = mangas.size();
                    }
                    for (int j = startIndex; j < endIndex; j++) {
                        Element element = mangas.get(j);

                        String title = element.select("a").attr("title");
                        String imgUrl = element.select("img").attr("src");

                        imgUrl = "https:" + imgUrl;

                        String href = element.select("a").attr("href");
                        String pageUrl = "https://tw.manhuagui.com" + href;
                        Log.d("HomeRepository",imgUrl);
                        // 建立 Book 物件並加入列表
                        if (title != null && !title.isEmpty()) {
                            bookList.add(new Book(title, imgUrl, pageUrl));
                        }
                    }
                    if (!bookList.isEmpty()) {
                        mangaGroupList.add(new MangaGroup(tag, bookList));
                    }
                }
                listener.onSuccess(mangaGroupList);


            } catch (IOException e) {
                e.printStackTrace();
                listener.onError("資料抓取失敗: " + e.getMessage());
            }
        }).start();
    }
}
