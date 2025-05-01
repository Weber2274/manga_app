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
                Log.d("tags", String.valueOf(tags.size()));
                Elements coverLists = doc.select("#cmt-cont ul.cover-list");
                for (int i = 0; i < tags.size(); i++) {
                    String tag = tags.get(i).text();
                    Element ul = coverLists.get(i);
                    Elements mangaLis = ul.select("li");
                    Log.d("mangaLis", String.valueOf(mangaLis));

                    List<Book> bookList = new ArrayList<>();
                    if(i == 0){
                        for (Element element : mangaLis) {
                            String title = element.select("a").attr("title");
                            String imgUrl = element.select("img").attr("src");

                            if (!imgUrl.startsWith("http")) {
                                imgUrl = "https:" + imgUrl;
                            }
                            Log.d("imgUrl", imgUrl);
                            String href = element.select("a").attr("href");
                            String pageUrl = "https://tw.manhuagui.com" + href;

                            bookList.add(new Book(title, imgUrl, pageUrl));
                            Log.d("bookList", String.valueOf(bookList.size()));
                        }
                    }
                    else{
                        for (Element element : mangaLis) {
                            String title = element.select("a").attr("title");
                            String imgUrl = element.select("img").attr("data-src");

                            if (!imgUrl.startsWith("http")) {
                                imgUrl = "https:" + imgUrl;
                            }
                            Log.d("imgUrl", imgUrl);
                            String href = element.select("a").attr("href");
                            String pageUrl = "https://tw.manhuagui.com" + href;

                            bookList.add(new Book(title, imgUrl, pageUrl));
                            Log.d("bookList", String.valueOf(bookList.size()));
                        }
                    }

                    mangaGroupList.add(new MangaGroup(tag, bookList));
                }
                Log.d("MangaGroup", String.valueOf(mangaGroupList.size()));
                listener.onSuccess(mangaGroupList);


            } catch (IOException e) {
                e.printStackTrace();
                listener.onError("資料抓取失敗: " + e.getMessage());
            }
        }).start();
    }
}
