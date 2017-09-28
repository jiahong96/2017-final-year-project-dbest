package com.example.cheahhong.dbest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CheahHong on 4/24/2017.
 */

public class Bearing implements Parcelable{
    private String serialNo="";
    private String quantity="";
    private String extraComment="";
    private String imageDownloadUrl="";
    private String imageFileUri="";
    private String imageFileUrl="";

    public Bearing(String serialNo, String quantity, String extraComment,String imageUrl, String imageFileUri, String imageFileUrl) {
        this.serialNo = serialNo;
        this.quantity = quantity;
        //this.diameterI = diameterI;
        //this.diameterO = diameterO;
        this.extraComment = extraComment;
        this.imageDownloadUrl = imageUrl;
        this.imageFileUri = imageFileUri;
        this.imageFileUrl = imageFileUrl;
    }

    public Bearing(){
    }



    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getHeight() {
        return quantity;
    }

    public void setHeight(String quantity) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.serialNo);
        parcel.writeString(this.quantity);
        parcel.writeString(this.extraComment);
        parcel.writeString(this.imageDownloadUrl);
        parcel.writeString(this.imageFileUri);
        parcel.writeString(this.imageFileUrl);
    }

    protected Bearing(Parcel in) {
        serialNo = in.readString();
        quantity = in.readString();
        extraComment = in.readString();
        imageDownloadUrl = in.readString();
        imageFileUri = in.readString();
        imageFileUrl = in.readString();
    }

    public static final Creator<Bearing> CREATOR = new Creator<Bearing>() {
        @Override
        public Bearing createFromParcel(Parcel in) {
            return new Bearing(in);
        }

        @Override
        public Bearing[] newArray(int size) {
            return new Bearing[size];
        }
    };
}
