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
import com.example.test.adapter.LikeAdapter;
import com.example.test.model.Book;
import com.example.test.viewmodel.LikeViewModel;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikeFragment extends Fragment {
    private LikeViewModel likeViewModel;
    private LikeAdapter likeAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout notLoginLayout;
    private Button loginButton;
    private TextView tv_like;
    private RecyclerView recyclerView_like;
    private final MutableLiveData<List<Book>> mangaItems = new MutableLiveData<>();

    public LiveData<List<Book>> getMangaItems() {
        return mangaItems;
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public LikeFragment() {
    }

    public static LikeFragment newInstance(String param1, String param2) {
        LikeFragment fragment = new LikeFragment();
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
        View view = inflater.inflate(R.layout.fragment_like, container, false);
        notLoginLayout = view.findViewById(R.id.layout_not_login);
        loginButton = view.findViewById(R.id.btn_login);
        loginButton.setOnClickListener(v -> {
            Log.d("LoginClick", "按下登入按鈕");
            startActivity(new Intent(requireActivity(), LoginActivity.class));
        });

        likeViewModel = new ViewModelProvider(this).get(LikeViewModel.class);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        RecyclerView recyclerView = view.findViewById(R.id.history_recycleview);

        likeAdapter = new LikeAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(likeAdapter);

        likeViewModel.loadMangaData(requireContext());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            likeViewModel.refreshMangaData(requireContext());
        });

        likeViewModel.getMangaItems().observe(getViewLifecycleOwner(), mangaItems -> {
            swipeRefreshLayout.setRefreshing(false);
            if (mangaItems != null) {
                likeAdapter.setMangas(mangaItems);
            }
        });

        likeAdapter.setOnItemClickListener(manga -> {
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