package com.example.cheahhong.chatapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.cheahhong.chatapp.LoginActivity.calledPersistance;
import static com.example.cheahhong.chatapp.LoginActivity.database;

public class MainActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    static String[] fontItems;
    static String[] menuItems;
    static final int NotificationID = 003;
    static FirebaseRecyclerAdapter<Inquiry, MainActivity.MessageViewHolder> adapter;
    String inquiryID, inquiryName, inquiryOwner;
    DatabaseReference refNormalInquiry,refAdInquiry,refNormalConv,refAdConv;
    Query             queryRef;
    FirebaseUser      user;
    ChatMessage       msg;
    RecyclerView      mRecyclerView;
    TextView          quoteDefault;
    ImageView         defaultPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize menu tools
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        menuItems = getResources().getStringArray(R.array.menu);
        fontItems = getResources().getStringArray(R.array.font);

        quoteDefault = (TextView) findViewById(R.id.quotationDefault);
        defaultPic = (ImageView) findViewById(R.id.defaultPic);

        //initialize auth and update UI
        mAuth = FirebaseAuth.getInstance();
        updateUI();

        //initialize database
        if (!calledPersistance) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            calledPersistance = true;
        }

        //initialize notification
        if(!isMyServiceRunning(BackgroundService.class)){
            startService(new Intent(this,BackgroundService.class));
        }

        //initialize recyclerview
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerViewMain);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //initialize firebase database references
        queryRef = database.getReference("adinquiries").child(user.getUid()).orderByChild("lastMessage/messageTime");
        queryRef.keepSynced(true);
        refNormalInquiry = database.getReference("inquiries");
        refAdInquiry = database.getReference("adinquiries");
        refAdInquiry.keepSynced(true);
        Log.d("database Reference",queryRef.toString());

        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("inquiryAdded","added one new inquiry-"+dataSnapshot.getKey());
                quoteDefault.setVisibility(View.GONE);
                defaultPic.setVisibility(View.GONE);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("inquiryChanged","changed one inquiry-"+dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("inquiryRemoved","removed one inquiry-"+dataSnapshot);
                Log.d("inquiryRemoved","removed one inquiry-"+dataSnapshot.child("inquiryName").getValue());
                Toast.makeText(MainActivity.this, "removed inquiry - "+dataSnapshot.child("inquiryName").getValue(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        displayInquiries();

        //initialize and listen to add inquiry button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inquiry = new Intent(MainActivity.this,InquiryActivity.class);
                startActivityForResult(inquiry, 1);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationID);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Log.d("logout","yes");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure you want to Logout?")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mAuth.signOut();
                            updateUI();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog logoutDialog = builder.create();
            logoutDialog.show();

            return true;
        }else if(id == R.id.action_font){
            fontDialog();
        }else if(id == R.id.action_feedback){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/ZXEum8"));
            startActivity(browserIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    void fontDialog(){
        SharedPreferences sp=getSharedPreferences("FontSize", 0);
        final SharedPreferences.Editor Ed=sp.edit();

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View msgLayout= inflater.inflate(R.layout.message, null, false);
        final TextView txtView = (TextView)msgLayout.findViewById(R.id.message_text);
        int defaultSize = 1;

        if(sp.getString("font",null)!=null){
            if(sp.getString("font",null).equals("small")){
                defaultSize = 0;
            }else if(sp.getString("font",null).equals("medium")){
                defaultSize = 1;
            }else if(sp.getString("font",null).equals("large")){
                defaultSize = 2;
            }
        }

        AlertDialog.Builder alt_font = new AlertDialog.Builder(this);
        //alt_bld.setIcon(R.drawable.icon);
        alt_font.setTitle("Font Size for Chat");
        alt_font.setSingleChoiceItems(fontItems, defaultSize, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item==0){
                    Log.d("fontSize",fontItems[item]);
                    Ed.putString("font","small");
                    Ed.commit();
                }else if(item==1){
                    Log.d("fontSize",fontItems[item]);
                    Ed.putString("font","medium");
                    Ed.commit();
                }else if(item==2){
                    Log.d("fontSize",fontItems[item]);
                    Ed.putString("font","large");
                    Ed.commit();
                }
                dialog.dismiss();
            }
        });
        AlertDialog alert = alt_font.create();
        alert.show();
    }

    void updateUI(){
        if(mAuth.getCurrentUser()!=null){
            user= mAuth.getCurrentUser();
            Log.d("userrrrr",user.getUid());
        } else{
            finish();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("Logout","Logout");
            startActivity(intent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void startMessageActivity(int position){
        Intent message = new Intent(MainActivity.this,MessageActivity.class);
        if(adapter.getItem(position).getLastMessage()!=null){
            message.putExtra("last_MessageID", adapter.getItem(position).getLastMessage().getMessageID());
        }
        message.putExtra("inquiry_Ref", adapter.getItem(position).getInquiryID());
        message.putExtra("inquiry_Name", adapter.getItem(position).getInquiryName());
        message.putExtra("unread_Count", adapter.getItem(position).getMsgUnreadCountForMobile());
        startActivity(message);
    }

    private void displayInquiries() {
        adapter = new FirebaseRecyclerAdapter<Inquiry, MainActivity.MessageViewHolder> (
                Inquiry.class,
                R.layout.inquiries,
                MainActivity.MessageViewHolder.class,
                queryRef
        )
        {
            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, Inquiry model, final int position) {
                msg = new ChatMessage();
                viewHolder.notification.setVisibility(View.GONE);
                String previewText = "";
                if(model.getLastMessage()!=null && model.getLastMessage().getMessageText()!=null){
                    msg = model.getLastMessage();
                    viewHolder.time.setText(DateFormat.format("MMM dd, HH:mm",
                            (msg.getMessageTime()*-1)));
                    viewHolder.time.setTextColor(Color.parseColor("#808080"));
                    if(model.getMsgUnreadCountForMobile()>0) {
                        Log.d("count", String.valueOf(model.getMsgUnreadCountForMobile()));
                        viewHolder.notification.setVisibility(View.VISIBLE);
                        viewHolder.notification.setText(String.valueOf(model.getMsgUnreadCountForMobile()));
                        viewHolder.time.setTextColor(Color.parseColor("#40C4FF"));
                    }

                    previewText = msg.getMessageText();
                    Log.d("containN",previewText);
                    if(previewText.contains("\n")){
                        Log.d("containN",previewText);
                        previewText = previewText.replaceAll("\n"," ");
                        Log.d("removeN",previewText);
                    }

                    if(previewText.equals("Image")){
                        previewText = "[Image attachment]";
                    }

                    if(previewText.length()>38){
                        viewHolder.content.setText(previewText.substring(0,35)+"...");
                    }else{
                        viewHolder.content.setText(previewText);
                    }
                }else{
                    viewHolder.time.setText("");
                    viewHolder.content.setText("");
                }

//                int countBreak = 0;
//                File imgFile;
//                for(int i=0;i<model.getBearings().size();i++){
//                    Log.d("imgpath","count");
//                    imgFile = new File(model.getBearings().get(i).getImageFileUrl());
//                    Log.d("imgpath",imgFile.toString());
//                    if(imgFile.exists()){
//                        Log.d("imgpath","exist");
//                        countBreak++;
//                        try {
//                            viewHolder.img.setImageBitmap(handleSamplingAndRotationBitmap(getApplicationContext(),Uri.parse(model.getBearings().get(0).getImageFileUri()),model.getBearings().get(0).getImageFileUrl()));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    if(countBreak>0){
//                        break;
//                    }
//                }

                viewHolder.inquiryTxt.setText(model.getInquiryName());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("OnClick", "You clicked on "+ inquiryName);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after
                                startMessageActivity(viewHolder.getAdapterPosition());
                            }
                        }, 1000);
//                        startColorAnimation(view,viewHolder.getAdapterPosition());
                    }
                });

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.d("OnClick", "You Long clicked on "+ inquiryName);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after
                                startMessageActivity(viewHolder.getAdapterPosition());
                            }
                        }, 1000);

