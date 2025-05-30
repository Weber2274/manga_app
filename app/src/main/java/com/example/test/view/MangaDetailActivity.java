package com.example.test.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.viewmodel.MangaDetailViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MangaDetailActivity extends AppCompatActivity {
    private ImageView imgCover;
    private TextView title,author, area,year,status;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private Button like;
    @SuppressLint({"SetTextI18n", "MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manga_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBar = findViewById(R.id.detail_progressBar);
        imgCover = findViewById(R.id.detail_cover);
        title = findViewById(R.id.detail_title);
        author = findViewById(R.id.detail_author);
        area = findViewById(R.id.detail_area);
        year = findViewById(R.id.detail_year);
        status = findViewById(R.id.detail_status);
        String title = getIntent().getStringExtra("title");
        MangaDetailViewModel viewModel = new ViewModelProvider(this).get(MangaDetailViewModel.class);
        viewModel.loadMangaDetail(title);
        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getMangaDetailLiveData().observe(this, mangaDetail -> {
            if(mangaDetail != null){
                Glide.with(this).load(mangaDetail.getCoverImg()).into(imgCover);
                this.title.setText(mangaDetail.getTitle());
                author.setText("作者: " + mangaDetail.getAuthor());
                area.setText("地區: " + mangaDetail.getArea());
                year.setText("出品年代: " + mangaDetail.getYear());
                status.setText("作品狀態: " + mangaDetail.getStatus());
            }

        });

        toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        SharedPreferences prefs = getSharedPreferences("Myprefs", MODE_PRIVATE);
        boolean isLogin = prefs.getBoolean("isLogin", false);
        Map<String, Object> data = new HashMap<>();
        data.put("title", this.title);
        data.put("cover", imgCover);
        String mangaId = "favorite";
        int no = 1;
        like.setOnClickListener(view -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //if(isLogin){
                DocumentReference favDocRef = db.collection("users")
                                                .document("user1")
                                                .collection("favorites")
                                                .document(mangaId + no);
                //no++

                favDocRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // ✅ 已收藏 ➜ 執行取消收藏
                        like.setText("收藏");
                        favDocRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Favorite", "取消收藏成功");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Favorite", "取消收藏失敗", e);
                                });
                    } else {
                        // ❌ 尚未收藏 ➜ 執行加入收藏
                        like.setText("已收藏");
                        favDocRef.set(data)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Favorite", "收藏成功");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Favorite", "收藏失敗", e);
                                });
                    }
                });
            //}
//            else{
//                Intent intent = new Intent(MangaDetailActivity.this,LoginActivity.class);
//                Toast.makeText(MangaDetailActivity.this, "登入後才能使用", Toast.LENGTH_LONG).show();
//                startActivity(intent);
//            }
        });
    }
}