package com.example.samuraitravel.entity;
public class ReviewDto {
    private String content;
    private Integer rating;

    // ゲッターとセッター
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
