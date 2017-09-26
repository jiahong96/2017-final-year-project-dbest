package com.example.cheahhong.chatapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CheahHong on 5/5/2017.
 */

public class BearingQuote implements Parcelable{

    String serialNo="";
    double pricePerUnit=0.0;
    double totalPrice=0.0;
    int quantity=0;

    public BearingQuote(String serialNo, double pricePerUnit, double totalPrice, int quantity) {
        this.serialNo = serialNo;
        this.pricePerUnit = pricePerUnit;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }

    public BearingQuote(){}

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public double getTotal() {
        return totalPrice;
    }

    public void setTotal(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected BearingQuote(Parcel in) {
        serialNo = in.readString();
        pricePerUnit = in.readDouble();
        totalPrice = in.readDouble();
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(serialNo);
        parcel.writeDouble(pricePerUnit);
        parcel.writeDouble(totalPrice);
        parcel.writeInt(quantity);
    }

    public static final Creator<BearingQuote> CREATOR = new Creator<BearingQuote>() {
        @Override
        public BearingQuote createFromParcel(Parcel in) {
            return new BearingQuote(in);
        }

        @Override
        public BearingQuote[] newArray(int size) {
            return new BearingQuote[size];
        }
    };
}
