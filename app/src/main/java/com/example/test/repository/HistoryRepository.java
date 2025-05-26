package com.example.test.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.test.model.MangaItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HistoryRepository {
    private static HistoryRepository instance;

    private final List<MangaItem> cache = new ArrayList<>();

    private HistoryRepository() {
    }

    public static synchronized HistoryRepository getInstance() {
        if (instance == null) {
            instance = new HistoryRepository();
        }
        return instance;
    }

    public interface OnLikeLoadedListener {
        void onSuccess(List<MangaItem> mangaItems);
        void onError(String error);
    }

    public void fetchAllMangaFromShelf(WebView webView, OnLikeLoadedListener listener) {
        if (!cache.isEmpty()) {
            listener.onSuccess(cache);
            return;
        }

        new Thread(() -> {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/90 Safari/537.36"); // 桌面版

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    // Step 3: 觸發 mouseover
                    String triggerJS = "var ev = new MouseEvent('mouseover', {" +
                            "bubbles: true, cancelable: true, view: window});" +
                            "document.querySelector('#history .handle').dispatchEvent(ev);";
                    view.evaluateJavascript(triggerJS, null);

                    // Step 4: 延遲一點再抓 innerHTML（確保資料已載入）
                    new Handler().postDelayed(() -> {
                        String getHTML = "document.documentElement.outerHTML;";
                        view.evaluateJavascript(getHTML, html -> {
                            // html 是 HTML 字串，現在可以用 Jsoup 分析
                            parseWithJsoup(html);
                        });
                    }, 2000); // 延遲 2 秒讓 mouseover 有時間載入資料
                }
            });

            webView.loadUrl("https://tw.manhuagui.com/");
        }).start();
    }
    public void clearCache() {
        cache.clear();
    }
}
