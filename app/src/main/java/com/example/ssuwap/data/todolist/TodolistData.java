package com.example.ssuwap.data.todolist;

import java.util.ArrayList;
import java.util.List;

public class TodolistData {
    private String subject;
    private List<Session> sessions;
    private long totalDuration;

    public TodolistData() {
        // Firebase를 위한 기본 생성자
    }

    public TodolistData(String subject) {
        this.subject = subject;
        this.sessions = new ArrayList<>();
        this.totalDuration = 0;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void addSession(long startTime, long endTime) {
        sessions.add(new Session(startTime, endTime));
        totalDuration += (endTime - startTime);
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public static class Session {
        private long startTime;
        private long endTime;

        public Session() {
        }

        public Session(long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getEndTime() {
            return endTime;
        }
    }
}
