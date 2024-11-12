package com.example.ssuwap.ui.book.selling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ssuwap.data.book.BookInfo;
import com.example.ssuwap.databinding.SellinglistBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SellingAdaptor extends RecyclerView.Adapter<SellingAdaptor.SellingViewHolder>{

    private ArrayList<BookInfo> arrayList;
    private Context context;
    private int chatNum;
    private int elapsedHours;

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
    public void onBindViewHolder(@NonNull SellingAdaptor.SellingViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImageUrl())
                .into(holder.binding.ivBook);
        holder.binding.price.setText(arrayList.get(position).getPrice());
        holder.binding.title.setText(arrayList.get(position).getTitle());
        // chatting 수 어떻게 구하지..
        holder.binding.chatnum.setText(chatNum);
        holder.binding.time.setText(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) - arrayList.get(position).getTime());
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
