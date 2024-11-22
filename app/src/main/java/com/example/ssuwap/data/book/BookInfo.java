package com.example.ssuwap.data.book;

import java.io.Serializable;
import java.util.Date;

public class BookInfo implements Serializable{
    public String title;
    public String imageUrl;
    public String author;
    public String publisher;
    public String description;
    public String tag_grade;
    public String tag_semester;
    public String tag_subject;
    public String price;
    public long time;

    public BookInfo() { }
    public BookInfo(String title, String imageUrl, String author, String publisher, String description, String tag_grade, String tag_semester, String tag_subject, String price, long time) {
        this.author = author;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.publisher = publisher;
        this.tag_semester = tag_semester;
        this.tag_grade = tag_grade;
        this.tag_subject = tag_subject;
        this.title = title;
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }
    public String getDescription() {
        return description;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getPublisher() {
        return publisher;
    }
    public String getPrice() {
        return price;
    }
    public String getTag_grade() {
        return tag_grade;
    }
    public String getTag_semester() {
        return tag_semester;
    }
    public String getTag_subject() {
        return tag_subject;
    }
    public String getTitle() {
        return title;
    }
    public long getTime() {
        return time;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public void setTag_grade(String tag_grade) {
        this.tag_grade = tag_grade;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public void setTag_semester(String tag_semester) {
        this.tag_semester = tag_semester;
    }
    public void setTag_subject(String tag_subject) {
        this.tag_subject = tag_subject;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setTime(long time) { this.time = time; }
}
