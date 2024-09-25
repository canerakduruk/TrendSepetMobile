package com.example.eticaretapp.datamodels;


import android.net.Uri;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;

public class UserModel {
    private String id;
    private String profileImageUrl;
    private String email;
    private String name;
    private String surname;
    private String phoneNumber;
    private String gender;


    // Boş constructor Firestore için gereklidir.
    public UserModel() {
    }

    public UserModel(String profileImageUrl, String email , String name, String surname, String gender, String phoneNumber) {
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.surname = surname;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("profileImageUrl", profileImageUrl);
        map.put("email", email);
        map.put("name", name);
        map.put("surname", surname);
        map.put("gender", gender);
        map.put("phoneNumber", phoneNumber);
        return map;
    }

    // Getter ve Setter'lar
    @Exclude // Bu alan Firebase'e kaydedilmeyecek
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
