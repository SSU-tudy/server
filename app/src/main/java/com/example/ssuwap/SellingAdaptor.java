package com.example.ssuwap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.databinding.SellinglistBinding;

import java.util.ArrayList;

public class SellingAdaptor extends RecyclerView.Adapter<SellingAdaptor.SellingViewHolder> {

    private ArrayList<SellingListData> list;
    public SellingAdaptor(ArrayList<SellingListData> list){
        this.list = list;
    }

    @NonNull
    @Override
    public SellingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SellinglistBinding binding = SellinglistBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new SellingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SellingAdaptor.SellingViewHolder holder, int position) {
        String title = list.get(position).getTv_title();
        String chatnum = String.valueOf(list.get(position).getTv_chatnum());
        String price = String.valueOf(list.get(position).getTv_price() + " 원");
        String time = String.valueOf(list.get(position).getTv_time()+" 시간전");
        int book = list.get(position).getIv_book();
        String book_title = list.get(position).getTv_book();
        holder.binding.title.setText(title);
        holder.binding.chatnum.setText(chatnum);
        holder.binding.price.setText(price);
        holder.binding.ivBook.setImageResource(book);
        holder.binding.time.setText(time);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SellingViewHolder extends RecyclerView.ViewHolder {
        private SellinglistBinding binding;

        private SellingViewHolder(SellinglistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
