package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.model.Book;
import com.example.test.adapter.BookAdapter;
import com.example.test.view.BookFragment;
import com.example.test.view.FilterFragment;
import com.example.test.view.HistoryFragment;
import com.example.test.view.HomeFragment;
import com.example.test.view.LikeFragment;
import com.example.test.view.LoginActivity;
import com.example.test.view.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, new androidx.core.view.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, @NonNull WindowInsetsCompat insets) {
                view.setPadding(0, 0, 0, 70);
                return insets;
            }
        });
        btnLogin = findViewById(R.id.login_btn);
        bottomNavigationView.setItemIconTintList(null);

        Fragment bookFragment = BookFragment.newInstance("", "");
        Fragment filterFragment = FilterFragment.newInstance("", "");
        Fragment historyFragment = HistoryFragment.newInstance("", "");
        Fragment likeFragment = LikeFragment.newInstance("", "");
        Fragment settingFragment = SettingFragment.newInstance("", "");
        Fragment homeFragment = HomeFragment.newInstance("","");

        setCurrentFragment(homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_home) {
                setCurrentFragment(homeFragment);
            } else if (item.getItemId() == R.id.nav_filter) {
                setCurrentFragment(filterFragment);
            }else if (item.getItemId() == R.id.nav_history) {
                setCurrentFragment(historyFragment);
            }else if (item.getItemId() == R.id.nav_like) {
                setCurrentFragment(likeFragment);
            } else {
                setCurrentFragment(settingFragment);
            }
            return true;
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .commit();
    }
}