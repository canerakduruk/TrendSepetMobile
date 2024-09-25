package com.example.eticaretapp.datamodels;

import com.example.eticaretapp.databasemodels.AddressDbModel;
import com.google.firebase.firestore.Exclude;

import java.util.HashMap;

public class AddressModel {
    private transient String id;
    private String title;
    private String cityId;
    private String districtId;
    private String city;
    private String district;
    private String detail;
    private String ownerId;

    public AddressModel() {
    }
    public AddressModel(String title, String cityId, String districtId, String detail, String ownerId) {
        this.title = title;
        this.cityId = cityId;
        this.districtId = districtId;
        this.detail = detail;
        this.ownerId = ownerId;
    }

    // Getter ve Setter metotları

    @Exclude // Bu alan Firebase'e kaydedilmeyecek
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    @Exclude
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Exclude
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put(AddressDbModel.TITLE,title);
        map.put(AddressDbModel.CITY_ID,cityId);
        map.put(AddressDbModel.DISTRICT_ID,districtId);
        map.put(AddressDbModel.DETAIL,detail);
        map.put(AddressDbModel.OWNER_ID,ownerId);
        return map;
    }


}
