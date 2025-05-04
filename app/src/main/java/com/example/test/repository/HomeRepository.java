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
import java.util.Random;

public class HomeRepository {
    private static HomeRepository instance;
    private HomeRepository() {}  // 私有建構子
    public static HomeRepository getInstance() {
        if (instance == null) {
            instance = new HomeRepository();
        }
        return instance;
    }
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5分鐘
    private long lastFetchTime = 0;
    private List<MangaGroup> cachedMangaGroups = null;
    public interface OnHomePageLoadedListener {
        void onSuccess(List<MangaGroup> categories);
        void onError(String error);
    }
    public void fetchMangaFromHomePage(OnHomePageLoadedListener listener) {
        long now = System.currentTimeMillis();
        if (cachedMangaGroups != null && now - lastFetchTime < CACHE_DURATION) {
            listener.onSuccess(cachedMangaGroups);
            return;
        }
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://tw.manhuagui.com/")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36")
                        .referrer("https://www.google.com/")
                        .header("Accept-Language", "zh-TW,zh;q=0.9")
                        .header("Connection", "keep-alive")
                        .timeout(15000)
                        .get();
                Random random = new Random();
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
                        int delay = 1000 + random.nextInt(2000);
                        Thread.sleep(delay);
                    }

                    mangaGroupList.add(new MangaGroup(tag, bookList));
                }
                Log.d("MangaGroup", String.valueOf(mangaGroupList.size()));
                lastFetchTime = now;
                cachedMangaGroups = mangaGroupList;
                listener.onSuccess(cachedMangaGroups);


            } catch (IOException e) {
                e.printStackTrace();
                listener.onError("資料抓取失敗: " + e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
