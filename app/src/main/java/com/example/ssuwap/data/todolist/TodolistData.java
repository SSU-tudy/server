package com.example.ssuwap.data.todolist;

public class TodolistData {
    private int id;
    private String todo;
    private int color;
    private boolean isPlaying;
    private TodoTimeData timeData;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public TodolistData(int id, String todo, int color) {
        this.color = color;;
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
