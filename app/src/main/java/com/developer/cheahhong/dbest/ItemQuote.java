package com.developer.cheahhong.dbest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CheahHong on 5/5/2017.
 */

public class ItemQuote implements Parcelable{

    String itemName     ="";
    double pricePerUnit =0.0;
    double total   =0.0;
    int    quantity     =0;

    public ItemQuote(String itemName, double pricePerUnit, double total, int quantity) {
        this.itemName = itemName;
        this.pricePerUnit = pricePerUnit;
        this.total = total;
        this.quantity = quantity;
    }

    public ItemQuote(){}

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
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
        total = in.readDouble();
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemName);
        parcel.writeDouble(pricePerUnit);
        parcel.writeDouble(total);
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
