package com.example.ssuwap.ui.book.selling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.R; // Drawable 리소스를 사용하기 위해 추가
import com.example.ssuwap.data.book.TagItem;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {
    private ArrayList<TagItem> tagList;

    public TagAdapter(ArrayList<TagItem> tagList) {
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 태그 아이템의 레이아웃을 인플레이트
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        TagItem tagItem = tagList.get(position);
        holder.tagView.setText(tagItem.getTagName());
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {
        public TextView tagView;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagView = itemView.findViewById(R.id.tag_text);
        }
    }
}