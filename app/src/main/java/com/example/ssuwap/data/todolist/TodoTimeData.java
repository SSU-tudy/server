package com.example.ssuwap.data.todolist;
import java.util.ArrayList;
import java.util.List;


public class TodoTimeData {
    private List<Session> sessions = new ArrayList<>();
    private long totalDuration = 0;

    public static class Session {
        private long startMillis;
        private long endMillis;

        public Session(long startMillis, long endMillis) {
            this.startMillis = startMillis;
            this.endMillis = endMillis;
        }

        public long getStartMillis() {
            return startMillis;
        }

        public long getEndMillis() {
            return endMillis;
        }
    }

    public void addSession(long startMillis, long endMillis) {
        sessions.add(new Session(startMillis, endMillis));
        totalDuration += (endMillis - startMillis) / 1000; // 초 단위
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public List<Session> getSessions() {
        return sessions;
    }
}
