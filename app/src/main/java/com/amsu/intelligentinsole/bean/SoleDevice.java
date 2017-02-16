package com.amsu.intelligentinsole.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 2016/12/23.
 */
public class SoleDevice implements Parcelable {
    String name;
    String state;
    String mac;
    String LEName;


    public SoleDevice(String name, String state) {
        this.name = name;
        this.state = state;
    }


    public SoleDevice(String name, String state, String mac) {
        this.name = name;
        this.state = state;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(state);
        dest.writeString(mac);
    }

    public static final Creator<SoleDevice> CREATOR = new Creator<SoleDevice>() {
        @Override
        public SoleDevice createFromParcel(Parcel source) {
            return new SoleDevice(source.readString(),source.readString(),source.readString());
        }

        @Override
        public SoleDevice[] newArray(int size) {
            return new SoleDevice[size];
        }
    };


    public SoleDevice(String name, String state, String mac, String LEName) {
        this.name = name;
        this.state = state;
        this.mac = mac;
        this.LEName = LEName;
    }

    public String getLEName() {
        return LEName;
    }

    public void setLEName(String LEName) {
        this.LEName = LEName;
    }
}
