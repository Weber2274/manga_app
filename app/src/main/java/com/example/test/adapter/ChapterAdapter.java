package com.example.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.model.MangaItem;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder>{
    private final List<String> chapterList;
    public interface OnItemClickListener {
//        void onItemClick();
    }
    private CategoryAdapter.OnItemClickListener listener;
    public void setOnItemClickListener(CategoryAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    public ChapterAdapter() {
        chapterList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            chapterList.add("第 " + i + " 集");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String chapterName = chapterList.get(position);
        holder.btnChapter.setText(chapterName);

        holder.btnChapter.setOnClickListener(v -> {
            if(listener != null){
//                listener.onItemClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button btnChapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnChapter = itemView.findViewById(R.id.btn_chapter);
        }
    }
}
