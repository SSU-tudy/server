package com.example.assignment;

public class BookInfo {
    private String bookTitle;
    private String bookImage;
    private int bookPrice;
    private String bookAuthor;
    private String bookPubCom;
    private int uploadedTime;
    private String tag1;
    private String tag2;
    private String tag3;

    public String getBookAuthor() {
        return bookAuthor;
    }
    public int getBookPrice() {
        return bookPrice;
    }
    public String getBookImage() {
        return bookImage;
    }
    public String getBookPubCom() {
        return bookPubCom;
    }
    public int getUploadedTime() { return uploadedTime; }
    public String getBookTitle() {
        return bookTitle;
    }
    public String getTag1() {
        return tag1;
    }
    public String getTag2() {
        return tag2;
    }
    public String getTag3() {
        return tag3;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }
    public void setBookPrice(int bookPrice) {
        this.bookPrice = bookPrice;
    }
    public void setBookPubCom(String bookPubCom) {
        this.bookPubCom = bookPubCom;
    }
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }
    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }
    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }
    public void setUploadedTime(int uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public BookInfo() {}
}
