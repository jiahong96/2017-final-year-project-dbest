package com.developer.cheahhong.dbest;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.developer.cheahhong.dbest.LoginActivity.calledPersistance;
import static com.developer.cheahhong.dbest.LoginActivity.database;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final String userType = "member";

    TextInputLayout layout_email,layout_password,layout_name,layout_repassword,layout_contact,layout_address;;
    EditText _nameText, _emailText,_passwordText,_reEnterPasswordText,_contactText, _addressText;
    Button _signupButton;
    TextView _loginLink;
    String email,name,contact,address;
    ProgressDialog progressDialog;
    Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        if (!calledPersistance) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            calledPersistance = true;
        }
        mDatabase = database.getReference();

        _signupButton = (Button)findViewById(R.id.btn_signup);
        _loginLink = (TextView)findViewById(R.id.link_login);
        layout_email = (TextInputLayout)findViewById(R.id.lay_email);
        layout_password = (TextInputLayout)findViewById(R.id.lay_password);
        layout_name = (TextInputLayout)findViewById(R.id.lay_name);
        layout_repassword = (TextInputLayout)findViewById(R.id.lay_repassword);
        layout_contact = (TextInputLayout)findViewById(R.id.lay_contact);
        layout_address = (TextInputLayout)findViewById(R.id.lay_address);
        _nameText= (EditText)findViewById(R.id.input_name);
        _emailText= (EditText)findViewById(R.id.input_email);
        _passwordText= (EditText)findViewById(R.id.input_password);
        _reEnterPasswordText= (EditText)findViewById(R.id.input_reEnterPassword);
        _contactText = (EditText)findViewById(R.id.input_contact);
        _addressText = (EditText)findViewById(R.id.input_address);


        _contactText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (_contactText.getText().length() > 0) {
                    layout_contact.setError(null);
                    layout_contact.setErrorEnabled(false);
                }
            }
        });

        _nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable edt) {
                if (_nameText.getText().length() > 0) {
                    layout_name.setError(null);
                    layout_name.setErrorEnabled(false);
                }
            }
        });

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

        _reEnterPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable edt) {
                if (_reEnterPasswordText.getText().length() > 0) {
                    layout_repassword.setError(null);
                    layout_repassword.setErrorEnabled(false);
                }
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                onBackPressed();
            }
        });
    }

    public void signup() {
        // //Log.d(TAG, "Signup");
        utility = new Utility();
        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        if (!validate()) {
            progressDialog.dismiss();
            return;
        }

        if(!utility.isNetworkAvailable(SignupActivity.this)){
            Toast.makeText(SignupActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        _signupButton.setEnabled(false);
        _loginLink.setEnabled(false);

        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        contact = _contactText.getText().toString();
        address = _addressText.getText().toString();
        // String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUser(email,name,contact,address,user.getUid());

                        } else {
                            // If sign in fails, display a message to the user.
                            onSignupFailed(task.getException().getMessage());
                        }
                    }
                });

    }

    public void saveUser(String email,String name,String contact,String address,String uid){
        User user = new User(email,name,contact,address,userType,322);

        mDatabase.child("users").child(uid).setValue(user, new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                progressDialog.dismiss();
                onSignupSuccess();
            }
        });
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        _loginLink.setEnabled(true);

        Toast.makeText(SignupActivity.this, "Signed Up successfully", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed(String errorMsg) {
        Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        _loginLink.setEnabled(true);
        progressDialog.dismiss();
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String contact = _contactText.getText().toString();
//        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            layout_name.setErrorEnabled(true);
            layout_name.setError("at least 3 characters");
            valid = false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layout_email.setErrorEnabled(true);
            layout_email.setError("enter a valid email address");
            valid = false;
        }

        if (contact.isEmpty() || contact.length() <9){
            layout_contact.setErrorEnabled(true);
            layout_contact.setError("Please input a valid number");
            valid = false;
        }

        //        if (mobile.isEmpty() || mobile.length()!=10) {
        //            _mobileText.setError("Enter Valid Mobile Number");
        //            valid = false;
        //        } else {
        //            _mobileText.setError(null);
        //        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            layout_password.setErrorEnabled(true);
            layout_password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            layout_repassword.setErrorEnabled(true);
            layout_repassword.setError("Password Do not match");
            valid = false;
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter, R.anim.leave);
    }
}
