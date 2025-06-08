package com.example.test.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.test.*;
import com.example.test.adapter.HistoryAdapter;
import com.example.test.model.Book;
import com.example.test.viewmodel.HistoryViewModel;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    private HistoryViewModel historyViewModel;
    private HistoryAdapter historyAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout notLoginLayout;
    private Button loginButton;
    private final MutableLiveData<List<Book>> mangaItems = new MutableLiveData<>();

    public LiveData<List<Book>> getMangaItems() {
        return mangaItems;
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private BottomNavigationView bottomNavigationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        notLoginLayout = view.findViewById(R.id.layout_not_login);
        loginButton = view.findViewById(R.id.btn_login);
        loginButton.setOnClickListener(v -> {
            Log.d("LoginClick", "按下登入按鈕");
            startActivity(new Intent(requireActivity(), LoginActivity.class));
        });

        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        RecyclerView recyclerView = view.findViewById(R.id.history_recycleview);

        historyAdapter = new HistoryAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(historyAdapter);

        historyViewModel.loadMangaData(requireContext());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            historyViewModel.refreshMangaData(requireContext());
        });

        historyViewModel.getMangaItems().observe(getViewLifecycleOwner(), mangaItems -> {
            swipeRefreshLayout.setRefreshing(false);
            if (mangaItems != null) {
                historyAdapter.setMangas(mangaItems);
            }
        });

        historyAdapter.setOnItemClickListener(manga -> {
            Intent intent = new Intent(requireActivity(), MangaDetailActivity.class);
            String title = manga.getTitle();
            intent.putExtra("title",title);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseAuth.getInstance().getCurrentUser();
        boolean login = (user != null);
        notLoginLayout.setVisibility(login ? View.GONE : View.VISIBLE);
        swipeRefreshLayout.setVisibility(login ? View.VISIBLE : View.GONE);

        Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_main);
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