// MainAdapter.java
package com.example.ssuwap.ui.book.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R; // Drawable 리소스를 사용하기 위해 추가
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.databinding.ItemListBinding;
import com.example.ssuwap.ui.book.buying.BuyingActivity;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<BookInfo> arrayList;

    public MainAdapter(ArrayList<BookInfo> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MainAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListBinding binding = ItemListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CustomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.CustomViewHolder holder, int position) {
        BookInfo bookInfo = arrayList.get(position);

        // 이미지 로드
        String imageUrl = bookInfo.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.binding.ivBook);
            Log.d("MainAdapter", "Image loaded for position: " + position);
        } else {
            // 기본 이미지 설정 또는 에러 처리
            holder.binding.ivBook.setImageResource(R.drawable.ic_launcher_background);
            Log.w("MainAdapter", "Image URL is null or empty for position: " + position);
        }

        // 제목 설정
//        holder.binding.tvBook.setText(bookInfo.getTitle());
//        Log.d("MainAdapter", "Title set for position: " + position);

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BuyingActivity.class);
            intent.putExtra("BookInfo", bookInfo); // 키를 "bookInfo"로 통일
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ItemListBinding binding;

        public CustomViewHolder(@NonNull ItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}