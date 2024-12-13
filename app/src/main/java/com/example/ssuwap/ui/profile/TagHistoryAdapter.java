package com.example.ssuwap.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R;

import java.util.List;

public class TagHistoryAdapter extends RecyclerView.Adapter<TagHistoryAdapter.TagViewHolder> {

    private List<String> tagList;

    public TagHistoryAdapter(List<String> tagList) {
        this.tagList = tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_history, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        if (tagList != null && position < tagList.size()) {
            String tag = tagList.get(position);
            holder.tvTag.setText(tag); // 태그 설정
        }
    }

    @Override
    public int getItemCount() {
        return tagList != null ? tagList.size() : 0;
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tv_tag_history);
        }
    }
}