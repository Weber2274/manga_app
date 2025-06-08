package com.example.test.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.CategoryAdapter;
import com.example.test.model.MangaItem;
import com.example.test.viewmodel.CategoryViewModel;
import com.google.android.material.tabs.TabLayout;

public class FilterFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private CategoryViewModel viewModel;
    private CategoryAdapter adapter;

    public FilterFragment() {}

    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        recyclerView = view.findViewById(R.id.recycleview_category);
        tabLayout = view.findViewById(R.id.category_tablayout);
        progressBar = view.findViewById(R.id.progressBar);

        adapter = new CategoryAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        observeViewModel();

        viewModel.loadCategory("熱血");
        adapter.setOnItemClickListener(manga -> {
            Intent intent = new Intent(requireActivity(), MangaDetailActivity.class);
            String title = manga.getTitle();
            intent.putExtra("title",title);
            startActivity(intent);
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = tab.getText().toString();
                adapter.setMangas(null);
                viewModel.loadCategory(category);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void observeViewModel() {
        viewModel.getMangaList().observe(getViewLifecycleOwner(), mangaItems -> {
            adapter.setMangas(mangaItems);
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
}
