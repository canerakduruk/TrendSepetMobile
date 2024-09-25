package com.example.eticaretapp.datamodels;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class ChatModel {
    private String id;
    private String senderId;
    private String receiverId;
    private String message;
    private Timestamp dateTime;

    // Boş yapıcı (default constructor)
    public ChatModel() {
    }

    // Parametreli yapıcı (constructor)
    public ChatModel(String senderId, String receiverId, String message, Timestamp dateTime) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = dateTime;
    }

    // Getter ve Setter metodları
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

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }
}