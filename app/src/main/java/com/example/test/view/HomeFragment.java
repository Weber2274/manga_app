package com.example.test.view;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.adapter.CategoryAdapter;
import com.example.test.adapter.HomeAdapter;
import com.example.test.model.ItemList;
import com.example.test.model.MangaItem;
//import com.example.test.viewmodel.HomeViewModel;
import com.example.test.viewmodel.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final Fragment historyFragment = HistoryFragment.newInstance("", "");
    private final Fragment likeFragment = LikeFragment.newInstance("", "");

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private Button btnLogin, btnRandom;
    HomeViewModel viewModel;
    private ProgressBar loading;
    private ImageView search;
    private EditText searchText;
    private BottomNavigationView bottomNavigationView;
    private List<MangaItem> mangaItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnLogin = view.findViewById(R.id.login_btn);
        btnRandom = view.findViewById(R.id.random_btn);
        loading = view.findViewById(R.id.progressBar);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        search = view.findViewById(R.id.search_icon);
        searchText = view.findViewById(R.id.et_search);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                boolean isLogin = (currentUser != null);

                if (!isLogin) {
                    updateLoginButton();
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    FirebaseAuth.getInstance().signOut();
                    updateLoginButton();

                }
            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mangaItems != null && !mangaItems.isEmpty()) {
                    int randomIndex = new Random().nextInt(mangaItems.size());
                    MangaItem randomItem = mangaItems.get(randomIndex);
                    String title = randomItem.getTitle();

                    Intent intent = new Intent(getContext(), MangaDetailActivity.class);
                    intent.putExtra("title", title);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "尚未載入漫畫資料", Toast.LENGTH_SHORT).show();
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = searchText.getText().toString().trim();
                if (keyword.isEmpty()) {
                    Toast.makeText(getContext(), "請輸入關鍵字", Toast.LENGTH_SHORT).show();
                    adapter.setItems(mangaItems);
                    return;
                } else {
                    List<MangaItem> filteredList = new ArrayList<>();
                    for (MangaItem item : mangaItems) {
                        if (item.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }

                    adapter.setItems(filteredList);
                    if (filteredList.isEmpty()) {
                        Toast.makeText(getContext(), "找不到相關漫畫", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        recyclerView = view.findViewById(R.id.home_recyclerView);
        adapter = new HomeAdapter();
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(adapter.getItemViewType(position) == HomeAdapter.TYPE_CATEGORY){
                    return 3;
                }else{
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(manga -> {
            Intent intent = new Intent(requireActivity(), MangaDetailActivity.class);
            String title = manga.getTitle();
            intent.putExtra("title",title);
            startActivity(intent);
        });
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.loadMangas();
        viewModel.getMangas().observe(getViewLifecycleOwner(), items -> {

            if (items != null) {
                Log.d("HomeFragment", "Items loaded: " + items.size());
                adapter.setItems(items);
                mangaItems = items;
            }
        });
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                loading.setVisibility(View.VISIBLE);
            } else {
                loading.setVisibility(View.GONE);
            }
        });
        return view;
    }

    public void resetMangaList() {
        if (adapter != null && mangaItems != null) {
            adapter.setItems(mangaItems);
        }
        if (searchText != null) {
            searchText.setText("");
        }
        searchText.clearFocus();

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginButton();

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

    private void updateLoginButton() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean isLogin = (currentUser != null);
        if (isLogin) {
            btnLogin.setText("登出");
        } else {
            btnLogin.setText("登入");
        }
    }

    private void setCurrentFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .addToBackStack(null)
                .commit();
    }
}