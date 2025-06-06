package com.example.test.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.adapter.ChapterAdapter;
import com.example.test.viewmodel.MangaDetailViewModel;

import java.util.ArrayList;
import java.util.List;

public class MangaDetailActivity extends AppCompatActivity {
    private ImageView imgCover;
    private TextView mangaTitle,author,region,year,status;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
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
        mangaTitle = findViewById(R.id.detail_title);
        author = findViewById(R.id.detail_author);
        region = findViewById(R.id.detail_region);
        year = findViewById(R.id.detail_year);
        status = findViewById(R.id.detail_status);
        recyclerView = findViewById(R.id.chapter_recyclerview);
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
                mangaTitle.setText(mangaDetail.getTitle());
                author.setText("作者: " + mangaDetail.getAuthor());
                region.setText("地區: " + mangaDetail.getArea());
                year.setText("出品年代: " + mangaDetail.getYear());
                status.setText("作品狀態: " + mangaDetail.getStatus());
            }

        });

        ChapterAdapter adapter = new ChapterAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
        toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        

    }
}