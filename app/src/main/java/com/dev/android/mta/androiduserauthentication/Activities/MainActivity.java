package com.dev.android.mta.androiduserauthentication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.android.mta.androiduserauthentication.R;
import com.dev.android.mta.androiduserauthentication.constans.AuthenticationMethodConstatns;
import com.dev.android.mta.androiduserauthentication.implementaion.AuthenticationMethodFactory;
import com.dev.android.mta.androiduserauthentication.implementaion.FacebookLogin;
import com.dev.android.mta.androiduserauthentication.implementaion.GoogleLogin;
import com.dev.android.mta.androiduserauthentication.interfaces.IEmailAndPasswordAuthentication;
import com.dev.android.mta.androiduserauthentication.interfaces.IExternalApiLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private List<IExternalApiLogin> mExternaApiLoginList;

    private FirebaseAuth mAuth;
    private String mEmail;
    private String mPassword;

    private Button signInButton;
    private Button mBackButton;

    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        addExternalAPIS();
        initExternalAPIS();
        signInButton = findViewById(R.id.btn_signin);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInByEmailAndPass();
            }
        });
        TextView forgetPassBtn = findViewById(R.id.forgetPwBtn);
        forgetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onForgetPassClick();
            }
        });
        mBackButton =findViewById(R.id.BackBtn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackBtnClick();
            }
        });

    }

    //Implement List of ExternalAPIs for extendability.
    private void addExternalAPIS() {
        mExternaApiLoginList = new ArrayList<>();
        mExternaApiLoginList.add(new FacebookLogin(mAuth,this));
        mExternaApiLoginList.add(new GoogleLogin(mAuth,this));
    }

    private void initExternalAPIS() {
        for(IExternalApiLogin externalApiLogin : mExternaApiLoginList){
            externalApiLogin.Init();
        }
    }

    private void onBackBtnClick() {
        Intent intent = new Intent(MainActivity.this,MainActivityOpening.class);
        startActivity(intent);
    }

    private void signInByEmailAndPass() {
        if (updateCurrentEmailAndPassword()) {
            IEmailAndPasswordAuthentication authenticationMethod = AuthenticationMethodFactory.getAuthenticationMethod(AuthenticationMethodConstatns.EMAIL_AND__PASSWORD, mAuth, this);
            authenticationMethod.SignIn(mEmail, mPassword);
        }
    }


    private void updateUI() {
        Intent intent = new Intent(MainActivity.this, AccountInfoActivity.class);
        startActivity(intent);
        finish();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for(IExternalApiLogin externalApiLogin : mExternaApiLoginList){
            externalApiLogin.onSignInResult(requestCode,resultCode,data);
        }

        Log.e(TAG, "onActivityResult () <<");
    }





    public void onForgetPassClick() {
        if (!(((TextView) (findViewById(R.id.input_email))).getText()).toString().isEmpty()) {
            mEmail = (((TextView) (findViewById(R.id.input_email))).getText()).toString();

            mAuth.sendPasswordResetEmail(mEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                                ((TextView) findViewById(R.id.input_email)).setText(mEmail);
                            } else {
                                Toast.makeText(MainActivity.this, "Email does'nt exist, try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "Please fill in your email addres.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private boolean updateCurrentEmailAndPassword() {
        if (!(((TextView) (findViewById(R.id.input_email))).getText()).toString().isEmpty()) {
            mEmail = (((TextView) (findViewById(R.id.input_email))).getText()).toString();
            if (!(((TextView) (findViewById(R.id.input_password))).getText()).toString().isEmpty()) {
                mPassword = (((TextView) (findViewById(R.id.input_password))).getText()).toString();
                return true;
            }
        }
        Toast.makeText(MainActivity.this, "Please fill your loging details", Toast.LENGTH_SHORT).show();
        return false;
    }

}