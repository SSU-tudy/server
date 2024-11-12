package com.example.ssuwap.data.book;

public class BookInfo {
    public String title;
    public String imageUrl;
    public String author;
    public String publisher;
    public String description;
    public String tag_grade;
    public String tag_semester;
    public String tag_subject;
    public int price;

    public BookInfo() { }
    public BookInfo(String title, String imageUrl, String author, String publisher, String description, String tag_grade, String tag_semester, String tag_subject, int price) {
        this.author = author;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.publisher = publisher;
        this.tag_semester = tag_semester;
        this.tag_grade = tag_grade;
        this.tag_subject = tag_subject;
        this.title = title;
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
    public int getPrice() {
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

    public void setAuthor(String author) {
        this.author = author;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setPrice(int price) {
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
}
