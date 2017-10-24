package com.developer.cheahhong.dbest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.developer.cheahhong.dbest.LoginActivity.calledPersistance;
import static com.developer.cheahhong.dbest.LoginActivity.database;

public class SettingsActivity extends BaseActivity {

    public static final String URL_FEEDBACK = "https://goo.gl/forms/zro9jlfa5jGAqDex2";
    public static final String URL_ABOUT = "https://www.facebook.com/Dbestcompanykuching/";
    public static final String URL_CONTACT = "dbestkchonline@gmail.com";

    TextView txtPoint;
    LinearLayout layoutFeedback,layoutFont,layoutAbout,layoutContact,layoutLogout;
    String[]          fontItems;
    FirebaseAuth      mAuth;
    FirebaseUser      user;
    Query             queryUserRef;
    User appUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);

        //initialize auth and update UI
        mAuth = FirebaseAuth.getInstance();
        updateUI();
        appUser = new User();

        //initialize database
        if (!calledPersistance) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            calledPersistance = true;
        }

        queryUserRef = database.getReference("users").orderByChild("email").equalTo(user.getEmail());

        fontItems = getResources().getStringArray(R.array.font);

        txtPoint = (TextView)findViewById(R.id.txt_point);
        layoutFeedback = (LinearLayout) findViewById(R.id.layout_feedback);
        layoutFont = (LinearLayout) findViewById(R.id.layout_font);
        layoutAbout = (LinearLayout) findViewById(R.id.layout_about);
        layoutContact = (LinearLayout) findViewById(R.id.layout_contact);
        layoutLogout = (LinearLayout) findViewById(R.id.layout_logout);

        Animation anim = new AlphaAnimation(0.05f, 1.0f);
        anim.setDuration(1500); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        txtPoint.startAnimation(anim);

        queryUserRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appUser = dataSnapshot.getValue(User.class);
                txtPoint.setText(String.valueOf(appUser.getMemberPoint()));
                Log.d("userData",dataSnapshot.toString());
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

        layoutFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_FEEDBACK));
                        startActivity(browserIntent);
                    }
                }, 1000);
            }
        });

        layoutFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after
                        fontDialog();
                    }
                }, 1000);
            }
        });

        layoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_ABOUT));
                        startActivity(browserIntent);
                    }
                }, 1000);
            }
        });

        layoutContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{URL_CONTACT});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact DBest (App)");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    }
                }, 1000);

            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("logout","yes");
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
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
            }
        });

    }

    void fontDialog(){
        SharedPreferences sp=getSharedPreferences("FontSize", 0);
        final SharedPreferences.Editor Ed=sp.edit();
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

    @Override
    int getContentViewId() {
        return R.layout.activity_settings;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.action_settings;
    }
}
