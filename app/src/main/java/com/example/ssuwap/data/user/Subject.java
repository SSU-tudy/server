// Subject.java
package com.example.ssuwap.data.user;

public class Subject {
    private String subjectName;
    private String grade;
    private String semester;
    private String department;
    private String startTime;
    private String endTime;

    // 빈 생성자
    public Subject() {}

    // 매개변수가 있는 생성자
    public Subject(String subjectName, String grade, String semester, String department, String startTime, String endTime) {
        this.subjectName = subjectName;
        this.grade = grade;
        this.semester = semester;
        this.department = department;
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

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}