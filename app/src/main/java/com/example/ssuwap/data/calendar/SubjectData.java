package com.example.ssuwap.data.calendar;

public class SubjectData {
    int color;
    float totalDuration;
    String subject;

    public SubjectData(int color, float totalDuration, String subject) {
        this.color = color;
        this.totalDuration = totalDuration;
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(float totalDuration) {
        this.totalDuration = totalDuration;
    }
}
