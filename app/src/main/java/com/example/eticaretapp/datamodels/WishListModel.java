package com.example.eticaretapp.datamodels;

import com.google.firebase.firestore.Exclude;

public class WishListModel {
    private String id;
    private String productId;
    private String customerId;

    public WishListModel(){

    }

    public WishListModel(String productId, String customerId) {
        this.productId = productId;
        this.customerId = customerId;
    }

    // Getter ve Setter metotları


    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
