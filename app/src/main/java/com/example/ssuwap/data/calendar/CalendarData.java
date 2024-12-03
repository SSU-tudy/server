package com.example.ssuwap.data.calendar;

public class CalendarData {
    String date;
    String color;
    int totalDuration;

    public CalendarData(String date, String color, int totalDuration) {
        this.date = date;
        this.color = color;
        this.totalDuration = totalDuration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }
}
