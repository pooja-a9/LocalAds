package com.pooja.anche.localads.model;

import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sweetie on 12/28/2017.
 */
@IgnoreExtraProperties
public class LocalAd {

    public String title;
    public String desc;
    public String phone;
    public String address;
    public String email;
    public String type;
    public String userName;
    public String userId;
    public HashMap<String, ImageInfo> images;


    public LocalAd() {

    }

    public LocalAd(String title, String desc, String phone, String address, String email, String type, String userName, String userID) {
        this.title = title;
        this.desc = desc;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.type = type;
        this.userName = userName;
        this.userId = userID;
        images = new HashMap<>();

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public HashMap<String, ImageInfo> getImages() {
        return images;
    }

    public void setImages(HashMap<String, ImageInfo> images) {
        this.images = images;
    }

    public ArrayList<Uri> getLocalAdImageList() {
        ArrayList<Uri> result = new ArrayList<>();
        if (this.images != null) {
            for (Map.Entry<String, ImageInfo> entry : this.images.entrySet()) {
                result.add(Uri.parse(entry.getValue().getUrl()));
            }
        }

        return result;
    }

}
