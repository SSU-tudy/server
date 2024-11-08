package com.example.ssuwap;

public class SellingListData{
    private String tv_title;
    private int tv_chatnum;
    private int tv_price;
    private int iv_book;
    private String tv_book;
    private int tv_time;

    public int getTv_time() {
        return tv_time;
    }

    public void setTv_time(int tv_time) {
        this.tv_time = tv_time;
    }

    public SellingListData(String tv_title, String tv_book, int iv_book, int tv_price, int tv_chatnum, int tv_time) {
        this.tv_title = tv_title;
        this.tv_book = tv_book;
        this.iv_book = iv_book;
        this.tv_price = tv_price;
        this.tv_chatnum = tv_chatnum;
        this.tv_time = tv_time;
    }

    public String getTv_title() {
        return tv_title;
    }

    public void setTv_title(String tv_title) {
        this.tv_title = tv_title;
    }

    public String getTv_book() {
        return tv_book;
    }

    public void setTv_book(String tv_book) {
        this.tv_book = tv_book;
    }

    public int getTv_price() {
        return tv_price;
    }

    public void setTv_price(int tv_price) {
        this.tv_price = tv_price;
    }

    public int getTv_chatnum() {
        return tv_chatnum;
    }

    public void setTv_chatnum(int tv_chatnum) {
        this.tv_chatnum = tv_chatnum;
    }

    public int getIv_book() {
        return iv_book;
    }

    public void setIv_book(int iv_book) {
        this.iv_book = iv_book;
    }
}
