package com.example.eticaretapp.datamodels;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.HashMap;


public class OrderModel {
    private String id;
    private HashMap<String, String> productInfo;
    private String orderAddress;
    private String customerId;
    private String sellerId;
    private Timestamp date;  // Tarih alanını Timestamp olarak değiştiriyoruz
    private String status;

    // Parametresiz Yapıcı Metod
    public OrderModel() {
    }

    // Parametreli Yapıcı Metod
    public OrderModel(HashMap<String, String> productInfo, String orderAddress, String customerId, String sellerId, Timestamp date, String status) {
        this.productInfo = productInfo;
        this.orderAddress = orderAddress;
        this.customerId = customerId;
        this.sellerId = sellerId;
        this.date = date;
        this.status = status;
    }

    // Getter ve Setter Metodları

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, String> getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(HashMap<String, String> productInfo) {
        this.productInfo = productInfo;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

