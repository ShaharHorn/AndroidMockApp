package com.dev.android.mta.androiduserauthentication.implementaion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dev.android.mta.androiduserauthentication.Activities.ItemsMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.dev.android.mta.androiduserauthentication.Model.User;

import static com.dev.android.mta.androiduserauthentication.constans.DatabaseConstans.USERS;

/**
 * Created by Ori on 4/25/2018.
 */

public abstract class AuthenticationMethod {
    protected FirebaseAuth mAuth;
    protected AppCompatActivity mMainActivity;
    public final String TAG = "AuthenticationMethod";


    public AuthenticationMethod(FirebaseAuth iAuth, AppCompatActivity mainActivity){
        mAuth = iAuth;
        mMainActivity = mainActivity;
    }

    protected void startNewActivityWithUserDetails(FirebaseUser user){

        Intent intent = new Intent(mMainActivity, ItemsMenuActivity.class);
        mMainActivity.startActivity(intent);
    }

}
