package com.example.cheahhong.dbest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends BaseActivity {

    public static final String URL_FEEDBACK = "https://goo.gl/ZXEum8";
    public static final String URL_ABOUT = "https://www.facebook.com/Dbestcompanykuching/";
    public static final String URL_CONTACT = "dbestkchonline@gmail.com";

    Button   logout;
    LinearLayout layoutFeedback,layoutFont,layoutAbout,layoutContact;
    String[] fontItems;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);

        //initialize auth and update UI
        mAuth = FirebaseAuth.getInstance();
        updateUI();

        fontItems = getResources().getStringArray(R.array.font);
        
        logout = (Button)findViewById(R.id.btn_logout);
        layoutFeedback = (LinearLayout) findViewById(R.id.layout_feedback);
        layoutFont = (LinearLayout) findViewById(R.id.layout_font);
        layoutAbout = (LinearLayout) findViewById(R.id.layout_about);
        layoutContact = (LinearLayout) findViewById(R.id.layout_contact);

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

        logout.setOnClickListener(new View.OnClickListener() {
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
