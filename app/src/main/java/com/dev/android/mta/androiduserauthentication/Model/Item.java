package com.dev.android.mta.androiduserauthentication.Model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class Item implements Parcelable {
    private String carColor;
    private String carImage;
    private String carMake;
    private String carModel;
    private String carModelYear;
    private String price;
    private String key = "";
    private Map<String,Review> reviews;

    public String getKey() {
        return key;
    }

    public Map<String, Review> getReviews() {
        return reviews;
    }

    public void setReviews(Map<String, Review> reviews) {
        this.reviews = reviews;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Item(String carColor, String carImage, String carMake, String carModel, String carModelYear, String price, Map<String,Review> reviews) {
        this.carColor = carColor;
        this.carImage = carImage;
        this.carMake = carMake;
        this.carModel = carModel;
        this.carModelYear = carModelYear;
        this.price = price;
        this.reviews = reviews;
    }
    public Item(){}

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarModelYear() { return carModelYear;}

    public void setCarModelYear(String carModelYear) {
        this.carModelYear = carModelYear;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(carColor);
        parcel.writeString(carImage);
        parcel.writeString(carMake);
        parcel.writeString(carModel);
        parcel.writeString(carModelYear);
        parcel.writeString(price);
    }
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }
        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
        private Item(Parcel in){
            this.carColor = in.readString();
            this.carImage = in.readString();
            this.carMake = in.readString();
            this.carModel = in.readString();
            this.carModelYear = in.readString();
            this.price = in.readString();
        }

}