package com.yliec.lbs.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by lecion on 15-5-16.
 */
public class Point extends DataSupport {
    private long id;
    private double latitude;
    private double longtitude;
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
}
