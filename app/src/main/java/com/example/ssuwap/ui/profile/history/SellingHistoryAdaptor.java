package com.example.ssuwap.ui.book.selling;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.R;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.databinding.ItemSellingHistoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SellingHistoryAdaptor extends RecyclerView.Adapter<SellingHistoryAdaptor.SellingHistoryViewHolder> {

    private final ArrayList<BookInfo> arrayList;
    private final Context context;

    public SellingHistoryAdaptor(ArrayList<BookInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public SellingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSellingHistoryBinding binding = ItemSellingHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SellingHistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SellingHistoryViewHolder holder, int position) {
        BookInfo book = arrayList.get(position);

        Glide.with(context)
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.binding.ivBook);

        holder.binding.tvTitle.setText(book.getTitle());
        holder.binding.tvPrice.setText(book.getPrice());

        holder.binding.cbIsSold.setOnCheckedChangeListener(null);
        holder.binding.cbIsSold.setChecked(book.isSold());

        holder.binding.cbIsSold.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (book.getKey() != null) {
                updateSoldStatus(book, isChecked);
                book.setSold(true); // 로컬 데이터 업데이트
                notifyDataSetChanged();
            } else {
                Log.e("SellingHistoryAdaptor", "Book key is null. Cannot update isSold status.");
            }
        });
    }

    private void updateSoldStatus(BookInfo book, boolean isSold) {
        if (book.getKey() == null || book.getKey().isEmpty()) {
            Log.e("SellingHistoryAdaptor", "Book key is missing. Cannot update isSold status.");
            return;
        }

        DatabaseReference bookRef = FirebaseDatabase.getInstance()
                .getReference("BookInfo")
                .child(book.getKey()); // Firebase 고유 키 사용

        bookRef.child("isSold").setValue(isSold).addOnSuccessListener(aVoid -> {
            Log.d("SellingHistoryAdaptor", "판매 상태 업데이트 완료: " + isSold);
        }).addOnFailureListener(e -> {
            Log.e("SellingHistoryAdaptor", "판매 상태 업데이트 실패", e);
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public static class SellingHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemSellingHistoryBinding binding;

        public SellingHistoryViewHolder(@NonNull ItemSellingHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}