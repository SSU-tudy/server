package com.example.ssuwap.data.todolist;

import android.view.LayoutInflater;

import com.example.ssuwap.R;
import com.example.ssuwap.databinding.TodoListBinding;
import com.example.ssuwap.ui.todolist.TodomainActivity;

public class TodolistData {
    private int id;
    private String todo;
    private boolean isPlaying;
    private TodoTimeData timeData;
    public TodolistData(int id, String todo) {
        this.id = id;
        this.todo = todo;
        this.isPlaying = false; // 기본값: 일시정지
        this.timeData = new TodoTimeData();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public void setTimeData(TodoTimeData timeData) {
        this.timeData = timeData;
    }

    public String getTodo() {
        return todo;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public TodoTimeData getTimeData() {
        return timeData;
    }
}
