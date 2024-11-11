package com.example.ssuwap.data;

public class SellingListData {
    private String tv_title;
    private int tv_chatnum;
    private int tv_price;
    private String iv_book;
    private int tv_time;

    public SellingListData() {}

    // getter
    public String getIv_book() {
        return iv_book;
    }
    public int getTv_chatnum() {
        return tv_chatnum;
    }
    public int getTv_price() {
        return tv_price;
    }
    public int getTv_time() {
        return tv_time;
    }
    public String getTv_title() {
        return tv_title;
    }

    // setter
    public void setIv_book(String iv_book) {  // 오타 수정
        this.iv_book = iv_book;
    }
    public void setTv_chatnum(int tv_chatnum) {
        this.tv_chatnum = tv_chatnum;
    }
    public void setTv_price(int tv_price) {
        this.tv_price = tv_price;
    }
    public void setTv_time(int tv_time) {
        this.tv_time = tv_time;
    }
    public void setTv_title(String tv_title) {
        this.tv_title = tv_title;
    }
}
