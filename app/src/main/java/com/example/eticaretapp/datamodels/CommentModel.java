package com.example.eticaretapp.datamodels;

import com.google.firebase.firestore.Exclude;

public class CommentModel {
    private String id;
    private String senderId;
    private String productId;
    private String orderId;
    private String stars;
    private String title;
    private String comment;
    private String date;
    private String imageUrl;
    private String status;

    public CommentModel() {
    }

    // Dolu yapıcı (constructor)
    public CommentModel(String senderId, String productId, String orderId,
                        String stars, String title, String comment, String date,
                        String imageUrl, String status) {
        this.senderId = senderId;
        this.productId = productId;
        this.orderId = orderId;
        this.stars = stars;
        this.title = title;
        this.comment = comment;
        this.date = date;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    // Getter ve Setter metotları
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
