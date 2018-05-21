package com.dev.android.mta.androiduserauthentication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dev.android.mta.androiduserauthentication.Model.AnalyticsManager;
import com.dev.android.mta.androiduserauthentication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;

public class MainActivityOpening extends AppCompatActivity {
    private FirebaseRemoteConfig mConfig;
    private Button mSkipButton;
    private FirebaseAuth mAuth;
    public static final String TAG = "Opening";
    private AnalyticsManager mAnalyticsManager = AnalyticsManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_opening);
        Button signInButton = (Button)findViewById(R.id.SignInBtn);
        mAuth = FirebaseAuth.getInstance();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignInActivity();
            }
        });
        mSkipButton = findViewById(R.id.SkipBtn);
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anonymouseLogin();
            }
        });
        remoteConfigInit();
        Button signUpButton = (Button)findViewById(R.id.SignUpBtn);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUpActivity();
            }
        });
        mAnalyticsManager.init(getApplicationContext());
        mAnalyticsManager.setUserID("Shahar Horn",true);    //..
        mAnalyticsManager.setUserProperty("enable_push_campaigns","true");//..
    }

    private void anonymouseLogin() {
        try {
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        String method = "Annonymouse SingIn";
                        mAnalyticsManager.trackLoginEvent(method);
                        updateUI();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, task.getException().getMessage(), task.getException());
                        Toast.makeText(MainActivityOpening.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                    // ...
                }
            });
        }catch (Exception e){
            Toast.makeText(MainActivityOpening.this, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {

        Intent intent = new Intent(MainActivityOpening.this, ItemsMenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void openSignUpActivity() {
        Intent intent = new Intent(this,SignUpActvity.class);
        startActivity(intent);
    }

    private void openSignInActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    private void remoteConfigInit() {

        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("allow_annoymous_user", false);
        mConfig = FirebaseRemoteConfig.getInstance();
        mConfig.setDefaults(defaults);
        mConfig.fetch(0).addOnCompleteListener(
                this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "onCompl`ete() Remote Config fetch isSuccessful=>>" + task.isSuccessful());
                        if(task.isSuccessful())
                            mConfig.activateFetched();
                    }
                });
        int visibility =  mConfig.getBoolean("allow_annoymous_user") ? View.VISIBLE : View.GONE;
        mSkipButton.setVisibility(visibility);

    }
}
