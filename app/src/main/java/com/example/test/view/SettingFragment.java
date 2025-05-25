package com.example.test.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test.R;
import com.example.test.model.SessionManager;
import com.example.test.model.Setting;
import com.example.test.viewmodel.SettingViewModel;

public class SettingFragment extends Fragment {

    private LinearLayout notLoginLayout, layoutProfileContent;
    private ScrollView userInfoLayout;
    private TextView tvProfileArrow, tvNickname, tvPhone, tvEmail, tvProvince, tvCity, tvArea, tvPoints;
    private Switch switchNightMode;

    private SettingViewModel viewModel;
    private Setting currentSetting;
    private boolean isProfileExpanded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        initViews(view);
        initViewModel();
        checkLoginStatus();

        return view;
    }

    private void initViews(View view) {
        notLoginLayout = view.findViewById(R.id.layout_not_login);
        userInfoLayout = view.findViewById(R.id.layout_user_info);
        layoutProfileContent = view.findViewById(R.id.layout_profile_content);
        tvProfileArrow = view.findViewById(R.id.tv_profile_arrow);

        tvNickname = view.findViewById(R.id.tv_nickname);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvEmail = view.findViewById(R.id.tv_email);
        tvProvince = view.findViewById(R.id.tv_province);
        tvCity = view.findViewById(R.id.tv_city);
        tvArea = view.findViewById(R.id.tv_area);
        tvPoints = view.findViewById(R.id.tv_points);

        switchNightMode = view.findViewById(R.id.switch_night_mode);


        view.findViewById(R.id.btn_login).setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), LoginActivity.class)));

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> logout());
        view.findViewById(R.id.layout_profile_header).setOnClickListener(v -> toggleProfile());
        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v -> showEditDialog());

        switchNightMode.setOnCheckedChangeListener((v, checked) -> {
            saveNightMode(checked);
            Toast.makeText(requireContext(), checked ? "已開啟夜間模式" : "已關閉夜間模式", Toast.LENGTH_SHORT).show();
        });

        loadNightMode();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        viewModel.getSettingData().observe(getViewLifecycleOwner(), this::displayUserInfo);
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        SessionManager.logout(requireContext());
        viewModel.clearData();
        checkLoginStatus();
        Toast.makeText(requireContext(), "已登出", Toast.LENGTH_SHORT).show();
    }

    //下拉個人資料攔
    private void toggleProfile() {
        isProfileExpanded = !isProfileExpanded;
        layoutProfileContent.setVisibility(isProfileExpanded ? View.VISIBLE : View.GONE);
        tvProfileArrow.setText(isProfileExpanded ? "▲" : "▼");

        if (isProfileExpanded && currentSetting == null) {
            String cookies = getPrefs().getString("cookie", "");
            if (!cookies.isEmpty()) viewModel.loadUserSettings(cookies);
        }
    }

    private void displayUserInfo(Setting setting) {
        if (setting != null) {
            currentSetting = setting;
            tvNickname.setText(setting.getNickname().isEmpty() ? "未設定" : setting.getNickname());
            tvPhone.setText(setting.getPhone().isEmpty() ? "未設定" : setting.getPhone());
            tvEmail.setText(setting.getEmail().isEmpty() ? "未設定" : setting.getEmail());
            tvProvince.setText(setting.getProvince().isEmpty() ? "未設定" : setting.getProvince());
            tvCity.setText(setting.getCity().isEmpty() ? "未設定" : setting.getCity());
            tvArea.setText(setting.getArea().isEmpty() ? "未設定" : setting.getArea());
            tvPoints.setText(setting.getPoints().isEmpty() ? "0" : setting.getPoints());
        }
    }

    private void showEditDialog() {

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(60, 40, 60, 40);

        EditText[] fields = {
                createEditText("暱稱", currentSetting.getNickname()),
                createEditText("手機", currentSetting.getPhone()),
                createEditText("郵箱", currentSetting.getEmail()),
                createEditText("省分", currentSetting.getProvince()),
                createEditText("城市", currentSetting.getCity()),
                createEditText("地區", currentSetting.getArea())
        };

        for (EditText field : fields) container.addView(field);

        new AlertDialog.Builder(requireContext())
                .setTitle("編輯個人資料")
                .setView(container)
                .setPositiveButton("儲存", (dialog, which) -> {
                    Setting newSetting = new Setting(
                            fields[0].getText().toString().trim(),
                            fields[2].getText().toString().trim(),
                            currentSetting.getPoints(),
                            fields[1].getText().toString().trim(),
                            fields[3].getText().toString().trim(),
                            fields[4].getText().toString().trim(),
                            fields[5].getText().toString().trim()
                    );
                    String cookies = getPrefs().getString("cookie", "");
                    if (!cookies.isEmpty()) viewModel.updateUserSettings(cookies, newSetting);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private EditText createEditText(String hint, String text) {
        EditText editText = new EditText(requireContext());
        editText.setHint(hint);
        editText.setText(text);
        return editText;
    }

    private void checkLoginStatus() {
        boolean isLogin = getPrefs().getBoolean("isLogin", false);
        notLoginLayout.setVisibility(isLogin ? View.GONE : View.VISIBLE);
        userInfoLayout.setVisibility(isLogin ? View.VISIBLE : View.GONE);
    }

    private void loadNightMode() {
        boolean isNightMode = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
                .getBoolean("night_mode", false);
        switchNightMode.setChecked(isNightMode);
    }

    private void saveNightMode(boolean isNightMode) {
        requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
                .edit().putBoolean("night_mode", isNightMode).apply();
    }

    private SharedPreferences getPrefs() {
        return requireActivity().getSharedPreferences("Myprefs", Context.MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoginStatus();
    }
}