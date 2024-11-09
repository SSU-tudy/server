package com.example.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private ArrayList<BookInfo> arrayList;
    private Context context;

    public BookAdapter(ArrayList<BookInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        BookViewHolder holder = new BookViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.BookViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getBookImage())
                .into(holder.iv_image);
        holder.tv_title.setText(arrayList.get(position).getBookTitle());
        holder.tv_price.setText(arrayList.get(position).getBookPrice());
        holder.tv_time.setText(arrayList.get(position).getUploadedTime());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class BookViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_image;
        TextView tv_title;
        TextView tv_time;
        TextView tv_price;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_image = itemView.findViewById(R.id.iv_image);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_price = itemView.findViewById(R.id.tv_price);
            this.tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
