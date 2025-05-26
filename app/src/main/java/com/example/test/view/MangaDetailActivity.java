package com.example.test.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.viewmodel.MangaDetailViewModel;

public class MangaDetailActivity extends AppCompatActivity {
    private ImageView imgCover;
    private TextView title,author,region,year,status;
    private ProgressBar progressBar;
    Button read,like;
    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
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
        region = findViewById(R.id.detail_region);
        year = findViewById(R.id.detail_year);
        status = findViewById(R.id.detail_status);
        read = findViewById(R.id.btn_read);
        like = findViewById(R.id.btn_like);
        String url = getIntent().getStringExtra("pageUrl");
        MangaDetailViewModel viewModel = new ViewModelProvider(this).get(MangaDetailViewModel.class);
        viewModel.loadMangaDetail(url);
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
                title.setText(mangaDetail.getTitle());
                author.setText("作者: " + mangaDetail.getAuthor());
                region.setText("地區: " + mangaDetail.getArea());
                year.setText("出品年代: " + mangaDetail.getYear());
                status.setText("作品狀態: " + mangaDetail.getStatus());
            }
        });

        SharedPreferences prefs = getSharedPreferences("Myprefs", MODE_PRIVATE);
        boolean isLogin = prefs.getBoolean("isLogin", false);
        like.setOnClickListener(view -> {
            if(isLogin){
                like.setText("已收藏");
            }
            else{
                Intent intent = new Intent(MangaDetailActivity.this,LoginActivity.class);
                Toast.makeText(MangaDetailActivity.this, "登入後才能使用", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });
    }
}