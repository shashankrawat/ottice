package com.ottice.ottice.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shashank.rawat on 13-07-2017.
 */

public class ReviewRatingBeanClass implements Parcelable{

    private String username;
    private String rating;
    private String reviews;

    public ReviewRatingBeanClass(){}

    protected ReviewRatingBeanClass(Parcel in) {
        username = in.readString();
        rating = in.readString();
        reviews = in.readString();
    }

    public static final Creator<ReviewRatingBeanClass> CREATOR = new Creator<ReviewRatingBeanClass>() {
        @Override
        public ReviewRatingBeanClass createFromParcel(Parcel in) {
            return new ReviewRatingBeanClass(in);
        }

        @Override
        public ReviewRatingBeanClass[] newArray(int size) {
            return new ReviewRatingBeanClass[size];
        }
    };

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviews() {
        return reviews;
    }
    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(rating);
        parcel.writeString(reviews);
    }
}
