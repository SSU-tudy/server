package com.example.ssuwap.data.post;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostInfo implements Parcelable {
    public String postID;
    public String imageUrl;
    public String description;
    private Map<String, CommentInfo> comments; // Firebase에서 불러올 때 사용하는 Map 형태의 comments 필드
    private ArrayList<CommentInfo> commentsList; // ArrayList 형태로 변환하여 사용할 필드

    public PostInfo() {
        comments = new HashMap<>();
        commentsList = new ArrayList<>();
    }

    public PostInfo(String postID, String imageUrl, String description, Map<String, CommentInfo> comments) {
        this.postID = postID;
        this.imageUrl = imageUrl;
        this.description = description;
        this.comments = comments;
        this.commentsList = new ArrayList<>(comments.values()); // Map을 ArrayList로 변환
    }

    protected PostInfo(Parcel in) {
        postID = in.readString();
        imageUrl = in.readString();
        description = in.readString();

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
        parcel.writeString(postID);
        parcel.writeString(imageUrl);
        parcel.writeString(description);

        // commentsList를 ArrayList 형태로 변환하여 저장
        parcel.writeTypedList(commentsList);
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

    public ArrayList<CommentInfo> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(ArrayList<CommentInfo> commentsList) {
        this.commentsList = commentsList;
        this.comments = new HashMap<>();
        for (CommentInfo comment : commentsList) {
            comments.put(comment.getCommentID(), comment);
        }
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

    public void setComments(Map<String, CommentInfo> comments) {
        this.comments = comments;
        this.commentsList = new ArrayList<>(comments.values()); // Map에서 ArrayList로 변환
    }

    public Map<String, CommentInfo> getComments() {
        return comments;
    }
}
