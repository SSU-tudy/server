// BookInfo.java
package com.example.ssuwap.data.book;

import java.io.Serializable;

public class BookInfo implements Serializable {
    private String title;
    private String imageUrl;
    private String author;
    private String publisher;
    private String description;
    private String tag_grade;
    private String tag_semester;
    private String tag_subject;
    private String price;
    private long time;
    private boolean isSold; // 판매 상태 변수 추가
    private String uploaderId; // 업로더의 UID 추가 (판매내역 조회를 위해)

    public BookInfo() { }

    public BookInfo(String title, String imageUrl, String author, String publisher, String description,
                    String tag_grade, String tag_semester, String tag_subject, String price, long time,
                    boolean isSold, String uploaderId) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.tag_grade = tag_grade;
        this.tag_semester = tag_semester;
        this.tag_subject = tag_subject;
        this.price = price;
        this.time = time;
        this.isSold = isSold;
        this.uploaderId = uploaderId;
    }

    // Getter 및 Setter 메서드

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTag_grade() { return tag_grade; }
    public void setTag_grade(String tag_grade) { this.tag_grade = tag_grade; }

    public String getTag_semester() { return tag_semester; }
    public void setTag_semester(String tag_semester) { this.tag_semester = tag_semester; }

    public String getTag_subject() { return tag_subject; }
    public void setTag_subject(String tag_subject) { this.tag_subject = tag_subject; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }

    public boolean isSold() { return isSold; }
    public void setSold(boolean sold) { isSold = sold; }

    public String getUploaderId() { return uploaderId; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }
}