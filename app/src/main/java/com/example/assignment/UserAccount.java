package com.example.assignment;

/* 사용자 계정 정보 모델 */
public class UserAccount {
    private String emailId;     // User Id
    private String password;    // User Pwd
    private String idToken;     // FB key value
    private String userName;

    public UserAccount() { } // 빈 생성자 생성

    public String getEmailId() { return emailId; }
    public String getPassword() { return password; }
    public String getIdToken() { return idToken; }
    public String getUserName() { return userName; }

    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setPassword(String password) { this.password = password; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
    public void setUserName(String userName) { this.userName = userName; }
}