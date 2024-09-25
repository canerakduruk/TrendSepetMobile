package com.example.eticaretapp.datamodels;

import com.google.firebase.firestore.Exclude;

public class ShoppingCartModel {
    private String id;
    private String productId;
    private String customerId;

    // Getter ve Setter metotları

    public ShoppingCartModel(){

    }
    public ShoppingCartModel(String productId, String customerId) {
        this.productId = productId;
        this.customerId = customerId;
    }

    @Exclude
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getProductId(){
        return productId;
    }
    public void setProductId(String productId){
        this.productId = productId;
    }

    public String getCustomerId(){
        return customerId;
    }
    public void setCustomerId(String customerId){
        this.customerId = customerId;
    }

}
