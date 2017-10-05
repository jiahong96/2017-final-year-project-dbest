package com.example.cheahhong.dbest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by CheahHong on 5/5/2017.
 */

public class Quotation implements Parcelable {

    ArrayList<ItemQuote> quoteBearings;
    double gTotal=0;
    Payment payment=null;

    public Quotation(ArrayList<ItemQuote> itemQuotes, double gTotal) {
        this.quoteBearings = itemQuotes;
        this.gTotal = gTotal;
    }

    public Quotation(ArrayList<ItemQuote> quoteBearings, double gTotal, Payment payment) {
        this.quoteBearings = quoteBearings;
        this.gTotal = gTotal;
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Quotation(){}

    public ArrayList<ItemQuote> getQuoteBearings() {
        return quoteBearings;
    }

    public void setQuoteBearings(ArrayList<ItemQuote> itemQuotes) {
        this.quoteBearings = itemQuotes;
    }

    public double getgTotal() {
        return gTotal;
    }

    public void setgTotal(double gTotal) {
        this.gTotal = gTotal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.quoteBearings);
        dest.writeDouble(this.gTotal);
        dest.writeParcelable(this.payment, flags);
    }

    protected Quotation(Parcel in) {
        this.quoteBearings = in.createTypedArrayList(ItemQuote.CREATOR);
        this.gTotal = in.readDouble();
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
