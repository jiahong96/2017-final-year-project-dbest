package com.example.cheahhong.dbest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CheahHong on 4/24/2017.
 */

public class Bearing implements Parcelable{
    private String serialNo="";
    private String height="";
    private String diameterI="";
    private String diameterO="";
    private String extraComment="";
    private String imageDownloadUrl="";
    private String imageFileUri="";
    private String imageFileUrl="";

    public Bearing(String serialNo, String height, String diameterI, String diameterO, String extraComment,String imageUrl, String imageFileUri, String imageFileUrl) {
        this.serialNo = serialNo;
        this.height = height;
        this.diameterI = diameterI;
        this.diameterO = diameterO;
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
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDiameterI() {
        return diameterI;
    }

    public void setDiameterI(String diameterI) {
        this.diameterI = diameterI;
    }

    public String getDiameterO() {
        return diameterO;
    }

    public void setDiameterO(String diameterO) {
        this.diameterO = diameterO;
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
        parcel.writeString(this.height);
        parcel.writeString(this.diameterI);
        parcel.writeString(this.diameterO);
        parcel.writeString(this.extraComment);
        parcel.writeString(this.imageDownloadUrl);
        parcel.writeString(this.imageFileUri);
        parcel.writeString(this.imageFileUrl);
    }

    protected Bearing(Parcel in) {
        serialNo = in.readString();
        height = in.readString();
        diameterI = in.readString();
        diameterO = in.readString();
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
