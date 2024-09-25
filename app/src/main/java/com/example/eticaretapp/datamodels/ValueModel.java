package com.example.eticaretapp.datamodels;

import com.google.firebase.firestore.Exclude;

public class ValueModel {
    private String id;
    private String name;
    private String parentId;

    public ValueModel() {
        // Boş constructor
    }

    public ValueModel(String name, String parentId) {
        this.name = name;
        this.parentId = parentId;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }


}
