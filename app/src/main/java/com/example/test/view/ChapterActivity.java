package com.example.test.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.test.R;
import com.example.test.adapter.ChapterPageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChapterActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ChapterPageAdapter adapter;
    private List<Integer> imageResIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        viewPager = findViewById(R.id.chapter_viewpager);
        String comicId = getIntent().getStringExtra("comicId");
        int chapter = getIntent().getIntExtra("chapter", 1);
        loadChapterImages(comicId, chapter);

        adapter = new ChapterPageAdapter(imageResIds);
        viewPager.setAdapter(adapter);

    }
    private void loadChapterImages(String comicId, int chapter) {
        int page = 1;
        while (true) {
            String imageName = comicId + "_" + chapter + "_" + page;
            @SuppressLint("DiscouragedApi")
            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            if (resId == 0) break; // 如果找不到圖片就停止
            imageResIds.add(resId);
            page++;
        }

        if (imageResIds.isEmpty()) {
            Toast.makeText(this, "找不到章節", Toast.LENGTH_SHORT).show();
        }
    }
}