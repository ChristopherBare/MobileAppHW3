package com.christopherbare.mobileapphw3;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Comment implements Parcelable{
    String comment;
    String user;
    Date time;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(comment);
        parcel.writeString(user);
        parcel.writeSerializable(time);
    }

    protected Comment(Parcel in){
        comment = in.readString();
        user = in.readString();
        time = (Date) in.readSerializable();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>(){
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
