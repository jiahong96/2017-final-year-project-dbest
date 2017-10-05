package com.example.cheahhong.dbest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CheahHong on 4/24/2017.
 */

public class Item implements Parcelable{
    private String itemName         ="";
    private String quantity         ="";
    private String extraComment     ="";
    private String imageDownloadUrl ="";
    private String imageFileUri     ="";
    private String imageFileUrl     ="";

    public Item(String itemName, String quantity, String extraComment, String imageUrl, String imageFileUri, String imageFileUrl) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.extraComment = extraComment;
        this.imageDownloadUrl = imageUrl;
        this.imageFileUri = imageFileUri;
        this.imageFileUrl = imageFileUrl;
    }

    public Item(){
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getExtraComment() {
        return extraComment;
    }

    public void setExtraComment(String extraComment) {
        this.extraComment = extraComment;
    }

    public String getImageUrl() {
        return imageDownloadUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageDownloadUrl = imageUrl;
    }

    public String getImageFileUrl() {
        return imageFileUrl;
    }

    public void setImageFileUrl(String imageFileUrl) {
        this.imageFileUrl = imageFileUrl;
    }

    public String getImageFileUri() {
        return imageFileUri;
    }

    public void setImageFileUri(String imageFileUri) {
        this.imageFileUri = imageFileUri;
    }

    public String getImageDownloadUrl() {
        return imageDownloadUrl;
    }

    public void setImageDownloadUrl(String imageDownloadUrl) {
        this.imageDownloadUrl = imageDownloadUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.itemName);
        parcel.writeString(this.quantity);
        parcel.writeString(this.extraComment);
        parcel.writeString(this.imageDownloadUrl);
        parcel.writeString(this.imageFileUri);
        parcel.writeString(this.imageFileUrl);
    }

    protected Item(Parcel in) {
        itemName = in.readString();
        quantity = in.readString();
        extraComment = in.readString();
        imageDownloadUrl = in.readString();
        imageFileUri = in.readString();
        imageFileUrl = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
