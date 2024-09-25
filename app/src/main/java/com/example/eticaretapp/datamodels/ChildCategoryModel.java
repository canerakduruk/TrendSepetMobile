package com.example.eticaretapp.datamodels;

import com.google.firebase.firestore.Exclude;

public class ChildCategoryModel {
    private String id;
    private String name;
    private String parentId;

    public ChildCategoryModel() {

    }

    public ChildCategoryModel(String id, String name, String parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
