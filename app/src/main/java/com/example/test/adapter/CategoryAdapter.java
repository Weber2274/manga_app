package com.example.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.model.MangaItem;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MangaItem manga);
    }
    private List<MangaItem> mangas = new ArrayList<>();
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public void setMangas(List<MangaItem> mangaList) {
        if (mangaList == null) mangaList = new ArrayList<>();
        this.mangas = mangaList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        MangaItem manga = mangas.get(position);
        holder.title.setText(manga.getTitle());
        Glide.with(holder.itemView.getContext()).load(manga.getImgUrl()).into(holder.image);
        holder.itemView.setOnClickListener(view -> {
            if(listener != null){
                listener.onItemClick(manga);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mangas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            image = itemView.findViewById(R.id.imageCover);
        }
    }


}
