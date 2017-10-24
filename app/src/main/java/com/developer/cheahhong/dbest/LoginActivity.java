package com.developer.cheahhong.dbest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    /* Listener for Firebase session changes */
    private FirebaseAuth mAuth;

    static FirebaseDatabase database;
    static boolean calledPersistance = false;

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    Query           queryRef;

    TextInputLayout layout_email,layout_password;
    EditText _emailText, _passwordText;
    Button _loginButton;
    TextView _signupLink;
    CheckBox _checkBox;
    ProgressDialog progressDialog;

    String logout;
    Utility utility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize fields
        mAuth = FirebaseAuth.getInstance();
        logout = getIntent().getStringExtra("Logout");
        SharedPreferences sp1=this.getSharedPreferences("Login",0);
        layout_email = (TextInputLayout)findViewById(R.id.lay_email);
        layout_password = (TextInputLayout)findViewById(R.id.lay_password);
        _emailText = (EditText)findViewById(R.id.input_email);
        _passwordText= (EditText)findViewById(R.id.input_password);
        _loginButton = (Button)findViewById(R.id.btn_login);
        _signupLink = (TextView)findViewById(R.id.link_signup);
        _checkBox = (CheckBox)findViewById(R.id.rememberPassword) ;

        //initialize database
        if (!calledPersistance) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            calledPersistance = true;
        }

	    //logged out
        if(logout!=null){
            Log.d("Logout","yy");
            Toast.makeText(LoginActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            SharedPreferences sp=getSharedPreferences("Login", 0);
            SharedPreferences.Editor Ed=sp.edit();
            Ed.putString("login","false");
            Ed.commit();
            Log.d("new setting",sp.getString("login",null));
        }

	    //permission for mashmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        _emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable edt) {
                if (_emailText.getText().length() > 0) {
                    layout_email.setError(null);
                    layout_email.setErrorEnabled(false);
                }
            }
        });

        _passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable edt) {
                if (_passwordText.getText().length() > 0) {
                    layout_password.setError(null);
                    layout_password.setErrorEnabled(false);
                }
            }
        });

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                //finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        // go to mainactivity if logged in
        if(mAuth.getCurrentUser()!=null){
            finish();
            Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
            startActivity(intent);
            this.overridePendingTransition(0, 0);
        }

        //check whether "remember me" is checked
        if(sp1.getString("checked",null)!=null){
            if(sp1.getString("checked",null).equalsIgnoreCase("true")){
                _checkBox.setChecked(true);

                if(sp1.getString("email",null)!=null ){
                    _emailText.setText(sp1.getString("email",null));
                    _passwordText.setText(sp1.getString("password",null));
                }
            }else{
                _checkBox.setChecked(false);
            }
        }
        _checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                SharedPreferences sp=getSharedPreferences("Login", 0);
                SharedPreferences.Editor Ed=sp.edit();
                if(isChecked){
                    Ed.putString("checked","true");
                    Ed.commit();
                }else{
                    Ed.putString("checked","false");
                    Ed.commit();
                }
            }
        });
    }

    public void login(){
        Log.d("dataSnap","login");
        utility = new Utility();

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        if (!validate()) {
            progressDialog.dismiss();
            return;
        }

        if(!utility.isNetworkAvailable(LoginActivity.this)){
            Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        // do something here
        _loginButton.setEnabled(false);
        _signupLink.setEnabled(false);

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        queryRef = database.getReference("users").orderByChild("email").equalTo(email);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()!=null){
                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);

                    Log.d("dataSnap",dataSnapshot.getValue().toString());

                    if(user.getType().equals("admin")){
                        Log.d("dataSnap","loginAdmin");
                        onLoginFailed("Admin account detected");
                    } else{
                        Log.d("dataSnap","loginNormal");
                        //Toast.makeText(LoginActivity.this, "Normal account detected", Toast.LENGTH_SHORT).show();
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();

                                    onLoginSuccess();

                                    // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                                } else {
                                    onLoginFailed(task.getException().getMessage());
                                }
                            }
                        });
                    }
                }else{

                    onLoginFailed("Account doesn't exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onLoginFailed("database error");
            }
        });
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            progressDialog.dismiss();
//
//                            onLoginSuccess();
//                            // Sign in success, update UI with the signed-in user's information
////                            FirebaseUser user = mAuth.getCurrentUser();
////                            updateUI(user);
//                        } else {
//                            onLoginFailed(task.getException().getMessage());
//                        }
//                    }
//                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
//                this.finish();
            }
        }
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        _signupLink.setEnabled(true);

        Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();

        SharedPreferences sp=getSharedPreferences("Login", 0);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("login","true");
        if(_checkBox.isChecked())
        {
            Ed.putString("email",_emailText.getText().toString() );
            Ed.putString("password",_passwordText.getText().toString());
            Ed.putString("checked","true");
            Ed.commit();
        }else{
//            Ed.putString("email",null);
//            Ed.putString("password",null);
            Ed.putString("checked","false");
            Ed.commit();
        }

        finish();
        Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
        startActivity(intent);

    }

    public void onLoginFailed(String errorMsg) {
        progressDialog.dismiss();
        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
        _signupLink.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layout_email.setErrorEnabled(true);
            layout_email.setError("enter a valid email address");
            valid = false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            layout_password.setErrorEnabled(true);
            layout_password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        }

        return valid;
    }
}
