package com.dev.android.mta.androiduserauthentication.interfaces;

/**
 * Created by Ori on 4/25/2018.
 */

public interface IEmailAndPasswordAuthentication {
    public void SignUp(String userName, String email, String password, String confirmPassword, String phoneNumber);
    public void SignIn(String email, String password);
}
