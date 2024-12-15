package com.example.ssuwap.data.post;

public class CommentUserInfo {

    private String userInfoId;
    private int userIndex;

    public CommentUserInfo(String userInfoId, int userIndex){
        this.userInfoId = userInfoId;
        this.userIndex = userIndex;
    }

    public void setUserInfoId(String userInfoId) {this.userInfoId = userInfoId;}
    public void setUserIndex(int userIndex) {this.userIndex = userIndex;}

    public String getUserInfoId() {return userInfoId;}
    public int getUserIndex() {return userIndex;}
}
