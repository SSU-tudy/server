package com.example.ssuwap.data.calendar;

public class SubjectData {
    int color;
    float totalDuration;

    public SubjectData(int color, float totalDuration) {
        this.color = color;
        this.totalDuration = totalDuration;
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
