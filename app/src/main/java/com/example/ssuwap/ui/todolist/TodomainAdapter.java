package com.example.ssuwap.ui.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuwap.data.todolist.todolistData;
import com.example.ssuwap.databinding.ActivityTodomainBinding;
import com.example.ssuwap.databinding.TodoListBinding;

import java.util.ArrayList;

public class TodomainAdapter extends RecyclerView.Adapter<TodomainAdapter.MyViewHolder> {

    private ArrayList<todolistData> list;
    private String todoSting;

    public TodomainAdapter(ArrayList<todolistData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TodoListBinding binding = TodoListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTodo.setText(list.get(position).getTodo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TodoListBinding binding;
        public MyViewHolder(TodoListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}