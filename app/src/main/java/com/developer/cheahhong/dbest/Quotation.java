package com.developer.cheahhong.dbest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by CheahHong on 5/5/2017.
 */

public class Quotation implements Parcelable {

    ArrayList<ItemQuote> quoteItems;
    double       discountPercent = 0.0;
    double       discountAmount  = 0.0;
    double       gTotal          = 0.0;
    double       rTotal          = 0.0;
    long    time       = 0;
    String  userStatus = null;
    Payment payment    =null;

    public Quotation(){}

    public ArrayList<ItemQuote> getQuoteItems() {
        return quoteItems;
    }

    public void setQuoteItems(ArrayList<ItemQuote> quoteItems) {
        this.quoteItems = quoteItems;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getgTotal() {
        return gTotal;
    }

    public void setgTotal(double gTotal) {
        this.gTotal = gTotal;
    }

    public double getrTotal() {
        return rTotal;
    }

    public void setrTotal(double rTotal) {
        this.rTotal = rTotal;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.quoteItems);
        dest.writeDouble(this.rTotal);
        dest.writeDouble(this.gTotal);
        dest.writeDouble(this.discountAmount);
        dest.writeDouble(this.discountPercent);
        dest.writeLong(this.time);
        dest.writeString(this.userStatus);
        dest.writeParcelable(this.payment, flags);
    }

    protected Quotation(Parcel in) {
        this.quoteItems = in.createTypedArrayList(ItemQuote.CREATOR);
        this.gTotal = in.readDouble();
        this.rTotal = in.readDouble();
        this.discountAmount = in.readDouble();
        this.discountPercent = in.readDouble();
        this.time = in.readLong();
        this.userStatus = in.readString();
        this.payment = in.readParcelable(Payment.class.getClassLoader());
    }

    public static final Creator<Quotation> CREATOR = new Creator<Quotation>() {
        @Override
        public Quotation createFromParcel(Parcel source) {
            return new Quotation(source);
        }

        @Override
        public Quotation[] newArray(int size) {
            return new Quotation[size];
        }
    };
}
