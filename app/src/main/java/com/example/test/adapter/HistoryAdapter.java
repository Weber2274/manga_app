package com.example.test.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.model.MangaItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = "";
    private List<MangaItem> mangaList = new ArrayList<>();
    public interface OnItemClickListener {
        void onItemClick(MangaItem item);
    }

    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setMangas(List<MangaItem> mangaItems) {
        this.mangaList = mangaItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        MangaItem mangaItem = mangaList.get(position);
        holder.title.setText(mangaItem.getTitle());

        Glide.with(holder.itemView.getContext())
                .load(mangaItem.getImgUrl())
                .into(holder.cover);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(mangaItem);
            }
        });
        if (user != null) {
            uid = user.getUid();
        } else {
        }

        DocumentReference historyDoc = db.collection("users")
                .document(uid)
                .collection("history")
                .document(mangaItem.getTitle());
        historyDoc.get().addOnSuccessListener(documentSnapshot -> {
                String latest = documentSnapshot.getString("latest");
                holder.latest.setText("上次閱讀:第"+latest+"話");
        });
    }

    @Override
    public int getItemCount() {
        Log.d("LikeAdapter", "Item count: " + mangaList.size());
        return mangaList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView cover;
        TextView latest;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            cover = itemView.findViewById(R.id.imageCover);
            latest = itemView.findViewById(R.id.history_latest);
        }
    }
}