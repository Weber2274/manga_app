package com.example.test.repository;

import android.util.Log;

import com.example.test.model.Setting;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingRepository {

    public interface OnSettingLoadedListener {
        void onSuccess(Setting setting);

        void onError(String error);
    }

    public interface OnSettingUpdatedListener {
        void onSuccess(String message);

        void onError(String error);
    }


    public void fetchSetting(String url, String cookies, OnSettingLoadedListener listener) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .referrer("https://www.google.com/")
                        .header("Accept-Language", "zh-TW,zh;q=0.9")
                        .header("Connection", "keep-alive")
                        .header("Cookie", cookies)
                        .timeout(15000)
                        .get();

                Log.d("SettingRepository", "Cookie: " + cookies);
                Log.d("SettingRepository", "HTML content: " + doc.html().substring(0, 500));


                Elements inputElements = doc.select("input[type=text], input[type=email], input[type=tel]");
                Elements selectElements = doc.select("select");
                Elements displayElements = doc.select(".points-display, .score-display, .user-points");

                String nickname = "";
                String phone = "";
                String email = "";
                String province = "";
                String city = "";
                String area = "";
                String points = "";


                for (Element element : inputElements) {
                    String id = element.attr("id");
                    String value = element.attr("value");

                    if (id.contains("Nick") || id.contains("nick")) {
                        nickname = value;
                    } else if (id.contains("Mobile") || id.contains("mobile") || id.contains("phone")) {
                        phone = value;
                    } else if (id.contains("Email") || id.contains("email")) {
                        email = value;
                    } else if (id.contains("Area") || id.contains("area")) {
                        area = value;
                    }
                }


                for (Element element : selectElements) {
                    String id = element.attr("id");
                    String value = element.attr("value");

                    if (id.contains("Province") || id.contains("province")) {
                        province = value;
                    } else if (id.contains("City") || id.contains("city")) {
                        city = value;
                    }
                }

                for (Element element : displayElements) {
                    if (!element.text().isEmpty()) {
                        points = element.text();
                        break;
                    }
                }

                Setting setting = new Setting(nickname, email, points, phone, province, city, area);
                Log.d("SettingRepository", "抓取完成");
                listener.onSuccess(setting);

            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                listener.onError("資料抓取失敗: " + e.getMessage());
            }
        }).start();
    }
}