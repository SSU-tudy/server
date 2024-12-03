// SellingHistoryAdapter.java
package com.example.ssuwap.ui.profile.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.databinding.ItemSellingHistoryBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SellingHistoryAdapter extends RecyclerView.Adapter<SellingHistoryAdapter.ViewHolder> {

    private ArrayList<BookInfo> bookList;
    private Context context;
    private ViewGroup parent;
    private int viewType;

    public SellingHistoryAdapter(ArrayList<BookInfo> bookList, Context context){
        this.bookList = bookList;
        this.context = context;
    }

    @NonNull
    @Override
    public SellingHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSellingHistoryBinding binding = ItemSellingHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SellingHistoryAdapter.ViewHolder holder, int position) {
        BookInfo bookInfo = bookList.get(position);

        holder.binding.tvTitle.setText(bookInfo.getTitle());
        holder.binding.tvPrice.setText(bookInfo.getPrice());

        Glide.with(context)
                .load(bookInfo.getImageUrl())
                .into(holder.binding.ivBook);

        holder.binding.cbIsSold.setOnCheckedChangeListener(null); // 기존 리스너 제거
        holder.binding.cbIsSold.setChecked(bookInfo.isSold());

        holder.binding.cbIsSold.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 판매 상태 업데이트
            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("BookInfo").child(bookInfo.getTitle());
            bookRef.child("isSold").setValue(isChecked)
                    .addOnSuccessListener(aVoid -> {
                        bookInfo.setSold(isChecked);
                        Toast.makeText(context, "판매 상태가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        holder.binding.cbIsSold.setChecked(!isChecked); // 상태 복원
                    });
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemSellingHistoryBinding binding;

        public ViewHolder(@NonNull ItemSellingHistoryBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}