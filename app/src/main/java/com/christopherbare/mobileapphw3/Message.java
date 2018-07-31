package com.christopherbare.mobileapphw3;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Message implements Parcelable{
    String message;
    String imageURL;
    String user;
    Date time;
    ArrayList<Comment> comments;
    boolean isPicture;
    String key;

    public Message() {
    }

    public Message(String message, String user, Date time, boolean isPicture){
        this.message = message;
        this.user = user;
        this.time = time;
        this.isPicture = isPicture;
    }

    public Message(String imageURL, String user, Date time){
        this.imageURL = imageURL;
        this.user = user;
        this.time = time;
        this.isPicture = true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public boolean isPicture() {
        return isPicture;
    }

    public void setPicture(boolean picture) {
        isPicture = picture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeString(imageURL);
        parcel.writeString(user);
        parcel.writeSerializable(time);
        parcel.writeSerializable(comments);
    }

    protected Message(Parcel in){
        message = in.readString();
        imageURL = in.readString();
        user = in.readString();
        time = (Date) in.readSerializable();
        comments = (ArrayList<Comment>) in.readSerializable();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>(){
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
