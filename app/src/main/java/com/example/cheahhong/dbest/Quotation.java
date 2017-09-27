package com.example.cheahhong.dbest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by CheahHong on 5/5/2017.
 */

public class Quotation implements Parcelable {

    ArrayList<BearingQuote> quoteBearings;
    double gTotal=0;
    Payment payment=null;

    public Quotation(ArrayList<BearingQuote> bearingQuotes, double gTotal) {
        this.quoteBearings = bearingQuotes;
        this.gTotal = gTotal;
    }

    public Quotation(ArrayList<BearingQuote> quoteBearings, double gTotal, Payment payment) {
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

    public ArrayList<BearingQuote> getQuoteBearings() {
        return quoteBearings;
    }

    public void setQuoteBearings(ArrayList<BearingQuote> bearingQuotes) {
        this.quoteBearings = bearingQuotes;
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
        this.quoteBearings = in.createTypedArrayList(BearingQuote.CREATOR);
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
