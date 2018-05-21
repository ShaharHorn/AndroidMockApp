package com.dev.android.mta.androiduserauthentication.implementaion;


import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dev.android.mta.androiduserauthentication.Model.AnalyticsManager;
import com.dev.android.mta.androiduserauthentication.R;
import com.dev.android.mta.androiduserauthentication.interfaces.IExternalApiLogin;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Ori on 4/25/2018.
 */

public class FacebookLogin extends AuthenticationMethod implements IExternalApiLogin {

    private CallbackManager mCallbackManager;
    private LoginButton mFacebookLoginButton;
    private AnalyticsManager mAnalyticsManager = AnalyticsManager.getInstance();

    public FacebookLogin(FirebaseAuth iAuth, AppCompatActivity mainActivity) {
        super(iAuth, mainActivity);
    }

    @Override
    public void SingIn(Parcelable SignInDetails) {
        AccessToken token = (AccessToken)SignInDetails;
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mMainActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String method = "Facebook SignIn " +" - " +mAuth.getCurrentUser().getEmail();
                            mAnalyticsManager.trackLoginEvent(method);
                            startNewActivityWithUserDetails(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mMainActivity, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
    }

    @Override
    public void Init() {
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton = mMainActivity.findViewById(R.id.button_facebook_login);
        mFacebookLoginButton.setReadPermissions("email", "public_profile");
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                // ...
            }
        });
    }

    @Override
    public void onSignInResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }


    private void handleFacebookAccessToken(AccessToken token) {
        FacebookLogin facebookLogin = new FacebookLogin(mAuth, mMainActivity);
        facebookLogin.SingIn(token);
    }
}
