package com.example.ssuwap.ui.book.selling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.data.book.SellingListData;
import com.example.ssuwap.databinding.SellinglistBinding;

import java.util.ArrayList;

public class SellingAdaptor extends RecyclerView.Adapter<SellingAdaptor.SellingViewHolder>{

    private ArrayList<SellingListData> arrayList;
    private Context context;

    public SellingAdaptor(ArrayList<SellingListData> arrayList, Context context) {
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
    public void onBindViewHolder(@NonNull SellingAdaptor.SellingViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getIv_book())
                .into(holder.binding.ivBook);
        holder.binding.price.setText(arrayList.get(position).getTv_price());
        holder.binding.title.setText(arrayList.get(position).getTv_title());
        holder.binding.time.setText(arrayList.get(position).getTv_time());
        holder.binding.chatnum.setText(arrayList.get(position).getTv_chatnum());
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
