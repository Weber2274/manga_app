package com.example.test.repository;

import com.example.test.model.Setting;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public void loadData(OnSettingLoadedListener listener) {
        FirebaseFirestore.getInstance()
                .collection("users").document("user1")
                .collection("user_data1").document("profile")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        parse(doc, listener);
                    } else {
                        create(listener);
                    }
                })
                .addOnFailureListener(e -> listener.onError("讀取失敗"));
    }

    private void create(OnSettingLoadedListener listener) {
        Map<String, Object> data = new HashMap<>();
        data.put("nickname", "新用戶");
        data.put("phone", "");
        data.put("email", "");
        data.put("province", "");
        data.put("city", "");
        data.put("area", "");
        data.put("points", "0");

        FirebaseFirestore.getInstance()
                .collection("users").document("user1")
                .collection("user_data1").document("profile")
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Setting s = new Setting("新用戶", "", "0", "", "", "", "");
                    listener.onSuccess(s);
                })
                .addOnFailureListener(e -> listener.onError("創建失敗"));
    }

    private void parse(com.google.firebase.firestore.DocumentSnapshot doc, OnSettingLoadedListener listener) {
        String nick = doc.getString("nickname");
        String phone = doc.getString("phone");
        String email = doc.getString("email");
        String province = doc.getString("province");
        String city = doc.getString("city");
        String area = doc.getString("area");
        String points = doc.getString("points");

        nick = nick != null ? nick : "";
        phone = phone != null ? phone : "";
        email = email != null ? email : "";
        province = province != null ? province : "";
        city = city != null ? city : "";
        area = area != null ? area : "";
        points = points != null ? points : "0";

        Setting s = new Setting(nick, email, points, phone, province, city, area);
        listener.onSuccess(s);
    }

    public void updateData(Setting s, OnSettingUpdatedListener listener) {
        Map<String, Object> data = new HashMap<>();
        data.put("nickname", s.getNickname());
        data.put("phone", s.getPhone());
        data.put("email", s.getEmail());
        data.put("province", s.getProvince());
        data.put("city", s.getCity());
        data.put("area", s.getArea());
        data.put("points", s.getPoints());

        FirebaseFirestore.getInstance()
                .collection("users").document("user1")
                .collection("user_data1").document("profile")
                .set(data)
                .addOnSuccessListener(aVoid -> listener.onSuccess("更新成功"))
                .addOnFailureListener(e -> listener.onError("更新失敗"));
    }
}