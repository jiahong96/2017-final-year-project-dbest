package com.example.cheahhong.dbest;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.example.cheahhong.dbest.LoginActivity.calledPersistance;
import static com.example.cheahhong.dbest.LoginActivity.database;

public class PaintFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String       mParam1;
    private String       mParam2;
    private FirebaseAuth mAuth;
    FirebaseUser      user;
    DatabaseReference ref;
    Query             queryRef;
    static FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    LinearLayoutManager layoutManager;
    RecyclerView mRecyclerView;

    public PaintFragment() {
        // Required empty public constructor
    }

    public static PaintFragment newInstance(String param1, String param2) {
        PaintFragment fragment = new PaintFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("fragmentpaint","oncreate");

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

        ref = database.getReference("promo");
        ref.addChildEventListener(new ChildEventListener() {
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
        Log.d("fragmentpaint","yolo");
        View view = inflater.inflate(R.layout.fragment_paint, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        displayProducts();
        return view;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView title,description,discount;
        Button         order;
        ImageView      imgProduct;
        RelativeLayout rlContent;
        CardView productCardView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            discount = (TextView) itemView.findViewById(R.id.promo);
            description = (TextView) itemView.findViewById(R.id.content);
            order = (Button) itemView.findViewById(R.id.btn_add);
            rlContent =(RelativeLayout) itemView.findViewById(R.id.relativeContent);
            imgProduct = (ImageView) itemView.findViewById(R.id.imgView);
            productCardView = (CardView) itemView.findViewById(R.id.productCardView);
        }
    }

    private void displayProducts() {

        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.product,
                ProductViewHolder.class,
                ref
        ) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Product model, final int position) {
                Log.d("fragment paint", "fragment paint");
                viewHolder.title.setText(model.getProductName());
                viewHolder.description.setText(model.getDescription());
                if (model.getPromotion().equals("true")) {
                    viewHolder.discount.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.discount.setVisibility(View.GONE);
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
            }
        };
        mRecyclerView.setAdapter(adapter);
    }

}
