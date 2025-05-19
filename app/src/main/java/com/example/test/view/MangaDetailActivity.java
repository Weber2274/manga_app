package com.example.test.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.viewmodel.MangaDetailViewModel;

import java.util.ArrayList;
import java.util.List;

public class MangaDetailActivity extends AppCompatActivity {
    private ImageView imgCover;
    private TextView title,author,region,year,status;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private WebView webView;
    private NestedScrollView nestedScrollView;
    private List<String> pageTitles = new ArrayList<>();
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
        nestedScrollView = findViewById(R.id.detail_nestedscrollview);
        webView = findViewById(R.id.manga_chapter);
        imgCover = findViewById(R.id.detail_cover);
        title = findViewById(R.id.detail_title);
        author = findViewById(R.id.detail_author);
        region = findViewById(R.id.detail_region);
        year = findViewById(R.id.detail_year);
        status = findViewById(R.id.detail_status);
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
                pageTitles = mangaDetail.getPageTitle();
                webView.loadUrl(url);
            }

        });

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void setHeight(final float height) {
                runOnUiThread(() -> {
                    ViewGroup.LayoutParams params = webView.getLayoutParams();
                    params.height = (int)(height * getResources().getDisplayMetrics().density);
                    webView.setLayoutParams(params);
                });
            }
        }, "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);

                String js = "javascript:(function() {" +
                        "var meta = document.createElement('meta');" +
                        "meta.name = 'viewport';" +
                        "meta.content = 'width=device-width, initial-scale=0.6, maximum-scale=1.0, user-scalable=no';" +
                        "document.getElementsByTagName('head')[0].appendChild(meta);" +

                        "var commentDiv = document.querySelector('div#Comment');" +
                        "var chapterDiv = document.querySelector('div.chapter.cf.mt16');" +
                        "var wrapper = document.createElement('div');" +
                        "if (chapterDiv) wrapper.appendChild(chapterDiv.cloneNode(true));" +
                        "if (commentDiv) wrapper.appendChild(commentDiv.cloneNode(true));" +
                        "document.body.innerHTML = ''; " +  // 清空 body
                        "document.body.appendChild(wrapper); " +

                        // 統一設定 body 跟 html 的 margin, padding 和 overflow
                        "document.body.style.margin = '0';" +
                        "document.body.style.padding = '10px';" +  // 你需要的 padding 可以調整
                        "document.body.style.background = '#fff';" +
                        "document.body.style.height = 'auto';" +
                        "document.documentElement.style.margin = '0';" +
                        "document.documentElement.style.padding = '0';" +
                        "document.documentElement.style.height = 'auto';" +
                        "document.body.style.overflow = 'auto';" +

                        "const navItems = document.querySelectorAll('#chapter-page-1 ul li');" +
                        "const chapterContainer = document.querySelector('#chapter-list-1');" +
                        "if (navItems && chapterContainer) {" +
                        "   const ulList = chapterContainer.querySelectorAll('ul');" +
                        "   ulList.forEach((ul, i) => { ul.style.display = (i === 0) ? 'block' : 'none'; });" +
                        "   navItems.forEach((li, idx) => li.classList.toggle('on', idx === 0));" +
                        "   navItems.forEach((item, index) => {" +
                        "     item.addEventListener('click', () => {" +
                        "       ulList.forEach((ul, i) => { ul.style.display = (i === index) ? 'block' : 'none'; });" +
                        "       navItems.forEach(li => li.classList.remove('on'));" +
                        "       item.classList.add('on');" +
                        "       if (typeof Android !== 'undefined' && Android.setHeight) {" +
                        "         Android.setHeight(document.body.scrollHeight);" +
                        "       }" +
                        "     });" +
                        "   });" +
                        "}" +

                        // 移除 footer
                        "var footer = document.querySelector('footer');" +
                        "if (footer) footer.remove();" +

                        // 圖片載入時更新高度
                        "function updateHeight() {" +
                        "  if (typeof Android !== 'undefined' && Android.setHeight) {" +
                        "    Android.setHeight(document.body.scrollHeight);" +
                        "  }" +
                        "}" +

                        "var imgs = document.images;" +
                        "for (var i = 0; i < imgs.length; i++) {" +
                        "  if (!imgs[i].complete) {" +
                        "    imgs[i].addEventListener('load', updateHeight);" +
                        "    imgs[i].addEventListener('error', updateHeight);" +
                        "  }" +
                        "}" +

                        // 頁面載入完成後也執行一次高度更新
                        "setTimeout(updateHeight, 300);" +

                        "var style = document.createElement('style');" +
                        "style.innerHTML = `" +
                        ".chapter-list ul {" +
                        "  display: flex;" +
                        "  flex-wrap: wrap;" +
                        "  padding: 0;" +
                        "}" +
                        ".chapter-list ul li {" +
                        "  width: 30%;" +
                        "  margin: 1%;" +
                        "  box-sizing: border-box;" +
                        "}" +
                        ".chapter-list ul li a {" +
                        "  display: block;" +
                        "  padding: 10px;" +
                        "  text-align: center;" +
                        "}" +
                        ".on a {" +
                        "  font-weight: bold;" +
                        "}" +
                        "`;" +
                        "document.head.appendChild(style);" +

                        "})();";

                webView.loadUrl(js);
            }

        });


        toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        

    }
}