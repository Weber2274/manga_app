//package com.example.test.repository;
//
//import android.util.Log;
//
//import com.example.test.model.LikeItem;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//public class LikeRepository {
//    private static LikeRepository instance;
//    private final Map<String, List<LikeItem>> cache = new HashMap<String, List<LikeItem>>();
//    private String cookies = "";
//
//    private LikeRepository() {
//    }
//
//    public static synchronized LikeRepository getInstance() {
//        if (instance == null) {
//            instance = new LikeRepository();
//        }
//        return instance;
//    }
//
//    public void setCookies(String cookies) {
//        this.cookies = cookies;
//    }
//
//    public interface OnLikeLoadedListener {
//        void onSuccess(List<LikeItem> mangaItems);
//        void onError(String error);
//    }
//
//    public void fetchMangaFromLikePage(String url, OnLikeLoadedListener listener) {
//        if (cache.containsKey(url)) {
//            listener.onSuccess(cache.get(url));
//            return;
//        }
//
//        new Thread(() -> {
//            try {
//                Document document = Jsoup.connect(url)
//                        .userAgent("Mozilla/5.0")
//                        .referrer("https://www.google.com/")
//                        .header("Accept-Language", "zh-TW,zh;q=0.9")
//                        .header("Connection", "keep-alive")
//                        .header("Cookie", cookies)
//                        .timeout(15000)
//                        .get();
//
//                List<LikeItem> mangaItemList = new ArrayList<>();
//                Elements mangas = document.select("div.book-list ul#contList > li");
//
//                for (Element element : mangas) {
//                    Element img = element.selectFirst("div.dy_img img");
//                    if (img != null) {
//                        String imgUrl = img.attr("src");
//                        String title = img.attr("alt");
//                        Log.d("like_title", title);
//
//                        if (imgUrl.startsWith("//")) {
//                            imgUrl = "https:" + imgUrl;
//                        }
//                        mangaItemList.add(new LikeItem(title, imgUrl));
//                    }
//                    Thread.sleep(500 + new Random().nextInt(500));
//                }
//
//                cache.put(url, mangaItemList);
//                listener.onSuccess(mangaItemList);
//            } catch (IOException | InterruptedException e) {
//                listener.onError("資料抓取失敗: " + e.getMessage());
//            }
//        }).start();
//    }
//}
