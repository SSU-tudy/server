package com.example.ssuwap.data.book;

public class MainData {
    private String iv_book;
    private String tv_book;

    public MainData(String iv_book, String tv_book) {
        this.iv_book = iv_book;
        this.tv_book = tv_book;
    }
    public String getIv_book() {
        return iv_book;
    }

    public void setIv_book(String iv_book) {
        this.iv_book = iv_book;
    }

    public String getTv_book() {
        return tv_book;
    }

    public void setTv_book(String tv_book) {
        this.tv_book = tv_book;
    }
}
