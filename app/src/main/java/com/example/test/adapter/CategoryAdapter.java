package com.example.test.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.model.ItemList;
import com.example.test.model.MangaItem;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

    private List<MangaItem> mangaItems = new ArrayList<>();
    @SuppressLint("NotifyDataSetChanged")
    public void setMangas(List<MangaItem> mangaItems) {
        this.mangaItems = mangaItems;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        MangaItem mangaItem = mangaItems.get(position);
        holder.title.setText(mangaItem.getTitle());
        Glide.with(holder.itemView.getContext()).load(mangaItem.getImgUrl()).into(holder.cover);
    }

    @Override
    public int getItemCount() {
        Log.d("CategoryAdapter", "Item count: " + mangaItems.size());
        return mangaItems.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView cover;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            cover = itemView.findViewById(R.id.imageCover);
        }
    }
}
