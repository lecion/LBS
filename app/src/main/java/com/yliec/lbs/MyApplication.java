package com.yliec.lbs;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;

/**
 * Created by Lecion on 4/22/15.
 */
public class MyApplication extends Application{

    private BaiduMap baiduMap;

    private boolean isTracking = false;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }

    public void setBaiduMap(BaiduMap baiduMap) {
        this.baiduMap = baiduMap;
    }

    public BaiduMap getBaiduMap() {
        return baiduMap;
    }

    public boolean isTracking() {
        return isTracking;
    }

    public void setTracking(boolean isTracking) {
        this.isTracking = isTracking;
    }
}
