package com.example.cheahhong.dbest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.cheahhong.dbest.ChatFragment.isInFront;
import static com.example.cheahhong.dbest.LoginActivity.calledPersistance;
import static com.example.cheahhong.dbest.LoginActivity.database;
import static com.example.cheahhong.dbest.MainActivity.NotificationID;

/**
 * Created by lesgo on 4/21/2017.
 */

public class BackgroundService extends Service {
    private static FirebaseAuth mAuth;

    static DatabaseReference myRef;
    static FirebaseUser user ;
    static String userID;

    NotificationManager notificationManager;
    List<String> messages;
    int inquiryCount;
    int count;
    String inquiryTitle;
    SharedPreferences sp1,sp2;
    JSONArray inquiryMessages;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //initialize database
        if (!calledPersistance) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            calledPersistance = true;
        }

        //initialize fields
        mAuth = FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
        sp1=this.getSharedPreferences("Login",0);

        if(user!=null){
            userID = user.getUid();
            Log.d("firstID",userID);
        }

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    userID = user.getUid();
                    //myRef = database.getReference("adconversations").child(user.getUid());
                    Log.d("listenUserID",userID);
                }
            }
        });

        //notification logic
        if(sp1!=null &&sp1.getString("login",null)!=null ){
            if(sp1.getString("login",null).equals("true")&& user!= null){
                Log.d("refUserID",userID);
                count = 0;
                myRef = database.getReference("adconversations").child(userID);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        inquiryMessages = new JSONArray();
                        messages = new ArrayList<String>();
                        Log.d("inquiryCountInq", String.valueOf(dataSnapshot.getChildrenCount()));
                        inquiryCount = (int) dataSnapshot.getChildrenCount();

                        for(DataSnapshot c : dataSnapshot.getChildren())
                        {
                            ArrayList<String> currentMsges = new ArrayList<String>();
                            Log.d("notificationInquiry",c.toString());
                            for(DataSnapshot b : c.getChildren())
                            {
                                //   Log.d("messageKey",b.toString());
                                //   Log.d("aaaaa",b.child("messageText").toString());

                                if(b.child("messageRead").getValue().toString().equalsIgnoreCase("false") && b.child("messageUser").getValue().toString().equalsIgnoreCase("admin")){
                                    if(b.child("inquiryOwner").getValue().toString().equalsIgnoreCase(userID)){
                                        Log.d("notificationKeyMsg",b.child("messageText").getValue().toString());

                                        inquiryTitle = (c.getKey().toString()).split("\\?")[1];
                                        Log.d("notificationKey",inquiryTitle);

                                        messages.add(b.child("messageText").getValue().toString());
                                        currentMsges.add(b.child("messageText").getValue().toString());
                                    }
                                }
                            }
                            if(currentMsges.size()!=0){
                                Log.d("notification length is", String.valueOf(currentMsges.size()));
                                Log.d("notification",currentMsges.toString());

                                JSONObject inquiry = new JSONObject();
                                try {
                                    inquiry.put("inquiryTitle", inquiryTitle);
                                    inquiry.put("unreadCount", currentMsges.size());
                                    inquiry.put("messages", new JSONArray(currentMsges));
                                    inquiryMessages.put(inquiry);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("mainInq",inquiryMessages.toString());
                            }
                        //   ChatMessage m = (ChatMessage) child.getValue();
                        //   Log.d("aaaaa",m.getMessageText());
                        }
                        if(messages.size()!=0) {
                            //if not in front of activity, send notifications
                            if (!isInFront) {
                                sp2 = getSharedPreferences("Login", 0);
                                if (sp2.getString("login", null).equals("true")) {
                                    sendNotification();
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
        // do your jobs here
        return Service.START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    public void sendNotification(){

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(); // set big style if many content
        NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this);

        if(messages.size()==1){
            try{
                mBuilder.setContentText(messages.get(0).toString());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            Log.d("aainquiryCount", String.valueOf(inquiryCount));
            Log.d("unreadinquiryCount", String.valueOf(inquiryMessages.length()));
            for(int i =0;i<inquiryMessages.length();i++) {
                JSONObject inquiry;
                JSONArray msgArray;
                try {
                    inquiry = inquiryMessages.getJSONObject(i);
                    Log.d("yoloinqObj", inquiry.toString());
                    msgArray = inquiry.getJSONArray("messages");
                    Log.d("yolomsgObj", msgArray.toString());
                    mBuilder.setStyle(inboxStyle);
                    inboxStyle.setBigContentTitle("D'Best");
                    for (int j=0; j < msgArray.length(); j++) {
                        Log.d("yoloinq", "dd");
                        inboxStyle.addLine(inquiry.getString("inquiryTitle")+": "+msgArray.get(j).toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(inquiryMessages.length()>1){
                mBuilder.setContentText(messages.size()+" new messages from "+inquiryMessages.length()+" chats");
            }else if(inquiryMessages.length()==1){
                mBuilder.setContentText(messages.size()+" new messages");
            }
        }

        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("D'Best")
                .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)// clear issued notification when clicked
                .setPriority(Notification.PRIORITY_HIGH);




//        Intent i = new Intent(this,MainActivity.class);
//        // The stack builder object will contain an artificial back stack for the started Activity.
//        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(MainActivity.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(i);
//
//        PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(resultPendingIntent);

        Intent i = new Intent(this.getApplicationContext(),MainActivity.class);
        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(i);

        PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationID,mBuilder.build());
    }
}
