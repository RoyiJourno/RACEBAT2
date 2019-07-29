package com.example.royi.racebet;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class UserInGroup implements Parcelable {
    private String Name; //user id
    private String stepCount;

    public UserInGroup(String Name,String stepCount){
        this.stepCount=stepCount;
        this.Name=Name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStepCount() {
        return stepCount;
    }

    public void setStepCount(String stepCount) {
        this.stepCount = stepCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Name);
        out.writeString(stepCount);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<UserInGroup> CREATOR = new Parcelable.Creator<UserInGroup>() {
        public UserInGroup createFromParcel(Parcel in) {
            return new UserInGroup(in);
        }

        public UserInGroup[] newArray(int size) {
            return new UserInGroup[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private UserInGroup(Parcel in) {
        Name = in.readString();
        stepCount = in.readString();

    }
}



