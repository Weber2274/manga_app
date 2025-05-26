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
import com.example.test.model.HistoryItem;
import com.example.test.model.LikeItem;
import com.example.test.model.MangaItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryItem> historyItems = new ArrayList<>();
    public interface OnItemClickListener {
        void onItemClick(MangaItem item);
    }

    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setMangas(List<HistoryItem> historyItems) {
        this.historyItems = historyItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryiewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem historyItem = historyItems.get(position);
        holder.book.setText(historyItem.getBook());
        holder.episode.setText(historyItem.getBook());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(historyItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("LikeAdapter", "Item count: " + mangaItems.size());
        return mangaItems.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView cover;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            cover = itemView.findViewById(R.id.imageCover);
        }
    }
}
