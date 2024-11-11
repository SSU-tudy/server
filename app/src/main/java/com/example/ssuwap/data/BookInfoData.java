package com.example.ssuwap.data;

public class BookInfoData {
    public String title;
    public String imageUrl;
    public String publisher;
    public String author;
    public String grade; // 학년
    public String semester; // 학기
    public String price;
    public String subject;
    public String description; // 부가설명

    // 기본 생성자 (Firebase에서 데이터 읽을 때 필요)
    public BookInfoData() {}

    // 생성자 (필드 초기화)
    public BookInfoData(String title, String imageUrl,String publisher, String author, String grade, String semester, String price, String subject, String description) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.publisher = publisher;
        this.author = author;
        this.grade = grade;
        this.semester = semester;
        this.price = price;
        this.subject = subject;
        this.description = description;
    }
}
