package com.example.ssuwap.data.user;

import java.io.Serializable;

public class Subject implements Serializable {
    private String subjectName;  // 과목명
    private String grade;        // 학년
    private String semester;     // 학기
    private String department;   // 학과
    private String day;          // 수업 요일 (월요일, 화요일 등)
    private String startTime;    // 수업 시작 시간 (HH:mm 형식)
    private String endTime;      // 수업 종료 시간 (HH:mm 형식)

    // 빈 생성자
    public Subject() {}

    // 매개변수가 있는 생성자
    public Subject(String subjectName, String grade, String semester, String department, String day, String startTime, String endTime) {
        this.subjectName = subjectName;
        this.grade = grade;
        this.semester = semester;
        this.department = department;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter 및 Setter
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}