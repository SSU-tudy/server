package com.example.ssuwap.data.post;

import android.os.Parcel;
import android.os.Parcelable;

public class CommentInfo implements Parcelable {
    public String userInfoId;
    public String userName;
    public String commentID;
    public String content;
    public String commentImage;
    public String userIndex;

    public CommentInfo() {}

    public CommentInfo(String userInfoId, String userName,String commentID, String content, String commentImage, String userIndex) {
        this.userInfoId = userInfoId;
        this.userName = userName;
        this.commentID = commentID;
        this.content = content;
        this.commentImage = commentImage;
        this.userIndex = userIndex;
    }

    protected CommentInfo(Parcel in) {
        userInfoId = in.readString();
        userName = in.readString();
        commentID = in.readString();
        content = in.readString();
        commentImage = in.readString();
    }

    public static final Creator<CommentInfo> CREATOR = new Creator<CommentInfo>() {
        @Override
        public CommentInfo createFromParcel(Parcel in) {
            return new CommentInfo(in);
        }

        @Override
        public CommentInfo[] newArray(int size) {
            return new CommentInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userInfoId);
        parcel.writeString(userName);
        parcel.writeString(commentID);
        parcel.writeString(content);
        parcel.writeString(commentImage);
    }

    // Getter and Setter methods

    public String getUserInfoId() {return userInfoId;}
    public String getCommentID() { return commentID; }
    public String getContent() { return content; }
    public String getCommentImage() {return commentImage;}
    public String getUserName() { return userName; }
    public String getUserIndex() {return userIndex;}

    public void setUserInfoId(String userInfoId) {this.userInfoId = userInfoId;}
    public void setCommentID(String commentID) { this.commentID = commentID; }
    public void setContent(String content) { this.content = content; }
    public void setCommentImage(String commentImage) {this.commentImage = commentImage;}
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserIndex(String userIndex) {this.userIndex = userIndex;}
}
