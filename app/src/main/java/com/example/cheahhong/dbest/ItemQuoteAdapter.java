package com.example.cheahhong.dbest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by CheahHong on 4/30/2017.
 */

public class ItemQuoteAdapter extends RecyclerView.Adapter<ItemQuoteAdapter.MyViewHolder>{

    private ArrayList<ItemQuote> quoteItemList = new ArrayList<>();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);

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

    public ItemQuoteAdapter(ArrayList<ItemQuote> itemList) {
        this.quoteItemList = itemList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ItemQuote quoteBearing = quoteItemList.get(position);
        if(quoteBearing.getItemName()== null || quoteBearing.getItemName().isEmpty()){
            holder.serialNo.setText("Item #"+(position+1));
        }else{
            holder.serialNo.setText(quoteBearing.getItemName());
        }

        holder.quantity.setText("Quantity: "+String.valueOf(quoteBearing.getQuantity()));
        holder.total.setText("Total: RM"+String.format("%.2f", quoteBearing.getTotal()));
        holder.pricePerUnit.setText("PricePerUnit: RM"+String.format("%.2f", quoteBearing.getPricePerUnit()));
    }

    @Override
    public int getItemCount() {
        return quoteItemList.size();
    }

}
