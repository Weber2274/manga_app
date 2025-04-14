package com.example.test.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.viewmodel.BookViewModel;

public class BookFragment extends Fragment {
    private BookViewModel viewModel;
    private BookAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(BookViewModel.class);
        viewModel.getBooks().observe(getViewLifecycleOwner(), adapter::setBooks);

        // ⚠️ 在這裡填入你的帳號密碼
        viewModel.loadBooks("whiteday", "THUC828053");

        return view;
    }
}
