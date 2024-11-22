package com.example.ssuwap.ui.book.buying.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.data.chat.ChatData;
import com.example.ssuwap.databinding.ChatItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatData> mDataset;
    private Context context;
    private String myName;

    public ChatAdapter(Context context, ArrayList<ChatData> arrayList, String myName){
        this.context = context;
        this.mDataset = arrayList;
        this.myName = myName;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatItemBinding binding = ChatItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatViewHolder(binding);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        public ChatItemBinding binding;

        public ChatViewHolder(@NonNull ChatItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {
        ChatData chat = mDataset.get(position);

        holder.binding.tvName.setText(chat.getName());
        holder.binding.tvMsg.setText(chat.getMsg());
        if(chat.getName().equals(this.myName)){
            holder.binding.tvMsg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.binding.tvName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }else{
            holder.binding.tvMsg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.binding.tvName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public ChatData getChat(int position){
        return mDataset != null ? mDataset.get(position) : null;
    }

    public void addChat(ChatData chat){
        mDataset.add(chat);
        notifyItemInserted(mDataset.size()-1);
    }
}

