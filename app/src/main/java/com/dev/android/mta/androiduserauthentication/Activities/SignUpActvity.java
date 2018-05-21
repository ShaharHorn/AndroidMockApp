package com.dev.android.mta.androiduserauthentication.Activities;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dev.android.mta.androiduserauthentication.R;
import com.dev.android.mta.androiduserauthentication.implementaion.EmailAndPasswordAuthentication;
import com.dev.android.mta.androiduserauthentication.interfaces.IEmailAndPasswordAuthentication;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActvity extends AppCompatActivity {
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mUserName;
    private EditText mPhoneNumber;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_actvity);
        Button signUpBtn = (findViewById(R.id.SignUpBtn));
        mEmail = (findViewById(R.id.mailText));
        mPassword = (findViewById(R.id.pwText));
        mConfirmPassword= (findViewById(R.id.pwConfirmText));
        mUserName = (findViewById(R.id.userNameText));
        mPhoneNumber = (findViewById(R.id.userNameText));
        mProgressDialog = new ProgressDialog(this);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {
        IEmailAndPasswordAuthentication emailAndPasswordAuthentication = new EmailAndPasswordAuthentication(mAuth,this);
        emailAndPasswordAuthentication.SignUp(mUserName.getText().toString(),mEmail.getText().toString(),mPassword.getText().toString(), mConfirmPassword.getText().toString(),mPhoneNumber.getText().toString());



    }

    public void StartProggressDialog(){
        mProgressDialog.setMessage("registering User...");
        mProgressDialog.show();
    }

    public void StopProggressDialog(){
        mProgressDialog.cancel();
    }




}
