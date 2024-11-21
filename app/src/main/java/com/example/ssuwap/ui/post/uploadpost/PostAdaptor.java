package com.example.ssuwap.ui.post.uploadpost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.data.post.PostInfo;
import com.example.ssuwap.databinding.PostItemBinding;

import java.util.ArrayList;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.PostViewHolder> {
    private ArrayList<PostInfo> list;
    private Context context;
    FragmentManager fragmentManager;

    public PostAdaptor(Context context, ArrayList<PostInfo> list, FragmentManager fragmentManager) {
        Log.d("PostAdaptor", "PostAdaptor()");
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;

        if (fragmentManager == null) {
            Log.e("PostAdaptor", "FragmentManager is null");
        }
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

        holder.binding.postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("PostAdaptor", "onClick()");

                if (postInfo == null) {
                    Log.e("PostAdaptor", "postInfo is null");
                }
                else Log.d("PostAdaptor", "postInfo is success");


                Bundle bundle = new Bundle();
                bundle.putParcelable("PostInfo", postInfo);

                PostViewFragment postViewFragment = PostViewFragment
                        .newInstance("Param1","Param2",postInfo);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, postViewFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.binding.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullScreenActivity.class);
                Log.d("PostAdaptor", "PostAdaptor ImageUrl : "+postInfo.getImageUrl());
                intent.putExtra("imageUrl", postInfo.getImageUrl()); // 이미지 URL 전달
                context.startActivity(intent);
            }
        });

        holder.binding.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullScreenActivity.class);
                intent.putExtra("imageUrl", postInfo.getImageUrl()); // 이미지 URL 전달
                context.startActivity(intent);
            }
        });

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
