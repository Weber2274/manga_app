package com.example.test.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test.R;
import com.example.test.model.Setting;
import com.example.test.viewmodel.SettingViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SettingFragment extends Fragment {

    private LinearLayout notLoginLayout, profileContent;
    private ScrollView userInfoLayout;
    private TextView tvArrow, tvNick, tvPhone, tvEmail, tvProvince, tvCity, tvArea, tvPoints;
    private Switch nightSwitch;

    private SettingViewModel vm;
    private Setting current;
    private boolean expanded = false;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initViews(view);
        initVM();
        checkLogin();
        return view;
    }

    private void initViews(View view) {
        notLoginLayout = view.findViewById(R.id.layout_not_login);
        userInfoLayout = view.findViewById(R.id.layout_user_info);
        profileContent = view.findViewById(R.id.layout_profile_content);
        tvArrow = view.findViewById(R.id.tv_profile_arrow);

        tvNick = view.findViewById(R.id.tv_nickname);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvEmail = view.findViewById(R.id.tv_email);
        tvProvince = view.findViewById(R.id.tv_province);
        tvCity = view.findViewById(R.id.tv_city);
        tvArea = view.findViewById(R.id.tv_area);
        tvPoints = view.findViewById(R.id.tv_points);
        nightSwitch = view.findViewById(R.id.switch_night_mode);

        view.findViewById(R.id.btn_login).setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), LoginActivity.class)));
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> logout());
        view.findViewById(R.id.layout_profile_header).setOnClickListener(v -> toggle());
        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v -> edit());

        nightSwitch.setOnCheckedChangeListener((v, checked) -> {
            saveNight(checked);
            Toast.makeText(requireContext(), checked ? "夜間模式開啟" : "夜間模式關閉", Toast.LENGTH_SHORT).show();
        });
        loadNight();
    }

    private void initVM() {
        vm = new ViewModelProvider(this).get(SettingViewModel.class);
        vm.getSettingData().observe(getViewLifecycleOwner(), this::display);
        vm.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        vm.clearData();
        current = null;
        checkLogin();
        Toast.makeText(requireContext(), "已登出", Toast.LENGTH_SHORT).show();
    }

    private void toggle() {
        expanded = !expanded;
        profileContent.setVisibility(expanded ? View.VISIBLE : View.GONE);
        tvArrow.setText(expanded ? "▲" : "▼");

        if (expanded && current == null && (user != null)) {
            checkData();
        }
    }

    private void checkData() {
        FirebaseFirestore.getInstance()
                .collection("users").document(user.getUid())
                .collection("user_data1").document("profile")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        parse(doc);
                    } else {
                        create();
                    }
                })
                .addOnFailureListener(e -> create());
    }

    private void create() {
        Map<String, Object> data = new HashMap<>();
        data.put("nickname", "新用戶");
        data.put("phone", "");
        data.put("email", "");
        data.put("province", "");
        data.put("city", "");
        data.put("area", "");
        data.put("points", "0");

        FirebaseFirestore.getInstance()
                .collection("users").document("user1")
                .collection("user_data1").document("profile")
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Setting s = new Setting("新用戶", "", "0", "", "", "", "");
                    display(s);
                });
    }

    private void parse(com.google.firebase.firestore.DocumentSnapshot doc) {
        String nick = doc.getString("nickname");
        String phone = doc.getString("phone");
        String email = doc.getString("email");
        String province = doc.getString("province");
        String city = doc.getString("city");
        String area = doc.getString("area");
        String points = doc.getString("points");

        nick = nick != null ? nick : "";
        phone = phone != null ? phone : "";
        email = email != null ? email : "";
        province = province != null ? province : "";
        city = city != null ? city : "";
        area = area != null ? area : "";
        points = points != null ? points : "0";

        Setting s = new Setting(nick, email, points, phone, province, city, area);
        display(s);
    }

    private void display(Setting s) {
        if (s != null) {
            current = s;
            tvNick.setText(s.getNickname().isEmpty() ? "未設定" : s.getNickname());
            tvPhone.setText(s.getPhone().isEmpty() ? "未設定" : s.getPhone());
            tvEmail.setText(s.getEmail().isEmpty() ? "未設定" : s.getEmail());
            tvProvince.setText(s.getProvince().isEmpty() ? "未設定" : s.getProvince());
            tvCity.setText(s.getCity().isEmpty() ? "未設定" : s.getCity());
            tvArea.setText(s.getArea().isEmpty() ? "未設定" : s.getArea());
            tvPoints.setText(s.getPoints().isEmpty() ? "0" : s.getPoints());
        }
    }

    private void edit() {
        if (current == null) return;

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(60, 40, 60, 40);

        EditText[] fields = {
                createField("暱稱", current.getNickname()),
                createField("手機", current.getPhone()),
                createField("郵箱", current.getEmail()),
                createField("省份", current.getProvince()),
                createField("城市", current.getCity()),
                createField("地區", current.getArea())
        };

        for (EditText field : fields) container.addView(field);

        new AlertDialog.Builder(requireContext())
                .setTitle("編輯個人資料")
                .setView(container)
                .setPositiveButton("儲存", (dialog, which) -> {
                    Setting newS = new Setting(
                            fields[0].getText().toString().trim(),
                            fields[2].getText().toString().trim(),
                            current.getPoints(),
                            fields[1].getText().toString().trim(),
                            fields[3].getText().toString().trim(),
                            fields[4].getText().toString().trim(),
                            fields[5].getText().toString().trim()
                    );
                    update(newS);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void update(Setting s) {
        Map<String, Object> data = new HashMap<>();
        data.put("nickname", s.getNickname());
        data.put("phone", s.getPhone());
        data.put("email", s.getEmail());
        data.put("province", s.getProvince());
        data.put("city", s.getCity());
        data.put("area", s.getArea());
        data.put("points", s.getPoints());

        FirebaseFirestore.getInstance()
                .collection("users").document(user.getUid())
                .collection("user_data1").document("profile")
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    display(s);
                    Toast.makeText(requireContext(), "更新成功", Toast.LENGTH_SHORT).show();
                });
    }

    private EditText createField(String hint, String text) {
        EditText et = new EditText(requireContext());
        et.setHint(hint);
        et.setText(text != null ? text : "");
        return et;
    }

    private void checkLogin() {
        boolean login = (user != null);
        notLoginLayout.setVisibility(login ? View.GONE : View.VISIBLE);
        userInfoLayout.setVisibility(login ? View.VISIBLE : View.GONE);
    }

    private void loadNight() {
        boolean night = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
                .getBoolean("night_mode", false);
        nightSwitch.setChecked(night);
    }

    private void saveNight(boolean night) {
        requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
                .edit().putBoolean("night_mode", night).apply();
    }

    private SharedPreferences getPrefs() {
        return requireActivity().getSharedPreferences("Myprefs", Context.MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLogin();
    }
}