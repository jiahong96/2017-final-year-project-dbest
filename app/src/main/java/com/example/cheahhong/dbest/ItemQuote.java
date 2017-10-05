package com.example.cheahhong.dbest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CheahHong on 5/5/2017.
 */

public class ItemQuote implements Parcelable{

    String itemName     ="";
    double pricePerUnit =0.0;
    double totalPrice   =0.0;
    int    quantity     =0;

    public ItemQuote(String itemName, double pricePerUnit, double totalPrice, int quantity) {
        this.itemName = itemName;
        this.pricePerUnit = pricePerUnit;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }

    public ItemQuote(){}

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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

    protected ItemQuote(Parcel in) {
        itemName = in.readString();
        pricePerUnit = in.readDouble();
        totalPrice = in.readDouble();
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemName);
        parcel.writeDouble(pricePerUnit);
        parcel.writeDouble(totalPrice);
        parcel.writeInt(quantity);
    }

    public static final Creator<ItemQuote> CREATOR = new Creator<ItemQuote>() {
        @Override
        public ItemQuote createFromParcel(Parcel in) {
            return new ItemQuote(in);
        }

        @Override
        public ItemQuote[] newArray(int size) {
            return new ItemQuote[size];
        }
    };
}