//                        startColorAnimation(v,viewHolder.getAdapterPosition());
                        return false;
                    }
                });

                viewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.d("OnClick", "You touch on "+ inquiryName);
//                        v.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.main_inquiry_background));
                        return false;
                    }
                });
            }
        };
        mRecyclerView.setAdapter(adapter);
        registerForContextMenu(mRecyclerView);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //get selected menu item (delete, edit etc)
        int menuItemIndex = item.getItemId();
        String menuItemName = menuItems[menuItemIndex];
        if(menuItemName.equals("Delete")){
            Log.d("Delete", "You choose to delete position"+item.getGroupId());
            Log.d("Delete", "You choose to delete "+adapter.getItem(item.getGroupId()).getInquiryName());
            adapter.getRef(item.getGroupId()).removeValue();
            // database.getReference("conversations/"+adapter.getItem(item.getGroupId()).getInquiryID()).removeValue();
            database.getReference("adconversations/"+user.getUid()+"/"+adapter.getItem(item.getGroupId()).getInquiryID()+"?"+adapter.getItem(item.getGroupId()).getInquiryName()).removeValue();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                //inquiry
                inquiryOwner = user.getUid();
                inquiryName = data.getStringExtra("inquiryTitle");
                //inquiryID = UUID.randomUUID().toString();
                //String [] peoples = {"testUser","Admin"};

                FirebaseUser user=  mAuth.getCurrentUser();

                String [] peoples = {user.getUid()};
                List<String> inquiryPeoples = new ArrayList<String>(Arrays.asList(peoples));

                //bearings
                Bundle bundle = data.getExtras();
                ArrayList<Bearing> arraylist = new ArrayList<>();
                arraylist = bundle.getParcelableArrayList("bearingList");

                //quotes
                //ArrayList<Quotation> quotelist = new ArrayList<>();
                //ArrayList<BearingQuote> bearingQuotes = new ArrayList<>();
                //quotelist.add(new Quotation());
                //bearingQuotes.add(new BearingQuote());
                //quotelist.get(0).setQuoteBearings(bearingQuotes);

                String messageID = UUID.randomUUID().toString();

                Long positiveNow = new Date().getTime();
                Long negativeNow = -(new Date().getTime());

                //create inquiry Object
                Inquiry inquiry = new Inquiry();
                inquiry.setInquiryName(inquiryName);
                inquiry.setInquiryTime(negativeNow);
                inquiry.setInquiryOwner(inquiryOwner);
                inquiry.setInquiryPeoples(inquiryPeoples);
                inquiry.setLastMessage(new ChatMessage(messageID,"I have sent you an inquiry",user.getUid(),negativeNow,user.getUid(),"inquiry",""));
                inquiry.setMsgUnreadCountForMobile(0);
                inquiry.setBearings(arraylist);
                //inquiry.setQuotations(quotelist);
                inquiry.setStatus("none");

                // push a new instance of inquiry to the Firebase database
                //refKey = ref.push().getKey();
                inquiryID = refNormalInquiry.push().getKey();
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
                Toast.makeText(MainActivity.this, "new inquiry added", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView inquiryTxt,content,time,notification;
        ImageView img;
        public MessageViewHolder(View itemView) {
            super(itemView);
//            img = (ImageView)itemView.findViewById(R.id.imageView1);
            inquiryTxt = (TextView)itemView.findViewById(R.id.title);
            content = (TextView)itemView.findViewById(R.id.content);
            time = (TextView)itemView.findViewById(R.id.time);
            notification = (TextView)itemView.findViewById(R.id.notification);
//            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//                @Override
//                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//                    Log.d("Position", "You long clicked on position "+getAdapterPosition());
//                    Log.w("OnLongClick", "You long clicked on "+adapter.getItem(getAdapterPosition()).getInquiryName());
//                    for (int i = 0; i<menuItems.length; i++) {
//                        contextMenu.add(getAdapterPosition(), i, i, menuItems[i]); //groupId, itemId, order, title
//                    }
//                }
//            });
        }
    }

    private void startColorAnimation(final View v,final int position){
        v.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.main_inquiry_selector));
        final TransitionDrawable background = (TransitionDrawable) v.getBackground();
        background.startTransition(300);
        background.reverseTransition(300);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after
                startMessageActivity(position);
            }
        }, 600);
    }
}
