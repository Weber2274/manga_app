package com.example.test.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.BookAdapter;
import com.example.test.viewmodel.BookViewModel;

public class BookFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private BookViewModel viewModel;
    private BookAdapter adapter;
    private RecyclerView recyclerView;

    public static BookFragment newInstance(String param1, String param2) {
        BookFragment fragment = new BookFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnBookClickListener(book -> {
            String url = book.getPageUrl();
            if (url != null && !url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "找不到漫畫連結"+url, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel = new ViewModelProvider(this).get(BookViewModel.class);
        viewModel.getBooks().observe(getViewLifecycleOwner(), adapter::setBooks);

        // ⚠️ 在這裡填入你的帳號密碼
        viewModel.loadBooks("", "");


        return view;
    }
}
