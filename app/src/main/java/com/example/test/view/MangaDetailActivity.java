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
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.adapter.ChapterAdapter;
import com.example.test.viewmodel.MangaDetailViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MangaDetailActivity extends AppCompatActivity {
    private ImageView imgCover;
    private TextView mangaTitle,author,region,year,status;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private Button like, read;
    private RecyclerView recyclerView;
    private String title;
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
        title = getIntent().getStringExtra("title");
        like = findViewById(R.id.btn_like);
        read = findViewById(R.id.btn_read);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DocumentReference favDocRef = FirebaseFirestore.getInstance()
                    .collection("users").document(user.getUid())
                    .collection("favorites").document(title);

            favDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    like.setText("取消收藏");
                } else {
                    like.setText("收藏");
                }
            });
        }

        like.setOnClickListener(view -> {
            if (user != null) {
                SharedPreferences prefs = getSharedPreferences("Myprefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                DocumentReference favDocRef = db.collection("users")
                        .document(user.getUid())
                        .collection("favorites")
                        .document(title);

                favDocRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        favDocRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    like.setText("收藏");
                                    editor.putBoolean("isFavorited_" + title, false);
                                    editor.apply();
                                    Log.d("Favorite", "取消收藏成功");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Favorite", "取消收藏失敗", e);
                                });
                    } else {
                        db.collection("comics")
                                .whereEqualTo("title", title)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        DocumentReference comicRef = querySnapshot.getDocuments()
                                                .get(0).getReference();

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("comic", comicRef);

                                        favDocRef.set(data)
                                                .addOnSuccessListener(aVoid -> {
                                                    like.setText("取消收藏");
                                                    editor.putBoolean("isFavorited_" + title, true);
                                                    editor.apply();
                                                    Log.d("Favorite", "收藏成功");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Favorite", "收藏失敗", e);
                                                });
                                    } else {
                                        Toast.makeText(this, "找不到該漫畫", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            } else {
                Toast.makeText(MangaDetailActivity.this, "請先登入才能使用收藏功能", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MangaDetailActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        String[] comicId = new String[1];
        read.setOnClickListener(view -> {
            if(user != null){
                DocumentReference historyDoc = db.collection("users")
                        .document(user.getUid())
                        .collection("history")
                        .document(title);
                historyDoc.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        historyDoc.update("latest", "1")
                                .addOnSuccessListener(aVoid -> Log.d("History", "最新章節已更新"))
                                .addOnFailureListener(e -> Log.e("History", "更新 latest 失敗", e));
                    } else {
                        db.collection("comics")
                                .whereEqualTo("title", title)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        DocumentReference comicRef = querySnapshot.getDocuments()
                                                .get(0).getReference();

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("comic", comicRef);
                                        data.put("latest", "1");

                                        historyDoc.set(data)
                                                .addOnSuccessListener(aVoid -> Log.d("History", "歷史紀錄已新增"))
                                                .addOnFailureListener(e -> Log.e("History", "新增紀錄失敗", e));
                                    } else {
                                        Toast.makeText(this, "找不到該漫畫", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
            Intent intent = new Intent(MangaDetailActivity.this, ChapterActivity.class);
            intent.putExtra("comicId", comicId[0]);
            intent.putExtra("chapter", 1);
            startActivity(intent);
        });

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
                comicId[0] = mangaDetail.getId();
                Glide.with(this).load(mangaDetail.getCoverImg()).into(imgCover);
                mangaTitle.setText(mangaDetail.getTitle());
                author.setText("作者: " + mangaDetail.getAuthor());
                region.setText("地區: " + mangaDetail.getArea());
                year.setText("出品年代: " + mangaDetail.getYear());
                status.setText("作品狀態: " + mangaDetail.getStatus());
            }

        });

        ChapterAdapter adapter = new ChapterAdapter();
        adapter.setOnItemClickListener(chapterNumber -> {
            if(user != null){
                DocumentReference historyDoc = db.collection("users")
                        .document(user.getUid())
                        .collection("history")
                        .document(title);
                historyDoc.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        historyDoc.update("latest", Integer.toString(chapterNumber))
                                .addOnSuccessListener(aVoid -> Log.d("History", "最新章節已更新"))
                                .addOnFailureListener(e -> Log.e("History", "更新 latest 失敗", e));
                    } else {
                        db.collection("comics")
                                .whereEqualTo("title", title)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        DocumentReference comicRef = querySnapshot.getDocuments()
                                                .get(0).getReference();

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("comic", comicRef);
                                        data.put("latest", Integer.toString(chapterNumber));

                                        historyDoc.set(data)
                                                .addOnSuccessListener(aVoid -> Log.d("History", "歷史紀錄已新增"))
                                                .addOnFailureListener(e -> Log.e("History", "新增紀錄失敗", e));
                                    } else {
                                        Toast.makeText(this, "找不到該漫畫", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }


            Intent intent = new Intent(MangaDetailActivity.this, ChapterActivity.class);
            intent.putExtra("comicId", comicId[0]);
            intent.putExtra("chapter", chapterNumber);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference favDocRef = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("favorites").document(title);

            favDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    like.setText("取消收藏");
                } else {
                    like.setText("收藏");
                }
            });
        }
    }
}