package com.example.ssuwap.ui.book;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.data.tag.TagData;
import com.example.ssuwap.databinding.TaglistBinding;

import java.util.ArrayList;

public class TaglistAdaptor extends RecyclerView.Adapter<TaglistAdaptor.TaglistViewHolder> {

    ArrayList<TagData> list;

    public TaglistAdaptor(ArrayList<TagData> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public TaglistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaglistBinding binding = TaglistBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false);
        return new TaglistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaglistViewHolder holder, int position) {
        String tagname = list.get(position).getTagname();
        holder.binding.tvTag.setText(tagname);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TaglistViewHolder extends RecyclerView.ViewHolder{
        private TaglistBinding binding;
        protected TaglistViewHolder(TaglistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
