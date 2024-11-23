package com.example.ssuwap.data.todolist;

public class TodolistData {

    private String key;
    private String subject;
    //private List<Session> sessions;
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
        //this.sessions = new ArrayList<>();
        this.totalDuration = 0;
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

    public TodolistData() {
        // Firebase를 위한 기본 생성자
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

//    public List<Session> getSessions() {
//        return sessions;
//    }

//    public void addSession(long startTime, long endTime) {
//        sessions.add(new Session(startTime, endTime));
//        totalDuration += (endTime - startTime);
//    }

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
