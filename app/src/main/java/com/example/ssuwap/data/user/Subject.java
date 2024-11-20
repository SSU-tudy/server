package com.example.ssuwap.data.user;

public class Subject {
    private String subjectName;
    private String subjectCode;
    private String schedule;

    // 빈 생성자
    public Subject() {}

    // 매개변수가 있는 생성자
    public Subject(String subjectName, String subjectCode, String schedule) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.schedule = schedule;
    }

    // Getter 및 Setter
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
}