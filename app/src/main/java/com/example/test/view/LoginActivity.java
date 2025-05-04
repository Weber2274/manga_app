package com.example.test.view;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private int targetFragmentId;

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

        // 接收目標 fragment ID
        targetFragmentId = getIntent().getIntExtra("target", R.id.nav_home);

        btn_login.setOnClickListener(v -> {
            String userName = et_name.getText().toString();
            String passWord = et_pass.getText().toString();

            if (!userName.isEmpty() && !passWord.isEmpty()) {
                login(userName, passWord);
            } else {
                Toast.makeText(LoginActivity.this, "請填寫帳號和密碼", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String username, String password) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("https://tw.manhuagui.com/user/login") // 實際可能不接受 POST，請視需求更改
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "登入失敗，請檢查網路", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 儲存登入狀態
                    SharedPreferences prefs = getSharedPreferences("Myprefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLogin", true);
                    editor.apply();

                    // 回傳成功並附帶 target fragment ID
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("target", targetFragmentId);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
