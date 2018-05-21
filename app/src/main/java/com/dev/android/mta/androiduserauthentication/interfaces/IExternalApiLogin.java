package com.dev.android.mta.androiduserauthentication.interfaces;

import android.content.Intent;
import android.os.Parcelable;

/**
 * Created by Ori on 5/2/2018.
 */

public interface IExternalApiLogin {
    void SingIn(Parcelable SignInDetails);
    void Init();
    void onSignInResult(int requestCode, int resultCode, Intent data);
}
