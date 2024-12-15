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
        // 현재 책 데이터 가져오기
        BookInfo book = arrayList.get(position);

        // 데이터 바인딩
        Glide.with(context)
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.binding.ivBook);

        holder.binding.tvTitle.setText(book.getTitle());
        holder.binding.tvPrice.setText(book.getPrice());

        // CheckBox 초기 상태 설정
        holder.binding.cbIsSold.setOnCheckedChangeListener(null); // 기존 리스너 제거
        holder.binding.cbIsSold.setChecked(book.isSold()); // CheckBox 상태 초기화

        // CheckBox 상태 변경 리스너
        holder.binding.cbIsSold.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //updateSoldStatus(book, isChecked);
            book.setSold(true);
            notifyDataSetChanged();
        });
    }

    private void updateSoldStatus(BookInfo book, boolean isSold) {
        DatabaseReference bookRef = FirebaseDatabase.getInstance()
                .getReference("BookInfo")
                .child(book.getUploaderId()) // 업로더 ID 기준
                .child(book.getTitle()); // 책 고유 제목 기준 (필요하면 고유 ID로 변경 가능)

        // Firebase에서 isSold 상태 업데이트
        bookRef.child("isSold").setValue(isSold).addOnSuccessListener(aVoid -> {
            book.setSold(true); // 로컬 데이터 업데이트
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