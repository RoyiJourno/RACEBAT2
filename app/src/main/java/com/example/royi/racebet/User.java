package com.example.royi.racebet;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class User implements Parcelable {

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;
    private String uuid;
    private String name;
    private String phone;
    private String photoPath;
    private List<String> groupList;

    public User(String token,String uuid,String name,String phone,String PhotoPath,List<String> groupList){
        this.uuid = uuid;
        this.token = token;
        this.name = name;
        this.phone = phone;
        this.photoPath = PhotoPath;
        this.groupList = groupList;
    }
    public User(){

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public List<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(token);
        out.writeString(uuid);
        out.writeString(name);
        out.writeString(phone);
        out.writeString(photoPath);
        out.writeList(groupList);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private User(Parcel in) {
        token = in.readString();
        uuid = in.readString();
        name = in.readString();
        phone = in.readString();
        photoPath = in.readString();
        in.readList(groupList,List.class.getClassLoader());
    }
}
