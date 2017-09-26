package com.example.cheahhong.chatapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by User on 8/5/2017.
 */

public class Payment implements Parcelable{
    String paymentID;
    String paymentDate;
    String paymentState;
    double amount;
    String currency;

    public Payment(String paymentID, String paymentDate, String paymentState, double amount, String currency) {
        this.paymentID = paymentID;
        this.paymentDate = paymentDate;
        this.paymentState = paymentState;
        this.amount = amount;
        this.currency = currency;
    }

    public Payment() {
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.paymentID);
        dest.writeString(this.paymentDate);
        dest.writeString(this.paymentState);
        dest.writeDouble(this.amount);
        dest.writeString(this.currency);
    }

    protected Payment(Parcel in) {
        this.paymentID = in.readString();
        this.paymentDate = in.readString();
        this.paymentState = in.readString();
        this.amount = in.readDouble();
        this.currency = in.readString();
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel source) {
            return new Payment(source);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };
}


