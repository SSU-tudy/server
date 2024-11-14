package com.example.ssuwap.data.post;

import android.os.Parcel;
import android.os.Parcelable;

public class CommentInfo implements Parcelable {
    public String commentID;
    public String author;
    public String content;
    public String commentImage;

    public CommentInfo() {}

    public CommentInfo(String commentID, String author, String content, String commentImage) {
        this.commentID = commentID;
        this.author = author;
        this.content = content;
        this.commentImage = commentImage;
    }

    protected CommentInfo(Parcel in) {
        commentID = in.readString();
        author = in.readString();
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
        parcel.writeString(commentID);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(commentImage);
    }

    // Getter and Setter methods
    public String getCommentID() { return commentID; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public String getCommentImage() {return commentImage;}

    public void setCommentID(String commentID) { this.commentID = commentID; }
    public void setAuthor(String author) { this.author = author; }
    public void setContent(String content) { this.content = content; }
    public void setCommentImage(String commentImage) {this.commentImage = commentImage;}
}
