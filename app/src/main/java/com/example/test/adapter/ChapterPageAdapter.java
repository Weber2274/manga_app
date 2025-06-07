package com.example.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;

import java.util.List;

public class ChapterPageAdapter extends RecyclerView.Adapter<ChapterPageAdapter.ImageViewHolder> {
    private List<Integer> imageResIds;

    public ChapterPageAdapter(List<Integer> imageResIds) {
        this.imageResIds = imageResIds;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.page_image);
        }
    }



    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_page, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        int resId = imageResIds.get(position);
        holder.imageView.setImageResource(resId);
    }

    @Override
    public int getItemCount() {
        return imageResIds.size();
    }
}
