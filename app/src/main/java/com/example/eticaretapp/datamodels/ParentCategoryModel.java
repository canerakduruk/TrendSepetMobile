package com.example.eticaretapp.datamodels;

import com.google.firebase.firestore.Exclude;

public class ParentCategoryModel {
    private transient String id;
    private String name;

    public ParentCategoryModel() {

    }

    public ParentCategoryModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    //Getter ve Setterlar
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

}
