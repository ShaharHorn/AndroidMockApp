package com.dev.android.mta.androiduserauthentication.implementaion;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dev.android.mta.androiduserauthentication.Activities.SignUpActvity;
import com.dev.android.mta.androiduserauthentication.Model.AnalyticsManager;
import com.dev.android.mta.androiduserauthentication.interfaces.IEmailAndPasswordAuthentication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ori on 4/25/2018.
 */

public class EmailAndPasswordAuthentication extends AuthenticationMethod implements IEmailAndPasswordAuthentication {

    private String mUsserName;
    private AnalyticsManager mAnalyticsManager = AnalyticsManager.getInstance();



    public EmailAndPasswordAuthentication(FirebaseAuth iAuth, AppCompatActivity iMainActivity){
        super(iAuth, iMainActivity );
        this.mAuth = iAuth;
        this.mMainActivity = iMainActivity;
        mUsserName = null;
    }

    @Override
    public void SignUp(String userName, String email, String password, String  confirmPassword, String phoneNumber) {
        if (isValidName(userName)){
            mUsserName = userName;
            if(confirmPassword.equals(password)){
                try {
                    ((SignUpActvity) mMainActivity).StartProggressDialog();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(mMainActivity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        mAuth.getCurrentUser()
                                                .sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        ((SignUpActvity) mMainActivity).StopProggressDialog();
                                                        if(task.isSuccessful()){
                                                            String method = "Sign Up" +" - " +mAuth.getCurrentUser().getEmail();
                                                            mAnalyticsManager.trackSignupEvent(method);
                                                            Toast.makeText(mMainActivity, "verifiction mail sent to " + mAuth.getCurrentUser().getEmail(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            Toast.makeText(mMainActivity, "Failed to send verifiction mail.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                      //  mAnalyticsManager.setUserID(mAuth.getCurrentUser().getUid(),true);
                                                      //  mAnalyticsManager.setUserProperty("email",mAuth.getCurrentUser().getEmail());
                                                    }
                                                });
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if(user != null){
                                            updateUI(user);
                                        }


                                    }else{
                                        ((SignUpActvity) mMainActivity).StopProggressDialog();
                                        Toast.makeText(mMainActivity, task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }catch (Exception e){
                    e.getMessage();
                }
            }else {
                Toast.makeText(mMainActivity, "Your password is not match with the confirm password! .",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateUI(FirebaseUser user)
    {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mUsserName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
            mAuth.signOut();


    }
    @Override
    public void SignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mMainActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String method = "Email and Password SignIn " +" - " +mAuth.getCurrentUser().getEmail();
                            mAnalyticsManager.trackLoginEvent(method);
                            startNewActivityWithUserDetails(mAuth.getCurrentUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mMainActivity, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidName(String userName) {
        String usernamePattern = "^[a-z0-9]{3,15}$";
        if(userName.isEmpty()) {
            Toast.makeText(mMainActivity, "Please fill in your name .",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            Pattern pattern = Pattern.compile(usernamePattern,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(userName.toString());
            if (!matcher.matches())
            {
                Toast.makeText(mMainActivity, "User name is not in the correct pattern (3-15 chat lenght,only numbers and letter) .",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

}


