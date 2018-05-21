package com.dev.android.mta.androiduserauthentication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shahar on 04/05/2018.
 */

public class User implements Parcelable {

    private String email;
    private String totalPurchase;
    private List<Item> myCars = new ArrayList<>();

    public User() {
    }

    public User(String email, String totalPurchase, List<Item> myCars) {
        this.email = email;
        this.totalPurchase = totalPurchase;
        if(myCars == null){
            myCars =new ArrayList<>();
        }
        else {
            this.myCars = myCars;
        }
    }


    public String getEmail() {
        return email;
    }
    public String getTotalPurchase() {
        return totalPurchase;
    }

    public List<Item> getMyCars() {return myCars;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeList(myCars);
        parcel.writeString(totalPurchase);
    }

    public User(Parcel in) {
        this.email = in.readString();
        this.totalPurchase = in.readString();
        this.myCars = in.readArrayList(ClassLoader.getSystemClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void upgdateTotalPurchase(String price) {
        Integer temp;
        if( totalPurchase==null || totalPurchase.isEmpty()) {
            temp=0;
        }
        else{
            temp = Integer.parseInt(totalPurchase);
        }
        temp+= Integer.parseInt(price);
        totalPurchase = temp.toString();
    }
}
