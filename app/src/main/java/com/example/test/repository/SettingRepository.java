package com.example.test.repository;

import com.example.test.model.Setting;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class SettingRepository {

    public interface OnSettingLoadedListener {
        void onSuccess(Setting setting);
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


                String nickname = getValueOrEmpty(doc.selectFirst("input#txtNickName"));
                String phone = getValueOrEmpty(doc.selectFirst("input#txtMobile"));
                String email = "";
                String province = "";
                String city = "";
                String area = "";
                String points = "";
                Setting setting = new Setting(nickname, email, points, phone, province, city, area);
                listener.onSuccess(setting);

            } catch (IOException | NullPointerException e) {
                listener.onError("資料抓取失敗: " + e.getMessage());
            }
        }).start();
    }

    private String getValueOrEmpty(Element element) {
        return element != null ? element.attr("value") : "";
    }

}
