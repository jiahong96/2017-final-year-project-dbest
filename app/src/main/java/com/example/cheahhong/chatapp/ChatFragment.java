package com.example.cheahhong.chatapp;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
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

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.cheahhong.chatapp.LoginActivity.calledPersistance;
import static com.example.cheahhong.chatapp.LoginActivity.database;
import static com.example.cheahhong.chatapp.MainActivity.NotificationID;

public class ChatFragment extends Fragment {
    private static final String ARG_PARAM_ID = "inquiryID";
    private static final String ARG_PARAM_NAME = "inquiryName";
    private static final String ARG_PARAM_LastMsg = "lastMsgID";
    private static final String ARG_PARAM_Unread = "unreadMsg";
    private String selectedImagePath = "";
    private Uri imgDownloadUri;
    private static final int RC_TAKE_PICTURE = 101;
    private FirebaseAuth mAuth;
    private Uri imgFileUri = null;
    private BroadcastReceiver mBroadcastReceiver;
    ProgressDialog mProgressDialog;
    Utility utility;
    ImageProcessing imgProcessor;
    FirebaseUser user ;
    static String[] menuItems;
    DatabaseReference refAdConv, refNormalConv,inquiryRef;
    static FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> adapter;
    static SharedPreferences                                       sp;

    int          friendlyMessageCount;
    int          lastVisiblePosition;
    int          unreadCount;

