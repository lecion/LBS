package com.yliec.lbs.bean;

import org.litepal.crud.DataSupport;

/**
 * 用于表示一个地图上的一个点，由经度纬度确定
 * Created by lecion on 15-5-16.
 */
public class Point extends DataSupport {
    private long id;
    private double latitude;
    private double longtitude;
    private long timestamp;
    private Track track;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
