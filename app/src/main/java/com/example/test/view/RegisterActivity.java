package com.example.test.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.MainActivity;
import com.example.test.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText nicknameEditText, phoneEditText, areaEditText, cityEditText, provinceEditText, emailEditText, passwordEditText;
    private Button submitButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        areaEditText = findViewById(R.id.areaEditText);
        cityEditText = findViewById(R.id.cityEditText);
        provinceEditText = findViewById(R.id.provinceEditText);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> submitProfile());
    }

    private void submitProfile() {
        String email = emailEditText.getText().toString().trim();
        String pass = passwordEditText.getText().toString().trim();
        String nickname = nicknameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String area = areaEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String province = provinceEditText.getText().toString().trim();


        if (email.isEmpty() || pass.isEmpty() || nickname.isEmpty() || phone.isEmpty()
                || area.isEmpty() || city.isEmpty() || province.isEmpty()) {
            Toast.makeText(this, "資料尚未填寫完整", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser newUser = auth.getCurrentUser();
                        if (newUser == null) {
                            Toast.makeText(this, "註冊失敗：無法取得使用者資訊", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> profile = new HashMap<>();
                        profile.put("email", email);
                        profile.put("nickname", nickname);
                        profile.put("phone", phone);
                        profile.put("point", "0");
                        profile.put("area", area);
                        profile.put("city", city);
                        profile.put("province", province);

                        db.collection("users")
                                .document(newUser.getUid())
                                .collection("user_data1")
                                .document("profile")
                                .set(profile)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "註冊成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, MainActivity.class);
                                    intent.putExtra("target", R.id.nav_home);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "註冊失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(this, "註冊失敗：" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
