package com.example.ssuwap.data.post;

public class PostInfo {
    public String imageUrl;
    public String description;

    public  PostInfo(){}

    public PostInfo(String imageUrl, String description){
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getImageUrl(){ return imageUrl;}
    public String getDescription(){ return description;}

    public void setImageUrl(String imageUrl){ this.imageUrl = imageUrl; }

    public void setDescription(String description) { this.description = description; }
}
