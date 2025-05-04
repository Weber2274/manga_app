package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.test.view.BookFragment;
import com.example.test.view.FilterFragment;
import com.example.test.view.HistoryFragment;
import com.example.test.view.HomeFragment;
import com.example.test.view.LikeFragment;
import com.example.test.view.LoginActivity;
import com.example.test.view.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> loginLauncher;
    private BottomNavigationView bottomNavigationView;

    private final Fragment homeFragment = HomeFragment.newInstance("", "");
    private final Fragment filterFragment = FilterFragment.newInstance("", "");
    private final Fragment historyFragment = HistoryFragment.newInstance("", "");
    private final Fragment likeFragment = LikeFragment.newInstance("", "");
    private final Fragment settingFragment = SettingFragment.newInstance("", "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        int targetFragment = result.getData().getIntExtra("target", R.id.nav_like);
                        handleNavigation(targetFragment);
                        bottomNavigationView.setSelectedItemId(targetFragment);
                    }
                }
        );

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (view, insets) -> {
            view.setPadding(0, 0, 0, 70);
            return insets;
        });

        setCurrentFragment(homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            return handleNavigation(itemId);
        });
    }

    private boolean handleNavigation(int itemId) {
        SharedPreferences prefs = getSharedPreferences("Myprefs", MODE_PRIVATE);
        boolean isLogin = prefs.getBoolean("isLogin", false);

        if (itemId == R.id.nav_home) {
            setCurrentFragment(homeFragment);
        } else if (itemId == R.id.nav_filter) {
            setCurrentFragment(filterFragment);
        } else if (itemId == R.id.nav_history) {
            if (!isLogin) {
                navToLogin(this, R.id.nav_history);
            } else {
                setCurrentFragment(historyFragment);
            }
        } else if (itemId == R.id.nav_like) {
            if (!isLogin) {
                navToLogin(this, R.id.nav_like);
            } else {
                setCurrentFragment(likeFragment);
            }
        } else if (itemId == R.id.nav_setting) {
            if (!isLogin) {
                navToLogin(this, R.id.nav_setting);
            } else {
                setCurrentFragment(settingFragment);
            }
        } else {
            return false;
        }
        return true;
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .commit();
    }

    private void navToLogin(Context context, int targetFragmentId) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("target", targetFragmentId); // 傳目標頁面給 LoginActivity
        loginLauncher.launch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (currentFragment instanceof HomeFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else if (currentFragment instanceof FilterFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_filter);
        } else if (currentFragment instanceof HistoryFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_history);
        } else if (currentFragment instanceof LikeFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_like);
        } else if (currentFragment instanceof SettingFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_setting);
        }
    }
}
