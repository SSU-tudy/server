package com.example.ssuwap.data.post;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostInfo implements Parcelable {

    private String userInfoId;
    private String userName;
    private String postID;
    private String imageUrl;
    private String description;
    private String postTag1;
    private String postTag2;
    private String postTag3;
    private Map<String, CommentInfo> comments; // Firebase에서 불러올 때 사용하는 Map 형태의 comments 필드
    private ArrayList<CommentInfo> commentsList; // ArrayList 형태로 변환하여 사용할 필드

    public PostInfo() {
        comments = new HashMap<>();
        commentsList = new ArrayList<>();
    }

    public PostInfo(String userInfoId,String userName,String postID, String imageUrl, String description, String postTag1, String postTag2, String postTag3, Map<String, CommentInfo> comments) {
        this.userInfoId = userInfoId;
        this.userName = userName;
        this.postID = postID;
        this.imageUrl = imageUrl;
        this.description = description;
        this.postTag1 = postTag1;
        this.postTag2 = postTag2;
        this.postTag3 = postTag3;
        this.comments = comments;
        this.commentsList = new ArrayList<>(comments.values()); // Map을 ArrayList로 변환
    }

    protected PostInfo(Parcel in) {
        userInfoId = in.readString();
        userName = in.readString();
        postID = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        postTag1 = in.readString();
        postTag2 = in.readString();
        postTag3 = in.readString();

        // Parcelable에서 commentsList로 읽어오기
        commentsList = in.createTypedArrayList(CommentInfo.CREATOR);
        comments = new HashMap<>();

        if (commentsList != null) {
            for (CommentInfo comment : commentsList) {
                comments.put(comment.getCommentID(), comment); // CommentInfo에 getCommentID() 메서드 필요
            }
        }
    }

    public static final Creator<PostInfo> CREATOR = new Creator<PostInfo>() {
        @Override
        public PostInfo createFromParcel(Parcel in) {
            return new PostInfo(in);
        }

        @Override
        public PostInfo[] newArray(int size) {
            return new PostInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(userInfoId);
        parcel.writeString(userName);
        parcel.writeString(postID);
        parcel.writeString(imageUrl);
        parcel.writeString(description);
        parcel.writeString(postTag1);
        parcel.writeString(postTag2);
        parcel.writeString(postTag3);

        // commentsList를 ArrayList 형태로 변환하여 저장
        parcel.writeTypedList(commentsList);
    }

    public void setUserInfoId(String userInfoId) {
        this.userInfoId = userInfoId;
    }

    // Setter & Getter 메서드들
    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPostTag1(String postTag1) {
        this.postTag1 = postTag1;
    }

    public void setPostTag2(String postTag2) {
        this.postTag2 = postTag2;
    }

    public void setPostTag3(String postTag3) {
        this.postTag3 = postTag3;
    }

    public void setCommentsList(ArrayList<CommentInfo> commentsList) {
        this.commentsList = commentsList;
        this.comments = new HashMap<>();
        for (CommentInfo comment : commentsList) {
            comments.put(comment.getCommentID(), comment);
        }
    }

    public void setComments(Map<String, CommentInfo> comments) {
        this.comments = comments;
        this.commentsList = new ArrayList<>(comments.values()); // Map에서 ArrayList로 변환
    }

    public void setUserName(String userName){ this.userName = userName; }

    public ArrayList<CommentInfo> getCommentsList() {
        return commentsList;
    }

    public String getUserInfoId() {
        return userInfoId;
    }

    public String getPostID() {
        return postID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, CommentInfo> getComments() {
        return comments;
    }

    public String getUserName() { return userName; }

    public String getPostTag1() {
        return postTag1;
    }

    public String getPostTag2() {
        return postTag2;
    }

    public String getPostTag3() {
        return postTag3;
    }
}