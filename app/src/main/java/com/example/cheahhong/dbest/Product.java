package com.example.cheahhong.dbest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CheahHong on 10/11/2017.
 */

public class Product{
    private String  productName  ="";
    private String  productType  ="";
    private String  description  ="";
    private String promotion    = "false";
    private String  imageFileUrl ="";

    public Product(){
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public String getImageFileUrl() {
        return imageFileUrl;
    }

    public void setImageFileUrl(String imageFileUrl) {
        this.imageFileUrl = imageFileUrl;
    }
}
