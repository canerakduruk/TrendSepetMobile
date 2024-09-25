package com.example.eticaretapp.datamodels;

import com.google.firebase.firestore.Exclude;

public class DistrictModel {
    private transient String id;
    private String name;
    private String cityId;

    public DistrictModel() {

    }

    public DistrictModel(String id, String name, String cityId) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
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

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

}
