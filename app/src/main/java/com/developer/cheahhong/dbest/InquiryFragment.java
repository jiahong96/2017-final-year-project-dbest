package com.developer.cheahhong.dbest;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.developer.cheahhong.dbest.LoginActivity.calledPersistance;
import static com.developer.cheahhong.dbest.LoginActivity.database;

public class InquiryFragment extends Fragment {

    private static final String ARG_PARAM_ID = "inquiryID";

    String inquiryID;

    private FirebaseAuth mAuth;
    FirebaseUser      user;
    DatabaseReference ref;
    static FirebaseRecyclerAdapter<Item, InquiryViewHolder> adapter;
    RecyclerView        mRecyclerView;
    LinearLayoutManager layoutManager;

    public InquiryFragment() {
        // Required empty public constructor
    }


    public static InquiryFragment newInstance(String param1) {
        InquiryFragment fragment = new InquiryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //parameters from messageActivity
        if (getArguments() != null) {
            inquiryID = getArguments().getString(ARG_PARAM_ID);
        }

        //initialize database
        if (!calledPersistance) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            calledPersistance = true;
        }

        ref = database.getReference("adinquiries").child(user.getUid()).child(inquiryID).child("items");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inquiry, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setMotionEventSplittingEnabled(false);

        displayInquiries();
        return view;
    }


    public static class InquiryViewHolder extends RecyclerView.ViewHolder {
        TextView txtItemName, txtQuantity, txtHiddenName, txtComment;
        ImageView      imgItem,hiddenImgView;
        ProgressBar    progressBar;
        CardView       cv;
        RelativeLayout rl;

        public InquiryViewHolder(View itemView) {
            super(itemView);
            txtHiddenName = (TextView) itemView.findViewById(R.id.bearingDisplay);
            txtItemName = (TextView) itemView.findViewById(R.id.code);
            txtQuantity = (TextView) itemView.findViewById(R.id.quantity);
            txtComment = (TextView) itemView.findViewById(R.id.extraComment);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            rl = (RelativeLayout) itemView.findViewById(R.id.bearingLayout);

            cv = (CardView) itemView.findViewById(R.id.formCardView);
            hiddenImgView = (ImageView) itemView.findViewById(R.id.hiddenImgView);
            imgItem = (ImageView) itemView.findViewById(R.id.imgView);
        }
    }


    private void displayInquiries() {

        adapter = new FirebaseRecyclerAdapter<Item, InquiryViewHolder>(
                Item.class,
                R.layout.item_history_view,
                InquiryViewHolder.class,
                ref
        ) {
            @Override
            protected void populateViewHolder(final InquiryViewHolder viewHolder, final Item model, int position) {

                viewHolder.txtHiddenName.setVisibility(View.GONE);

                if (model.getItemName() == null || model.getItemName().isEmpty()) {
                    viewHolder.txtItemName.setText("Item #" + (position + 1));
                } else {
                    viewHolder.txtItemName.setText(model.getItemName());
                }

                if (model.getExtraComment() == null || model.getExtraComment().isEmpty()) {
                    viewHolder.txtComment.setText("Comment: N/A");
                } else {
                    viewHolder.txtComment.setText("Comment: " + model.getExtraComment());
                }

                //compulsory
                viewHolder.txtQuantity.setText("Quantity: " + model.getQuantity());

                if (model.getImageUrl() != null && !model.getImageUrl().equals("")) {
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(model.getImageUrl())
                            .listener(new RequestListener<Drawable>() {

                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(viewHolder.imgItem);

                    viewHolder.imgItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (viewHolder.imgItem.getDrawable() != null) {
                                LayoutInflater factory = LayoutInflater.from(getActivity());
                                final View view = factory.inflate(R.layout.dialog_photo, null);

                                PhotoView photo = (PhotoView) view.findViewById(R.id.imgView);
                                Glide.with(getActivity())
                                        .load(model.getImageUrl())
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
                }else{
                    Glide.with(getActivity()).load(getImage("placeholder")).into(viewHolder.imgItem);
                }
            }

        };

        mRecyclerView.setAdapter(adapter);
    }

    public int getImage(String imageName) {

        int drawableResourceId = getActivity().getResources().getIdentifier(imageName, "drawable", getActivity().getPackageName());

        return drawableResourceId;
    }
}