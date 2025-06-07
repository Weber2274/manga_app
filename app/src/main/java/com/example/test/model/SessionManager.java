//package com.example.test.model;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.util.Log;
//import android.webkit.CookieManager;
//import android.webkit.ValueCallback;
//
//import com.example.test.MainActivity; // 替換成你實際的 MainActivity 所在路徑
//
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class SessionManager {
//
//    private static final String PREF_NAME = "Myprefs";
//    private static final String KEY_IS_LOGIN = "isLogin";
//    private static final String KEY_COOKIE = "cookie";
//
//    // 登出方法
//    public static void logout(Context context) {
//
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
//            @Override
//            public void onReceiveValue(Boolean value) {
//                Log.d("Logout", "WebView cookies removed: " + value);
//            }
//        });
//        cookieManager.flush();
//
//        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean(KEY_IS_LOGIN, false);
//        editor.remove(KEY_COOKIE);
//        editor.apply();
//
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("https://tw.manhuagui.com/user/center/exit")
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("Logout", "登出請求失敗：" + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("Logout", "登出請求成功，狀態碼：" + response.code());
//                response.close();
//            }
//        });
//
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        context.startActivity(intent);
//    }
//}