    FloatingActionButton fab;
    EditText inputMsg;
    ImageView pictureUpload;
    static boolean isInFront;
    private boolean isEditTextFocused;
    RecyclerView mRecyclerView;
    LinearLayoutManager layoutManager;
    String lastMessageKey,lastMessageID,inquiryName,inquiryID;
    ChatMessage lastMessage=null;
    Boolean deletedLastItem;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String ID, String Name, String lastMsgID, int unread) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_ID, ID);
        args.putString(ARG_PARAM_NAME, Name);
        args.putString(ARG_PARAM_LastMsg, lastMsgID);
        args.putInt(ARG_PARAM_Unread, unread);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getActivity().getSharedPreferences("FontSize", 0);
        mAuth = FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
        menuItems = getResources().getStringArray(R.array.chat_menu);
        deletedLastItem = false;

        utility = new Utility();
        imgProcessor = new ImageProcessing();

	    //parameters from messageactivity
        if (getArguments() != null) {
            inquiryID = getArguments().getString(ARG_PARAM_ID);
            inquiryName = getArguments().getString(ARG_PARAM_NAME);
            lastMessageID = getArguments().getString(ARG_PARAM_LastMsg);
            unreadCount = getArguments().getInt(ARG_PARAM_Unread);
        }

	    //initialize database
        if (!calledPersistance) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            calledPersistance = true;
        }

        // Local broadcast receiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("InquiryActivity", "onReceive:" + intent);

                switch (intent.getAction()) {
                    case MyUploadService.UPLOAD_COMPLETED:
                        //imgDownloadUri = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
                    case MyUploadService.UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }
            }
        };

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationID);

        refAdConv = database.getReference("adconversations").child(user.getUid()).child(inquiryID+"?"+inquiryName);
        refNormalConv = database.getReference("conversations").child(inquiryID);
        refAdConv.keepSynced(true);
        Log.d("database RefMsg",refAdConv.toString());

	    //listen to conversations
        refAdConv.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("new Messages",dataSnapshot.getKey());
                if(isInFront){
                    database.getReference().child("inquiries").child(inquiryID).
                            child("msgUnreadCountForMobile").setValue(0);
                    database.getReference().child("conversations").child(inquiryID).child(dataSnapshot.getKey()).
                            child("messageRead").setValue(true);
                    database.getReference().child("adinquiries").child(user.getUid()).child(inquiryID).
                            child("msgUnreadCountForMobile").setValue(0);
                    database.getReference().child("adconversations").child(user.getUid()).child(inquiryID+"?"+inquiryName).child(dataSnapshot.getKey()).
                            child("messageRead").setValue(true);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("removedMessage",dataSnapshot.getKey());
                refAdConv.child(dataSnapshot.getKey()).child("lastMessage").removeValue();
                if(isInFront) {
                    Toast.makeText(getActivity(), "message deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        inquiryRef = database.getReference("adinquiries").child(user.getUid()).child(inquiryID);
        inquiryRef.child("msgUnreadCountForMobile").setValue(0);

	    //listen to last message of conversation
        final Query lastMessageQuery = refAdConv.limitToLast(1);
        lastMessageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("lastMsgKeyAdd",dataSnapshot.getKey());
                lastMessage = dataSnapshot.getValue(ChatMessage.class);
                lastMessageKey = dataSnapshot.getKey();
                Log.d("lastMsgIntentID", String.valueOf(lastMessageID));
                Log.d("lastMsgSnapID", String.valueOf(lastMessage.getMessageID()));
                if(deletedLastItem){
                    database.getReference().child("inquiries").child(inquiryID).
                            child("lastMessage").setValue(dataSnapshot.getValue(ChatMessage.class));
                    database.getReference().child("adinquiries").child(user.getUid()).child(inquiryID).
                            child("lastMessage").setValue(dataSnapshot.getValue(ChatMessage.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("lastMsgKeyChng",dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("lastMsgKeyRemove",dataSnapshot.getKey());
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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        inputMsg = (EditText)view.findViewById(R.id.input);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        pictureUpload = (ImageView) view.findViewById(R.id.pic);

        inputMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Log.d("keypadAppear","yes");
                    isEditTextFocused = true;
                }
            }
        });

        inputMsg.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                Log.w("OnLongClick", "You long clicked on input box");
                contextMenu.add(111, 1, 1, menuItems[1]); //groupId, itemId, order, title
            }
        });

        pictureUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(utility.isNetworkAvailable(getActivity())){
                    launchPhotoStorage();
                }else{
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if(isEditTextFocused){
                    Log.d("keypadAppear","yes");
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
                }
            }
        });

        displayChatMessages();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tMsg = inputMsg.getText().toString().trim();

                Long positiveNow = new Date().getTime();
                Long negativeNow = -(new Date().getTime());

                if(tMsg.matches("")){
                    Log.d("empty msg","empty msg");
                    inputMsg.setText("");
                }else{
                    if(tMsg.contains("\n")){
                        Log.d("new line",tMsg);

//                        tMsg = tMsg.replaceAll("\n","\r\n");
//                        Log.d("r new line",tMsg);
//                        tMsg = tMsg.replaceAll("\r\n"," ");
//                        Log.d("r1 new line",tMsg);
                    }
                    // Read the input field and push a new instance
                    // of ChatMessage to the Firebase database
                    String messageID = refAdConv.push().getKey();
                    refAdConv.child(messageID)
                            .setValue(new ChatMessage(messageID,inputMsg.getText().toString(),user.getUid(),positiveNow,user.getUid(),"","")
                            );
                    refNormalConv.child(messageID)
                            .setValue(new ChatMessage(messageID,inputMsg.getText().toString(),user.getUid(),positiveNow,user.getUid(),"","")
                            );
                    database.getReference().child("adinquiries").child(user.getUid()).child(inquiryID).
                            child("lastMessage").setValue(new ChatMessage(messageID,inputMsg.getText().toString(),user.getUid(),negativeNow,user.getUid(),"",""));
                    database.getReference().child("inquiries").child(inquiryID).
                            child("lastMessage").setValue(new ChatMessage(messageID,inputMsg.getText().toString(),user.getUid(),positiveNow,user.getUid(),"",""));

                    inputMsg.setText("");
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unregister download receiver
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_TAKE_PICTURE) {
            Log.d("PhotoSelected", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
            if (resultCode == RESULT_OK) {
                imgFileUri = data.getData();

                Log.d("PhotoSelected", "resultok");
                if (imgFileUri != null) {
                    uploadFromUri(imgFileUri);
                    //bearingList.get(savedPosition).setImageFileUri(imgFileUri.toString());
                    //selectedImagePath = getAbsolutePath(imgFileUri);
                    //bearingList.get(savedPosition).setImageFileUrl(selectedImagePath);

                } else {
                    Log.w("PhotoFileNull", "File URI is null");
                }
            } else {
                Toast.makeText(getActivity(), "No picture selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFromUri(Uri fileUri) {
        // Show loading spinner
        showProgressDialog(getString(R.string.progress_uploading));

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        getActivity().startService(new Intent(getActivity(), MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI,fileUri.toString())
                .putExtra(MyUploadService.EXTRA_INQ_ID,inquiryID)
                .setAction(MyUploadService.ACTION_UPLOAD));
    }

    private void onUploadResultIntent(Intent intent) {
        // Got a new intent from MyUploadService with a success or failure
        //Log.d("uploadSuccess", String.valueOf(intent.getIntExtra(MyUploadService.EXTRA_DOWNLOAD_URL,0)));
        imgDownloadUri = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        String imgUrl = imgDownloadUri.toString();
        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        String messageID = refAdConv.push().getKey();

        Long positiveNow = new Date().getTime();
        Long negativeNow = -(new Date().getTime());

        refAdConv.child(messageID)
                .setValue(new ChatMessage(messageID,"Image",user.getUid(),positiveNow,user.getUid(),"",imgUrl)
                );
        refNormalConv.child(messageID)
                .setValue(new ChatMessage(messageID,"Image",user.getUid(),positiveNow,user.getUid(),"",imgUrl)
                );
        database.getReference().child("adinquiries").child(user.getUid()).child(inquiryID).
                child("lastMessage").setValue(new ChatMessage(messageID,"Image",user.getUid(),negativeNow,user.getUid(),"",imgUrl));
        database.getReference().child("inquiries").child(inquiryID).
                child("lastMessage").setValue(new ChatMessage(messageID,"Image",user.getUid(),positiveNow,user.getUid(),"",imgUrl));
        hideProgressDialog();
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void displayChatMessages() {
        adapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                ChatMessage.class,
                R.layout.message,
                MessageViewHolder.class,
                refAdConv
        )
        {
            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, ChatMessage model, int position) {

                viewHolder.unreadLayout.setVisibility(View.GONE);
                viewHolder.unreadMessages.setVisibility(View.GONE);

                if(lastMessage!=null && lastMessageID!=null && lastMessageID.equals(lastMessage.getMessageID())){
//                    Log.d("updatedToLatestMessage?", "yes");
//                    Log.d("position?", String.valueOf(position));
//                    Log.d("getcount?", String.valueOf(adapter.getItemCount()));

                    if(unreadCount>0 && unreadCount <= adapter.getItemCount() && position == (adapter.getItemCount()-unreadCount)){
                        Log.d("setUnreadTitle","yes");
                        viewHolder.unreadLayout.setVisibility(View.VISIBLE);
                        viewHolder.unreadMessages.setVisibility(View.VISIBLE);
                        viewHolder.unreadMessages.setText(String.valueOf(unreadCount)+" Unread messages");

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 5 sec
                                viewHolder.unreadLayout.setVisibility(View.GONE);
                                viewHolder.unreadMessages.setVisibility(View.GONE);
                                unreadCount=0;
                            }
                        }, 5000);
                    }
                }

                if(!model.getMessageUser().equals(user.getUid())){
                    viewHolder.contentwithBG.setBackgroundResource(R.drawable.bubble_in);

                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) viewHolder.contentwithBG.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    viewHolder.contentwithBG.setLayoutParams(lp);

                }else{
                    viewHolder.contentwithBG.setBackgroundResource(R.drawable.bubble_out);

                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) viewHolder.contentwithBG.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    viewHolder.contentwithBG.setLayoutParams(lp);
                }

                //setMessageOrImage(model,viewHolder.messageText,viewHolder.imgView,viewHolder.progress,viewHolder.cardView);
                if(!model.getLink().equals("") && model.getLink()!=null && model.getMessageText().equals("Image")){
                    viewHolder.messageText.setVisibility(View.GONE);
                    viewHolder.cardView.setVisibility(View.VISIBLE);
                    viewHolder.progress.setVisibility(View.VISIBLE);
                    viewHolder.imgView.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(model.getLink())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    viewHolder.progress.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    viewHolder.progress.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(viewHolder.imgView);

                    viewHolder.imgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(viewHolder.imgView.getDrawable() != null) {
                                LayoutInflater factory = LayoutInflater.from(getActivity());
                                final View view = factory.inflate(R.layout.dialog_photo, null);

                                PhotoView photo = (PhotoView) view.findViewById(R.id.imgView);
                                Bitmap bitmap1 = ((BitmapDrawable) viewHolder.imgView.getDrawable()).getBitmap();
                                photo.setImageBitmap(bitmap1);

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
                    viewHolder.messageText.setVisibility(View.VISIBLE);
                    viewHolder.messageText.setText(model.getMessageText());
                    viewHolder.cardView.setVisibility(View.GONE);
                    viewHolder.progress.setVisibility(View.GONE);
                    viewHolder.imgView.setVisibility(View.GONE);
                }
                //viewHolder.messageUser.setText(model.getMessageUser());
                viewHolder.messageTime.setText(DateFormat.format("EEE, HH:mm", model.getMessageTime()));
            }
        };

        mRecyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(final int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                friendlyMessageCount = adapter.getItemCount();
                lastVisiblePosition =
                        layoutManager.findLastCompletelyVisibleItemPosition();

                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                //Log.d("scroll to latest", String.valueOf(positionStart));
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    layoutManager.scrollToPosition(positionStart);
                }

                if(lastMessageID!=null && lastMessage!=null && lastMessageID.equals(lastMessage.getMessageID())) {
                    if(unreadCount>0){
                        if(positionStart>10 && (positionStart-unreadCount)>6){
//                            Log.d("scroll to unread1", String.valueOf(positionStart));
//                            Log.d("scroll to unreadCount1", String.valueOf(unreadCount));
//                            Log.d("scrolltounreadCount1Po", String.valueOf(positionStart-(unreadCount)));
                            layoutManager.scrollToPositionWithOffset((positionStart-unreadCount), 20);
                        }else{
//                            Log.d("scroll to unread2", String.valueOf(positionStart));
//                            Log.d("scroll to unreadCount2", String.valueOf(unreadCount));
                            layoutManager.scrollToPositionWithOffset((positionStart-unreadCount),0);
                        }
                    }
                }
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
//                Log.d("hi range","bb");
            }
        });
    }

    private void setMessageOrImage(ChatMessage chat, TextView textView, ImageView imgView, ProgressBar pgBar, CardView cd) {
        if (chat.getMessageText().equals("Image") && !chat.getLink().equals("") && chat.getLink() != null) {
            showImageViews(textView,imgView, pgBar,cd);
        }else {
            showMessageView(textView,imgView, pgBar,cd);
        }
    }

    private void showImageViews(TextView textView, ImageView imgView, ProgressBar pgBar, CardView cd) {
        textView.setVisibility(View.GONE);
    }

    private void showMessageView(TextView textView, ImageView imgView, ProgressBar pgBar, CardView cd) {
        imgView.setVisibility(View.GONE);
        pgBar.setVisibility(View.GONE);
        cd.setVisibility(View.GONE);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //get selected menu item (delete, edit etc)
        int menuItemIndex = item.getItemId();
        String menuItemName = menuItems[menuItemIndex];
        ClipboardManager clipboard = (ClipboardManager)
                getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//        if(menuItemName.equals("Delete")){
//            Log.d("Delete", "You choose to delete position"+item.getGroupId());
////            Log.d("Delete", "You choose to delete "+adapter.getItem(item.getGroupId()).getMessageText());
//            if(lastMessageKey.equals(adapter.getRef(item.getGroupId()).getKey())){
////                Log.d("Delete", "You choose to delete last item");
//                adapter.getRef(item.getGroupId()).removeValue();
//                inquiryRef.child("lastMessage").setValue(null);
//                deletedLastItem = true;
//            }else{
////                Log.d("Delete", "You choose to delete normal item");
//                adapter.getRef(item.getGroupId()).removeValue();
//            }
//        }
        if(menuItemName.equals("Copy")){
            Log.d("Copy", "You choose to copy normal item");
            // Gets a handle to the clipboard service.
            ClipData clip = ClipData.newPlainText("messageText", adapter.getItem(item.getGroupId()).getMessageText());

            // Set the clipboard's primary clip.
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), "text copied", Toast.LENGTH_SHORT).show();
        }
        else if(menuItemName.equals("Paste")){
            String pasteData = "";

            // If the clipboard doesn't contain data, disable the paste menu item.
            // If it does contain data, decide if you can handle the data.
            if (!(clipboard.hasPrimaryClip())) {

                item.setEnabled(false);

            } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

                // This disables the paste menu item, since the clipboard has data but it is not plain text
                item.setEnabled(false);
            } else {

                // This enables the paste menu item, since the clipboard contains plain text.
                item.setEnabled(true);
            }

            // Examines the item on the clipboard. If getText() does not return null, the clip item contains the
            // text. Assumes that this application can only handle one item at a time.
            ClipData.Item paste_item = clipboard.getPrimaryClip().getItemAt(0);

            // Gets the clipboard as text.
            pasteData = paste_item.getText().toString();

            // If the string contains data, then the paste operation is done
            if (pasteData != null) {
                Log.d("paste",pasteData);
                inputMsg.setText(pasteData);
            }
        }
        return true;
    }

    private void launchPhotoStorage() {
        Log.d("PhotoStorage", "launch");

        // Pick an image from storage
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        } else
            return null;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView messageText,messageUser,messageTime,unreadMessages;
        ImageView    imgView;
        ProgressBar progress;
        LinearLayout content, contentwithBG,mainLayout;
        RelativeLayout unreadLayout,msgRelativeLayout;
        CardView cardView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            unreadLayout = (RelativeLayout) itemView.findViewById(R.id.unreadLayout);
            mainLayout = (LinearLayout) itemView.findViewById(R.id.messageMainLayout);
            msgRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.msgRelativeLayout);
            contentwithBG = (LinearLayout) itemView.findViewById(R.id.contentWithBackground);
            unreadMessages = (TextView)itemView.findViewById(R.id.unreadMsg);
            messageText = (TextView)itemView.findViewById(R.id.message_text);
            messageTime = (TextView)itemView.findViewById(R.id.messageTime);
            imgView = (ImageView) itemView.findViewById(R.id.imgView);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
            cardView = (CardView) itemView.findViewById(R.id.cardView);

            if(sp.getString("font",null)!=null && sp.getString("font",null).equals("small")){
                messageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            }else if(sp.getString("font",null)!=null && sp.getString("font",null).equals("large")){
                messageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }else if(sp.getString("font",null)!=null && sp.getString("font",null).equals("medium")){
                messageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    Log.d("Position", "You long clicked on position "+getAdapterPosition());
                    Log.w("OnLongClick", "You long clicked on "+adapter.getItem(getAdapterPosition()).getMessageText());
//                    for (int i = 0; i<menuItems.length; i++) {
                    contextMenu.add(getAdapterPosition(), 0, 0, menuItems[0]); //groupId, itemId, order, title
//                    }
                }
            });
        }
    }
}
