package com.developer.cheahhong.dbest;

/**
 * Created by CheahHong on 10/11/2017.
 */

public class Product{
    private String  productName  ="";
    private String  productType  ="";
    private String  description  ="";
    private String listing    = "false";
    private int    discountPercent = 0;
    private long   latestUpdated  = 0;
    private long   latestUpdated1 = 0;
    private String imageFileUrl   ="";

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

    public String getImageFileUrl() {
        return imageFileUrl;
    }

    public void setImageFileUrl(String imageFileUrl) {
        this.imageFileUrl = imageFileUrl;
    }

    public String getListing() {
        return listing;
    }

    public void setListing(String listing) {
        this.listing = listing;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public long getLatestUpdated() {
        return latestUpdated;
    }

    public void setLatestUpdated(long latestUpdated) {
        this.latestUpdated = latestUpdated;
    }

    public long getLatestUpdated1() {
        return latestUpdated1;
    }

    public void setLatestUpdated1(long latestUpdated1) {
        this.latestUpdated1 = latestUpdated1;
    }
}
