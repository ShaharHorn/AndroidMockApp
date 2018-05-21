package com.dev.android.mta.androiduserauthentication.implementaion;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dev.android.mta.androiduserauthentication.Model.AnalyticsManager;
import com.dev.android.mta.androiduserauthentication.R;
import com.dev.android.mta.androiduserauthentication.interfaces.IExternalApiLogin;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.dev.android.mta.androiduserauthentication.constans.AuthenticationMethodConstatns.RC_SIGN_IN;

/**
 * Created by Ori on 4/25/2018.
 */

public class GoogleLogin extends AuthenticationMethod implements IExternalApiLogin {
    public GoogleLogin(FirebaseAuth iAuth, AppCompatActivity mainActivity) {
        super(iAuth, mainActivity);
    }
    private SignInButton mGoogleButton;
    private GoogleSignInOptions mGso;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private AnalyticsManager mAnalyticsManager = AnalyticsManager.getInstance();


    @Override
    public void SingIn(Parcelable SignInDetails) {
        GoogleSignInAccount googleAccount = (GoogleSignInAccount)SignInDetails;
        AuthCredential credential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mMainActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String method = "FaceBook SignIn " +" - " +mAuth.getCurrentUser().getEmail();
                            mAnalyticsManager.trackLoginEvent(method);
                            startNewActivityWithUserDetails(mAuth.getCurrentUser());
                        } else {
                            Toast.makeText(mMainActivity, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();                        }
                    }
                });
    }

    @Override
    public void Init() {
        mGoogleButton = mMainActivity.findViewById(R.id.google_sign_button);

        mGso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mMainActivity.getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mMainActivity.getApplicationContext()).enableAutoManage(mMainActivity, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(mMainActivity, "Error on Google login",
                        Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mMainActivity, mGso);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startNewActivityWithUserDetails(firebaseAuth.getCurrentUser());
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGooglesignIn();
            }
        });

    }

    @Override
    public void onSignInResult(int requestCode, int resultCode, Intent data){

        if (requestCode == RC_SIGN_IN) {
            //Google Login...
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                String method = "Google SignIn " +" - " +account.getEmail();
                mAnalyticsManager.trackLoginEvent(method);
                startNewActivityWithUserDetails(mAuth.getCurrentUser());

            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e);
                // ...
            }
        }
        Log.e(TAG, "onActivityResult () <<");
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        IExternalApiLogin googleLogin = new GoogleLogin(mAuth, mMainActivity);
        googleLogin.SingIn(account);
    }

    private void onGooglesignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mMainActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
