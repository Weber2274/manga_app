package com.example.test.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;

public class LoginActivity extends AppCompatActivity {

    private WebView webView;
    private int targetFragmentId;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        webView = findViewById(R.id.webview);
        targetFragmentId = getIntent().getIntExtra("target", R.id.nav_home);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.contains("/user/login/center")) {
                    // 等待跳轉後的頁面，如 /user/center 表示已登入成功
                    String cookie = CookieManager.getInstance().getCookie(url);
                    saveLoginStatus(cookie);
                }

                String js = "javascript:(function() {"
                        + "var head = document.getElementsByTagName('head')[0];"
                        + "var viewport = document.createElement('meta');"
                        + "viewport.name = 'viewport';"
                        + "viewport.content = 'width=device-width, initial-scale=1.0';"
                        + "head.appendChild(viewport);"

                        // 更安全的隱藏方式（只隱藏body的直接子元素）
                        + "var bodyChildren = document.body.children;"
                        + "for (var i = 0; i < bodyChildren.length; i++) {"
                        + "    var child = bodyChildren[i];"
                        + "    if (!child.contains(document.querySelector('.login-right'))) {"
                        + "        child.style.display = 'none';"
                        + "    }"
                        + "}"

                        // 移動登錄框到body的直接子級（確保定位基準）
                        + "var loginBox = document.querySelector('.login-right');"
                        + "if (loginBox) {"
                        + "    document.body.appendChild(loginBox);"
                        + "    loginBox.style.cssText = '"
                        + "display: block !important; "
                        + "position: fixed !important; "
                        + "top: 50% !important; "
                        + "left: 50% !important; "
                        + "transform: translate(-50%, -50%) !important; "
                        + "width: 90% !important; "
                        + "max-width: 400px !important; "
                        + "padding: 20px !important; "
                        + "background: white !important; "
                        + "z-index: 9999 !important;"
                        + "';"
                        + "    document.body.style.background = 'white';"
                        + "    document.body.style.overflow = 'hidden';"
                        + "}"
                        + "})();";

                view.evaluateJavascript(js, null);
            }
        });
        webView.loadUrl("https://tw.manhuagui.com/user/login");
    }

    private void saveLoginStatus(String cookie) {
        SharedPreferences prefs = getSharedPreferences("Myprefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLogin", true);
        editor.putString("cookie", cookie);
        editor.apply();
    }

}
