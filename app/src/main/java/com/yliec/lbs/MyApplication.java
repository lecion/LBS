package com.yliec.lbs;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Lecion on 4/22/15.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);

    }
}
