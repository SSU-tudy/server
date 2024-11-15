package com.example.ssuwap.ui.post.uploadpost.comment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.databinding.CommentItemBinding;

import java.util.ArrayList;

public class CommentAdaptor extends RecyclerView.Adapter<CommentAdaptor.CommentViewHolder> {

    private ArrayList<CommentInfo> list;

    public CommentAdaptor(ArrayList<CommentInfo> list){
        this.list = list;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding binding = CommentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentInfo comment = list.get(position);
        Glide.with(holder.itemView)
                .load(comment.getCommentImage())
                .into(holder.binding.commentImage);
        holder.binding.commText.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        CommentItemBinding binding;

        public CommentViewHolder(CommentItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
