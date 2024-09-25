package com.example.eticaretapp.datamodels;

import android.net.Uri;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;

public class ProductModel {
    private String id;
    private String name;
    private String description;
    private String category;
    private HashMap<String,String> property;
    private String content;
    private String price;
    private String kdvRate;
    private String imageUrl;
    private String ownerId;

    public ProductModel() {
        // Boş constructor
    }

    public ProductModel(String name, String description,
                        String category, HashMap<String,String> property,
                        String price, String kdvRate, String imageUrl, String ownerId) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.property = property;
        this.price = price;
        this.kdvRate = kdvRate;
        this.imageUrl = imageUrl;
        this.ownerId = ownerId;
    }

    // Getter ve Setter metodları
    @Exclude // Bu alan Firebase'e kaydedilmeyecek
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public HashMap<String, String> getProperty() {
        return property;
    }

    public void setProperty(HashMap<String,String> property) {
        this.property = property;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getKdvRate() {
        return kdvRate;
    }

    public void setKdvRate(String kdvRate) {
        this.kdvRate = kdvRate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }



}
