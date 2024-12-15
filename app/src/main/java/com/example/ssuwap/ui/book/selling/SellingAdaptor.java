package com.example.ssuwap.ui.book.selling;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.databinding.SellinglistBinding;
import com.example.ssuwap.ui.book.buying.BuyingActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SellingAdaptor extends RecyclerView.Adapter<SellingAdaptor.SellingViewHolder>{

    private ArrayList<BookInfo> arrayList;
    private Context context;
    private int chatNum = 1000;

    public SellingAdaptor(ArrayList<BookInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public SellingAdaptor.SellingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SellinglistBinding binding = SellinglistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SellingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SellingViewHolder holder, int position) {
        // 현재 책 position
        BookInfo book = arrayList.get(position);

        // holder 설정
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImageUrl())
                .into(holder.binding.ivBook); Log.d("SellingAdaptor", "image check");
        holder.binding.price.setText(arrayList.get(position).getPrice()+"원"); Log.d("SellingAdaptor", "price check" + arrayList.get(position).getPrice());
        holder.binding.title.setText(arrayList.get(position).getTitle()); Log.d("SellingAdaptor", "title check");
        holder.binding.chatnum.setText(String.valueOf(chatNum));

        long elapsedTime = (System.currentTimeMillis() - arrayList.get(position).getTime()) / (1000 * 60 * 60);
        if (elapsedTime < 24) holder.binding.time.setText(String.format("%d시간 전", elapsedTime));
        else holder.binding.time.setText(String.format("%d일 전", elapsedTime/24));

        // holder listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BuyingActivity.class);
                intent.putExtra("BookInfo", book);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class SellingViewHolder extends RecyclerView.ViewHolder {
        private SellinglistBinding binding;

        public SellingViewHolder(@NonNull SellinglistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
