package com.example.ssuwap.ui.post.uploadpost;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.databinding.PostItemBinding;

import java.util.ArrayList;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.PostViewHolder> {
    private ArrayList<PostInfo> list;
    private Context context;

    public PostAdaptor(Context context, ArrayList<PostInfo> list) {
        Log.d("PostAdaptor", "PostAdaptor()");
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("PostAdaptor", "onCreateViewHolder()");
        PostItemBinding binding = PostItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Log.d("PostAdaptor", "onBindViewHolder()");
        PostInfo postInfo = list.get(position);
        Glide.with(holder.itemView)
                .load(postInfo.getImageUrl())
                .into(holder.binding.postImage);
        holder.binding.postText.setText(postInfo.getDescription());
        Log.d("PostAdaptor", "image check : " + postInfo.getImageUrl());
        Log.d("PostAdaptor", "post check : " + postInfo.getDescription());

    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        PostItemBinding binding;
        public PostViewHolder(@NonNull PostItemBinding binding) {
            super(binding.getRoot());
            Log.d("PostAdaptor", "PostViewHolder()");
            this.binding = binding;
        }
    }
}
