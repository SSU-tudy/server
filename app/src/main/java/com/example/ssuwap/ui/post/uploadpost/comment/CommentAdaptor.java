package com.example.ssuwap.ui.post.uploadpost.comment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.data.post.CommentInfo;
import com.example.ssuwap.databinding.CommentItemBinding;
import com.example.ssuwap.ui.post.uploadpost.FullScreenActivity;

import java.util.ArrayList;

public class CommentAdaptor extends RecyclerView.Adapter<CommentAdaptor.CommentViewHolder> {

    private ArrayList<CommentInfo> list;
    private Context context;

    public CommentAdaptor(Context context, ArrayList<CommentInfo> list){
        this.context = context;
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
        holder.binding.userName.setText("익명의 답변자"+position);

        holder.binding.commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullScreenActivity.class);
                intent.putExtra("imageUrl", comment.getCommentImage()); // 이미지 URL 전달
                context.startActivity(intent);
            }
        });
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
