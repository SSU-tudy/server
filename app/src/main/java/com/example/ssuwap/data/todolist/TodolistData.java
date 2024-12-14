package com.example.ssuwap.data.todolist;

public class TodolistData {

    private String key;
    private String subject;
    private long totalDuration;
    private int color;
    private boolean isPlaying;


    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public TodolistData(String key, String subject, int color) {
        this.subject = subject;
        this.color = color;
        this.key = key;
        this.totalDuration = 0;
        isPlaying = false;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getTotalDuration() {
        return totalDuration;
    }
}
