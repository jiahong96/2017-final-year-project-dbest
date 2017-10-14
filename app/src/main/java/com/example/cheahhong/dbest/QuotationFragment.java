package com.example.cheahhong.dbest;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;

import static com.example.cheahhong.dbest.LoginActivity.calledPersistance;
import static com.example.cheahhong.dbest.LoginActivity.database;


public class QuotationFragment extends Fragment {

    private static final String ARG_PARAM_ID = "inquiryID";
    private static final String ARG_PARAM_ID2 = "inquiryName";
    private int pos;

    private static final String        CONFIG_ENVIRONMENT   = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String        CLIENT_ID            ="ARpQlxBphXmsR7VjpT9JFO2Td1z0Zlz4B_MUqNB4mUMeKEKwXfMS27JGl9_e2kLdsnHck5wGNLNeQMVu";
    private static final int           REQUEST_CODE_PAYMENT = 1;

    private static final PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CLIENT_ID);

    String inquiryID, inquiryName;
    int count;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference ref,ref2;
    Query queryRef;
    static FirebaseRecyclerAdapter<Quotation, QuotationViewHolder> adapter;

    RecyclerView mRecyclerView;
    TextView quoteDefault;
    ImageView defaultPic;
    LinearLayoutManager layoutManager;
    Utility utility;

    private Context mContext;
    private Activity mActivity;
    private RelativeLayout mRelativeLayout;

    private PopupWindow mPopupWindow;

    public QuotationFragment() {
        // Required empty public constructor
    }

    public static QuotationFragment newInstance(String param1,String param2) {
        QuotationFragment fragment = new QuotationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_ID, param1);
        args.putString(ARG_PARAM_ID2,param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
        if (getArguments() != null) {
            inquiryID = getArguments().getString(ARG_PARAM_ID);
            inquiryName = getArguments().getString(ARG_PARAM_ID2);
        }

        if (!calledPersistance) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            calledPersistance = true;
        }

        ref = database.getReference("adinquiries").child(user.getUid()).child(inquiryID).child("quotations");
        ref2 = database.getReference("inquiries").child(inquiryID).child("quotations");

        queryRef = database.getReference("adinquiries").child(user.getUid()).child(inquiryID).child("quotations").orderByChild("time");
        queryRef.keepSynced(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotation, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        quoteDefault = (TextView) view.findViewById(R.id.quotationDefault);
        defaultPic = (ImageView) view.findViewById(R.id.defaultPic);

        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        count = 0;
        displayQuotations();

        return view;
    }

    public static class QuotationViewHolder extends RecyclerView.ViewHolder{
        TextView gTotal,rTotal,discount;
        RecyclerView quoteBearings;
        Button payment;
        ImageView imgExpandCollapse;
        RelativeLayout rl;

        public QuotationViewHolder(View itemView) {
            super(itemView);
            gTotal = (TextView) itemView.findViewById(R.id.gtotal);
            rTotal = (TextView) itemView.findViewById(R.id.rtotal);
            discount = (TextView) itemView.findViewById(R.id.discount);
            quoteBearings = (RecyclerView) itemView.findViewById(R.id.recyclerView2);
            imgExpandCollapse = (ImageView) itemView.findViewById(R.id.expand_collapse);
            payment = (Button) itemView.findViewById(R.id.payment);
            rl = (RelativeLayout) itemView.findViewById(R.id.quotationLayout);
        }

    }

    private void displayQuotations() {

        adapter = new FirebaseRecyclerAdapter<Quotation, QuotationViewHolder>(
                Quotation.class,
                R.layout.quotation,
                QuotationViewHolder.class,
                queryRef
        ) {
            @Override
            protected void populateViewHolder(final QuotationViewHolder viewHolder, final Quotation model, final int position) {
                if(model.getgTotal()> 0){
                    count++;
                    if(count==1){
                        quoteDefault.setVisibility(View.GONE);
                        defaultPic.setVisibility(View.GONE);
                    }

                    ItemQuoteAdapter qAdapter = new ItemQuoteAdapter(model.getQuoteItems());
                    LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity().getBaseContext());
                    viewHolder.quoteBearings.setHasFixedSize(true);
                    viewHolder.quoteBearings.setLayoutManager(layoutManager2);
                    viewHolder.quoteBearings.setItemAnimator(new DefaultItemAnimator());
                    viewHolder.quoteBearings.setAdapter(qAdapter);

                    hideQuotation(viewHolder);
                    if(model.getDiscountAmount()>0 && model.getDiscountPercent()>0){
                        viewHolder.discount.setVisibility(View.VISIBLE);
                        viewHolder.discount.setText(String.format("%.0f", model.getDiscountPercent())+"% OFF");
                        viewHolder.rTotal.setVisibility(View.VISIBLE);
                        viewHolder.rTotal.setTextColor(Color.parseColor("#424242"));
                        viewHolder.rTotal.setText("RM "+String.format("%.2f", model.getrTotal()));
                        viewHolder.rTotal.setPaintFlags(viewHolder.gTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        viewHolder.gTotal.setText("RM "+String.format("%.2f", model.getgTotal()) + "(#"+ (adapter.getItemCount()-position) + ")");
                    }else{
                        viewHolder.gTotal.setText("RM "+String.format("%.2f", model.getgTotal()) + "(#"+ (adapter.getItemCount()-position) + ")");
                        viewHolder.rTotal.setVisibility(View.GONE);
                        viewHolder.discount.setVisibility(View.GONE);
                    }

                    viewHolder.rl.setOnClickListener(new View.OnClickListener() {
                        int count = 1;
                        int rotationAngle=0;
                        @Override
                        public void onClick(View view) {
                            Log.d("expand/collapse", String.valueOf(viewHolder.getAdapterPosition()));
                            if (count > 0) {
                                Log.d("count", String.valueOf(count));
                                count++;
                                if (count % 2 == 0) {
                                    Log.d("hide", "ya");
                                    showQuotation(viewHolder);
                                    ObjectAnimator anim = ObjectAnimator.ofFloat(viewHolder.imgExpandCollapse, "rotation",rotationAngle, rotationAngle + 180);
                                    anim.setDuration(30);
                                    anim.start();
                                    rotationAngle += 180;
                                    rotationAngle = rotationAngle%360;
                                } else {
                                    Log.d("expand", "ya");
                                    hideQuotation(viewHolder);
                                    ObjectAnimator anim = ObjectAnimator.ofFloat(viewHolder.imgExpandCollapse, "rotation",rotationAngle, rotationAngle + 180);
                                    anim.setDuration(30);
                                    anim.start();
                                    rotationAngle += 180;
                                    rotationAngle = rotationAngle%360;
                                }
                            }
                        }
                    });

                if(model.getPayment() != null) {
                    if (model.getPayment().getPaymentState().equalsIgnoreCase("approved")) {

                        viewHolder.payment.setText("Show Receipt");

                        mContext = getActivity().getApplicationContext();
                        mActivity = getActivity();

                        mRelativeLayout = (RelativeLayout) getView().findViewById(R.id.receipt_layout);
                        viewHolder.payment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Receipt");

                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                View customView = inflater.inflate(R.layout.receipt_pop,null);
                                TextView rID, rAmount, rState, rDate;

                                rID = (TextView) customView.findViewById(R.id.receipt_pay_id);
                                rAmount = (TextView) customView.findViewById(R.id.receipt_pay_amount);
                                rState = (TextView) customView.findViewById(R.id.receipt_pay_state);
                                rDate = (TextView) customView.findViewById(R.id.receipt_pay_date);
                                //Button dismiss = (Button) customView.findViewById(R.id.button_dismiss);

                                rID.setText(model.getPayment().getPaymentID());
                                rAmount.setText(model.getPayment().getCurrency() + " " + model.getPayment().getAmount());
                                rState.setText(model.getPayment().getPaymentState());
                                rDate.setText(model.getPayment().getPaymentDate());


                                builder.setView(customView)
                                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });


                                builder.create().show();

                            }
                        });
                    }
                }else{
                    viewHolder.payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!utility.isNetworkAvailable(getActivity())){
                                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                            }else{
                                PayPalPayment makePayment = product(PayPalPayment.PAYMENT_INTENT_SALE, String.valueOf(model.getgTotal()));

                                Intent intent = new Intent(getActivity(), PaymentActivity.class);

                                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, makePayment);
                                pos = position;
                                Log.d("pos",String.valueOf(pos));
                                Log.d("position",String.valueOf(position));
                                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                            }
                        }
                    });
                }
            }else{
                    viewHolder.rl.setVisibility(View.GONE);
                    viewHolder.gTotal.setVisibility(View.GONE);
                    viewHolder.imgExpandCollapse.setVisibility(View.GONE);
            }

            }
            public void hideQuotation(QuotationViewHolder viewHolder){
                viewHolder.quoteBearings.setVisibility(View.GONE);
            }

            public void showQuotation(QuotationViewHolder viewHolder){
                viewHolder.quoteBearings.setVisibility(View.VISIBLE);
            }
        };
        mRecyclerView.setAdapter(adapter);
    }


    public PayPalPayment product(String paymentIntent,String total){
        return new PayPalPayment(new BigDecimal(total),"MYR","inquiry",paymentIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String payID, payDate, currency,payState;
//        int position = data.getIntExtra("quotation",0);
        int position = pos;
        Log.d("result",String.valueOf(position));
        double amount;

        if (requestCode == REQUEST_CODE_PAYMENT){
            if(resultCode == Activity.RESULT_OK){
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null){
                    try{
                        JSONObject confirmJSON = new JSONObject(confirm.toJSONObject().toString(4));
                        payDate = confirmJSON.getJSONObject("response").getString("create_time");
                        payID = confirmJSON.getJSONObject("response").getString("id");
                        payState = confirmJSON.getJSONObject("response").getString("state");
                        Log.d("Date",payDate);
                        Log.d("ID", payID);
                        Log.d("PayState", payState);

                        JSONObject paymentJSON = new JSONObject(confirm.getPayment().toJSONObject().toString());
                        currency = paymentJSON.getString("currency_code");
                        amount = Double.valueOf(paymentJSON.getString("amount"));


                        String messageID = database.getReference("adconversations").child(user.getUid()).child(inquiryID).push().getKey();

                        database.getReference("adconversations").child(user.getUid()).child(inquiryID+"?"+inquiryName).child(messageID)
                                .setValue(new ChatMessage
                                        (messageID,"I have paid MYR "+amount+" at Quotation "+(adapter.getItemCount()-pos),
                                                user.getUid(),new Date().getTime(),user.getUid(),"payment",""));

                        database.getReference("conversations").child(inquiryID).child(messageID)
                                .setValue(new ChatMessage
                                        (messageID,"I have paid MYR "+amount+" at Quotation "+(adapter.getItemCount()-pos),
                                                user.getUid(),new Date().getTime(),user.getUid(),"payment",""));;

                        database.getReference().child("adinquiries").child(user.getUid()).child(inquiryID).child("lastMessage")
                                .setValue(new ChatMessage
                                        (messageID,"I have paid MYR "+amount+" at Quotation "+(adapter.getItemCount()-pos),
                                                user.getUid(),new Date().getTime(),user.getUid(),"payment",""));

                        database.getReference().child("inquiries").child(inquiryID).child("lastMessage")
                                .setValue(new ChatMessage
                                        (messageID,"I have paid MYR "+amount+" at Quotation "+(adapter.getItemCount()-pos),
                                                user.getUid(),new Date().getTime(),user.getUid(),"payment",""));

                        Log.d("currency",currency);
                        Log.d("amount",String.valueOf(amount));

                        Payment payment = new Payment();
                        payment.setPaymentID(payID);
                        payment.setPaymentDate(payDate);
                        payment.setAmount(amount);
                        payment.setCurrency(currency);
                        payment.setPaymentState(payState);


                        ref.child(String.valueOf((adapter.getItemCount()-1) - pos)).child("payment").setValue(payment);
                        ref2.child(String.valueOf((adapter.getItemCount()-1) - pos)).child("payment").setValue(payment);
                        Toast.makeText(getActivity(),"Payment received successfully", Toast.LENGTH_LONG).show();

                    } catch ( JSONException e) {
                        Log.e("failure reason:", e.toString());
                        Toast.makeText(getActivity(),"Payment failed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
