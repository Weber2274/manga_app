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
import com.example.test.model.CategoryTitleItem;
import com.example.test.model.ItemList;
import com.example.test.model.MangaGroup;
import com.example.test.model.MangaItem;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MangaItem> items = new ArrayList<>();
    public static final int TYPE_CATEGORY = 1;
    public static final int TYPE_MANGA = 2;

    private CategoryAdapter.OnItemClickListener listener;

    public void setOnItemClickListener(CategoryAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<MangaItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public int getItemViewType(int position){
        return items.get(position).getType();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_CATEGORY){
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_category,parent,false);
            return new CategoryViewHolder(view);
        }else{
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_book,parent,false);
            return new MangaViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CategoryViewHolder){
//            ((CategoryViewHolder) holder).title.setText(((CategoryTitleItem)items.get(position)).getCategoryTitle());
        }else{
            MangaItem manga = (MangaItem) items.get(position);
            ((MangaViewHolder) holder).title.setText(manga.getTitle());
            Glide.with(holder.itemView.getContext())
                    .load(manga.getImgUrl())
                    .into(((MangaViewHolder)holder).imgCover);
            holder.itemView.setOnClickListener(view -> {
                if(listener != null){
                    listener.onItemClick(manga);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.d("Adapter", "Item count: " + items.size());
        return items.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.categoryTitle);
        }
    }
    public static class MangaViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView imgCover;
        public MangaViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            imgCover = itemView.findViewById(R.id.imageCover);
        }
    }
}
