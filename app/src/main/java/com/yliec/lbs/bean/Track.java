package com.yliec.lbs.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lecion on 5/15/15.
 */
public class Track implements Parcelable{
    private long id = -1;
    private long beginTime;
    private long endTime;
    private double distance;
    private String beginPlace;
    private String endPlace;

    public Track(long id, long beginTime, long endTime, double distance) {
        this.id = id;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.distance = distance;
    }

    public Track() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getBeginPlace() {
        return beginPlace;
    }

    public void setBeginPlace(String beginPlace) {
        this.beginPlace = beginPlace;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(beginTime);
        dest.writeLong(endTime);
        dest.writeDouble(distance);
        dest.writeString(beginPlace);
        dest.writeString(endPlace);
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    private Track(Parcel in) {
        id = in.readLong();
        beginTime = in.readLong();
        endTime = in.readLong();
        distance = in.readDouble();
        beginPlace = in.readString();
        endPlace = in.readString();
    }
}
