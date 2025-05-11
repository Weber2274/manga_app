package com.example.test.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;

import com.example.test.R;
import com.example.test.adapter.CategoryAdapter;
import com.example.test.model.MangaItem;
import com.example.test.viewmodel.CategoryViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
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
    private CategoryViewModel viewModel;
    private CategoryAdapter adapter;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private final String rexueUrl = "https://tw.manhuagui.com/list/japan_rexue_lianzai/view.html";
    private final String aiqingUrl = "https://tw.manhuagui.com/list/japan_aiqing_lianzai/view.html";
    private final String maoxianUrl = "https://tw.manhuagui.com/list/japan_maoxian_lianzai/view.html";
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        recyclerView = view.findViewById(R.id.recycleview_category);
        tabLayout = view.findViewById(R.id.category_tablayout);
        progressBar = view.findViewById(R.id.progressBar);
        adapter = new CategoryAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.loadCategories(rexueUrl);
        viewModel.getCategory().observe(getViewLifecycleOwner(), mangaItems -> {
            if (mangaItems != null) {
                Log.d("FilterFragment", "Items loaded: " + mangaItems.size());
                adapter.setMangas(mangaItems);
            } else {
                Log.d("FilterFragment", "No items loaded");
                adapter.setMangas(new ArrayList<>());
            }
        });
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(requireActivity(), MangaDetailActivity.class);
            intent.putExtra("pageUrl",item.getPageUrl());
            startActivity(intent);
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = tab.getText().toString();
                adapter.setMangas(new ArrayList<>());
                if(category.equals("熱血")){
                    viewModel.loadCategories(rexueUrl);
                }else if(category.equals("愛情")){
                    viewModel.loadCategories(aiqingUrl);
                }else if(category.equals("冒險")){
                    viewModel.loadCategories(maoxianUrl);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;

    }
}