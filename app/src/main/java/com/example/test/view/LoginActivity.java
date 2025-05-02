package com.example.test.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText et_name;
    private EditText et_pass;
    private Button btn_login;
    public String userName;
    public String passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_name = findViewById(R.id.et_username);
        et_pass = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btnL_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = et_name.getText().toString();
                passWord = et_pass.getText().toString();

                if (!userName.isEmpty() && !passWord.isEmpty()) {
                    // 發送登入請求
                    login(userName, passWord);
                } else {
                    Toast.makeText(LoginActivity.this, "請填寫帳號和密碼", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login(final String username, final String password) {
        OkHttpClient client = new OkHttpClient();

        // 使用 FormBody.Builder 設定 POST 請求的參數
        RequestBody body = new FormBody.Builder()
                .add("username", username)  // 傳送用戶名
                .add("password", password)  // 傳送密碼
                .build();

        // 創建 Request 物件，並設置請求的 URL 和方法
        Request request = new Request.Builder()
                .url("https://tw.manhuagui.com/user/login")  // 替換為登入的 URL
                .post(body)  // POST 請求
                .build();

        // 發送請求並處理回應
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // 請求失敗時的處理
                e.printStackTrace();
                // 顯示錯誤提示或其他處理邏輯
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body() != null ? response.body().string() : "無回應內容";
                    Log.d("login", "登入成功: ");
                } else {
                    Log.d("login", "登入失敗: ");
                    System.out.println("登入失敗，狀態碼: " + response.code());
                    String errorBody = response.body() != null ? response.body().string() : "無錯誤訊息";
                    System.out.println("錯誤訊息: " + errorBody);
                }
            }
        });
    }
}