package com.example.ssuwap.data;

public class MainData {
    private int iv_book;
    private String tv_book;

    public MainData(int iv_book, String tv_book) {
        this.iv_book = iv_book;
        this.tv_book = tv_book;
    }
    public int gesdf(){return 3;}
    public int getIv_book() {
        return iv_book;
    }

    public void setIv_book(int iv_book) {
        this.iv_book = iv_book;
    }

    public String getTv_book() {
        return tv_book;
    }

    public void setTv_book(String tv_book) {
        this.tv_book = tv_book;
    }
}
