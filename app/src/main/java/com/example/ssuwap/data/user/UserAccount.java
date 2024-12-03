// UserAccount.java
package com.example.ssuwap.data.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserAccount implements Serializable {
    private String emailId;      // User Id
    // private String password;  // User Pwd - 제거 (Firebase Auth에서 관리)
    private String idToken;      // Firebase key value
    private String userName;     // 사용자 이름 (필요 없다면 제거)
    private String studentName;  // 학생 이름 (Firebase 데이터와 일치)
    private String grade;        // 학년
    private String semester;     // 학기
    private String department;   // 학과
    private String imageUrl;

    // 시간표 정보를 저장할 필드
    private List<Subject> subjects;

    // 빈 생성자 생성
    public UserAccount() {
        this.subjects = new ArrayList<>(); // subjects 초기화
    }

    // Getter 및 Setter 메서드
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    // public String getPassword() { return password; }
    // public void setPassword(String password) { this.password = password; }

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }

    public void addSubject(Subject subject) { this.subjects.add(subject); }
}