package com.example.cheahhong.dbest;

import android.animation.ObjectAnimator;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by CheahHong on 4/30/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder>{

    private ArrayList<Item> bearingsList = new ArrayList<>();
    private static ClickListener clickListener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_form, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout form;
        FrameLayout frame;
        CardView card;
        int count, rotationAngle;
        TextView itemDisplay;
        ImageView itemImage,imgTakePhoto,expandIcon;
        EditText editTxtCode,editTxtQuantity,editTxtComment;
        TextInputLayout txtLayoutCode,txtLayoutHeight,txtLayoutDiameterI,txtLayoutDiameterO, txtLayoutComment;


        public MyViewHolder(View view) {
            super(view);
            rotationAngle=0;
            count = 1;
            form = (RelativeLayout) view.findViewById(R.id.bearingLayout);
            frame = (FrameLayout) view.findViewById(R.id.frameLayout);
            card = (CardView)view.findViewById(R.id.formCardView);

            itemDisplay = (TextView)view.findViewById(R.id.bearingDisplay);

            itemImage = (ImageView)view.findViewById(R.id.imgView);
            imgTakePhoto = (ImageView)view.findViewById(R.id.imgIcon);
            //expandIcon = (ImageView)view.findViewById(R.id.expand_collapse);

            editTxtCode = (EditText)view.findViewById(R.id.code);
            editTxtQuantity = (EditText)view.findViewById(R.id.quantity);
            editTxtComment = (EditText)view.findViewById(R.id.extraComment);

            txtLayoutCode = (TextInputLayout) view.findViewById(R.id.codeLayout);
            txtLayoutHeight = (TextInputLayout) view.findViewById(R.id.quantityLayout);
            txtLayoutComment = (TextInputLayout) view.findViewById(R.id.commentLayout);

            //rotateIcon();

            //take photo
            imgTakePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onTakeImgClick(bearingsList.get(getAdapterPosition()),view,getAdapterPosition());
                }
            });

            //select photo
            itemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onSelectImgClick(bearingsList.get(getAdapterPosition()),view,getAdapterPosition());
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    clickListener.onViewLongClick(view,getAdapterPosition());
                    return true;
                }
            });

            //rotate expand ICON and hide/expand views
//            form.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(count>0){
//                        Log.d("count", String.valueOf(count));
//                        count++;
//                        if(count%2==0){
//                            Log.d("hide","ya");
//                            rotateIcon();
//                            hideBearingForm();
//                        }else{
//                            Log.d("expand","ya");
//                            rotateIcon();
//                            expandBearingForm();
//                        }
//                    }
//                }
//            });
        }


        public void rotateIcon(){
            ObjectAnimator anim = ObjectAnimator.ofFloat(expandIcon, "rotation",rotationAngle, rotationAngle + 180);
            anim.setDuration(30);
            anim.start();
            rotationAngle += 180;
            rotationAngle = rotationAngle%360;
            Log.d("expand", String.valueOf(rotationAngle));
        }

        public void hideBearingForm(){
            card.setVisibility(View.GONE);
            frame.setVisibility(View.GONE);
            itemImage.setVisibility(View.GONE);
            imgTakePhoto.setVisibility(View.GONE);
            editTxtQuantity.setVisibility(View.GONE);
            editTxtCode.setVisibility(View.GONE);
            //editTxtDiameterI.setVisibility(View.GONE);
            //editTxtDiameterO.setVisibility(View.GONE);
            editTxtComment.setVisibility(View.GONE);
            txtLayoutCode.setVisibility(View.GONE);
            //txtLayoutDiameterI.setVisibility(View.GONE);
            //txtLayoutDiameterO.setVisibility(View.GONE);
            txtLayoutComment.setVisibility(View.GONE);
            txtLayoutHeight.setVisibility(View.GONE);

            itemDisplay.setText("Item #"+(getAdapterPosition()+1));
            itemDisplay.setVisibility(View.VISIBLE);
        }

        public void expandBearingForm(){
            card.setVisibility(View.VISIBLE);
            frame.setVisibility(View.VISIBLE);
            itemImage.setVisibility(View.VISIBLE);
            imgTakePhoto.setVisibility(View.VISIBLE);
            editTxtQuantity.setVisibility(View.VISIBLE);
            editTxtCode.setVisibility(View.VISIBLE);
            editTxtComment.setVisibility(View.VISIBLE);
            txtLayoutCode.setVisibility(View.VISIBLE);
            txtLayoutComment.setVisibility(View.VISIBLE);
            txtLayoutHeight.setVisibility(View.VISIBLE);

            itemDisplay.setVisibility(View.GONE);
        }
    }


    public ItemAdapter(ArrayList<Item> bearingsList) {
        this.bearingsList = bearingsList;
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        ItemAdapter.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemDisplay.setText("");
        holder.itemDisplay.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return bearingsList.size();
    }

    public interface ClickListener {
        void onTakeImgClick(Item item, View v, int position);
        void onSelectImgClick(Item item, View v, int position);
        void onViewLongClick(View v, int position);
    }
}
