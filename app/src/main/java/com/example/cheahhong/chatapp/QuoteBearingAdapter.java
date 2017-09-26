package com.example.cheahhong.chatapp;

import android.animation.ObjectAnimator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CheahHong on 4/30/2017.
 */

public class QuoteBearingAdapter extends RecyclerView.Adapter<QuoteBearingAdapter.MyViewHolder>{

    private ArrayList<BearingQuote> quoteBearingsList = new ArrayList<>();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bearingquote, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView serialNo,quantity,pricePerUnit,total;

        public MyViewHolder(View view) {
            super(view);

            serialNo = (TextView)view.findViewById(R.id.serialNo);
            total = (TextView)view.findViewById(R.id.totalprice);
            quantity = (TextView)view.findViewById(R.id.quantity);
            pricePerUnit = (TextView)view.findViewById(R.id.price);
        }
    }

    public QuoteBearingAdapter(ArrayList<BearingQuote> bearingsList) {
        this.quoteBearingsList = bearingsList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BearingQuote quoteBearing = quoteBearingsList.get(position);
        if(quoteBearing.getSerialNo()== null || quoteBearing.getSerialNo().isEmpty()){
            holder.serialNo.setText("Bearing #"+(position+1));
        }else{
            holder.serialNo.setText(quoteBearing.getSerialNo());
        }

        holder.quantity.setText("Quantity: "+String.valueOf(quoteBearing.getQuantity()));
        holder.total.setText("Total: RM"+String.format("%.2f", quoteBearing.getTotal()));
        holder.pricePerUnit.setText("PricePerUnit: RM"+String.format("%.2f", quoteBearing.getPricePerUnit()));
    }

    @Override
    public int getItemCount() {
        return quoteBearingsList.size();
    }

}
