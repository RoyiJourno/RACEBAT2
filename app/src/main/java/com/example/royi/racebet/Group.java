package com.example.royi.racebet;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group implements Parcelable {
    public String getGruopID() {
        return gruopID;
    }

    public void setGruopID(String gruopID) {
        this.gruopID = gruopID;
    }

    private String gruopID;
    private String name;
    private String durtion;
    private String max_users;
    private String adminID;
    private String betPrice;

    public ArrayList<UserInGroup> getGroupUser() {
        return groupUser;
    }

    public void setGroupUser(ArrayList<UserInGroup> groupUser) {
        this.groupUser = groupUser;
    }

    private ArrayList<UserInGroup> groupUser;

    public Group(){

    }

    public Group(String gruopID,String name,String durtion,String max_users,String betPrice,String adminID,ArrayList<UserInGroup> groupUser){
        this.gruopID=gruopID;
        this.betPrice=betPrice;
        this.name=name;
        this.durtion=durtion;
        this.max_users=max_users;
        this.adminID=adminID;
        this.groupUser=groupUser;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDurtion() {
        return durtion;
    }

    public void setDurtion(String durtion) {
        this.durtion = durtion;
    }

    public String getMax_users() {
        return max_users;
    }

    public void setMax_users(String max_users) {
        this.max_users = max_users;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getBetPrice() {
        return betPrice;
    }

    public void setBetPrice(String betPrice) {
        this.betPrice = betPrice;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(gruopID);
        out.writeString(name);
        out.writeString(durtion);
        out.writeString(max_users);
        out.writeString(betPrice);
        out.writeString(adminID);
        //out.writeArray(groupUser);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Group(Parcel in) {
        gruopID = in.readString();
        name = in.readString();
        durtion = in.readString();
        max_users = in.readString();
        betPrice = in.readString();
        adminID = in.readString();
        //in.readList(HashMap.class.getClassLoader());
    }
}
