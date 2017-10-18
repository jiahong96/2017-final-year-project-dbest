package com.example.cheahhong.dbest;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.cheahhong.dbest.LoginActivity.calledPersistance;
import static com.example.cheahhong.dbest.LoginActivity.database;

public class HandToolFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    FirebaseUser      user;
    DatabaseReference ref;
    Query             queryRef;
    static FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    LinearLayoutManager layoutManager;
    RecyclerView        mRecyclerView;

    public HandToolFragment() {
        // Required empty public constructor
    }

    public static HandToolFragment newInstance(String param1, String param2) {
        HandToolFragment fragment = new HandToolFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAuth = FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();

        if (!calledPersistance) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            calledPersistance = true;
        }

        queryRef = database.getReference("product").child("handtool").orderByChild("latestUpdated");
        queryRef.keepSynced(true);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("snapshot","dfasdfa");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_power_tool, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        displayProducts();
        return view;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView title,description,discount,time;
        Button    order;
        ImageView imgProduct,fireView,hiddenImgView;
        RelativeLayout rlContent;
        CardView       productCardView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);
            discount = (TextView) itemView.findViewById(R.id.promo);
            description = (TextView) itemView.findViewById(R.id.content);
            order = (Button) itemView.findViewById(R.id.btn_add);
            rlContent =(RelativeLayout) itemView.findViewById(R.id.relativeContent);
            imgProduct = (ImageView) itemView.findViewById(R.id.imgView);
            hiddenImgView = (ImageView) itemView.findViewById(R.id.hiddenImgView);
            fireView = (ImageView) itemView.findViewById(R.id.fireView);
            productCardView = (CardView) itemView.findViewById(R.id.productCardView);
        }
    }

    private void displayProducts() {

        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.product,
                ProductViewHolder.class,
                queryRef
        ) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Product model, final int position) {
                Log.d("fragment paint", "fragment paint");

                if(model.getListing().equals("true")){
                    viewHolder.productCardView.setVisibility(View.VISIBLE);

                    viewHolder.title.setText(model.getProductName());
                    viewHolder.description.setText(model.getDescription());
                    viewHolder.time.setText(DateFormat.format("MMM dd, HH:mm",
                            (model.getLatestUpdated()*-1)));

                    if (model.getDiscountPercent()>0) {
                        viewHolder.discount.setVisibility(View.VISIBLE);
                        viewHolder.discount.setText(model.getDiscountPercent()+"% OFF");
                        viewHolder.discount.setPaintFlags(viewHolder.discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        viewHolder.fireView.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.discount.setVisibility(View.GONE);
                        viewHolder.fireView.setVisibility(View.GONE);
                    }

                    if (model.getImageFileUrl() != null && !model.getImageFileUrl().equals("")) {
                        Glide.with(getActivity())
                                .load(model.getImageFileUrl())
                                .listener(new RequestListener<Drawable>() {

                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        //viewHolder.progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        //viewHolder.progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(viewHolder.imgProduct);
                    }

                    viewHolder.imgProduct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (viewHolder.imgProduct.getDrawable() != null) {
                                LayoutInflater factory = LayoutInflater.from(getActivity());
                                final View view = factory.inflate(R.layout.dialog_photo, null);

                                PhotoView photo = (PhotoView) view.findViewById(R.id.imgView);
                                Glide.with(getActivity())
                                        .load(model.getImageFileUrl())
                                        .into(photo);

                                final AlertDialog alertadd =
                                        new AlertDialog.Builder(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                                                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .setView(view)
                                                .create();

                                alertadd.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
                                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                                    }
                                });
                                alertadd.show();
                            }
                        }
                    });

                    viewHolder.order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Enquire Us?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String messageID = UUID.randomUUID().toString();

                                            String [] peoples = {user.getUid()};
                                            List<String> inquiryPeoples = new ArrayList<String>(Arrays.asList(peoples));

                                            ArrayList<Item> itemList = new ArrayList<>();
                                            itemList.add(new Item(model.getProductName(), "N/A", "product inquiry", model.getImageFileUrl(), "", ""));

                                            Long positiveNow = new Date().getTime();
                                            Long negativeNow = -(new Date().getTime());

                                            //create inquiry Object
                                            String inquiryName = model.getProductName();
                                            Inquiry inquiry = new Inquiry();
                                            inquiry.setInquiryName(inquiryName);
                                            inquiry.setInquiryTime(negativeNow);
                                            inquiry.setInquiryOwner(user.getUid());
                                            inquiry.setInquiryPeoples(inquiryPeoples);
                                            inquiry.setLastMessage(new ChatMessage(messageID,"I have sent you an inquiry",user.getUid(),negativeNow,user.getUid(),"inquiry",""));
                                            inquiry.setMsgUnreadCountForMobile(0);
                                            inquiry.setItems(itemList);
                                            inquiry.setStatus("none");
                                            inquiry.setUserStatus("Active");

                                            // push a new instance of inquiry to the Firebase database
                                            //refKey = ref.push().getKey();
                                            DatabaseReference refNormalInquiry,refAdInquiry,refNormalConv,refAdConv;
                                            refNormalInquiry = database.getReference("inquiries");
                                            refAdInquiry = database.getReference("adinquiries");

                                            String inquiryID = refNormalInquiry.push().getKey();
                                            inquiry.setInquiryID(inquiryID);

                                            refAdInquiry.child(user.getUid()).child(inquiryID).setValue(inquiry);
                                            //no negative time for normal inq
                                            inquiry.getLastMessage().setMessageTime(positiveNow);
                                            refNormalInquiry.child(inquiryID).setValue(inquiry);

                                            refNormalConv = database.getReference("conversations").child(inquiryID);
                                            refAdConv = database.getReference("adconversations").child(user.getUid()).child(inquiryID+"?"+inquiryName);
                                            String msgID = refNormalConv.push().getKey();
                                            refNormalConv.child(msgID)
                                                    .setValue(new ChatMessage(messageID,"I have sent you an inquiry",user.getUid(),positiveNow,user.getUid(),"inquiry","")
                                                    );
                                            refAdConv.child(msgID)
                                                    .setValue(new ChatMessage(messageID,"I have sent you an inquiry",user.getUid(),positiveNow,user.getUid(),"inquiry","")
                                                    );

                                            //start message Activity
                                            Intent message = new Intent(getActivity(),MessageActivity.class);
                                            message.putExtra("last_MessageID", inquiry.getLastMessage().getMessageID());
                                            message.putExtra("inquiry_Ref", inquiry.getInquiryID());
                                            message.putExtra("inquiry_Name", inquiry.getInquiryName());
                                            message.putExtra("unread_Count", inquiry.getMsgUnreadCountForMobile());
                                            startActivity(message);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }else{
                    viewHolder.productCardView.setVisibility(View.GONE);
                }
            }
        };
        mRecyclerView.setAdapter(adapter);
    }
}
