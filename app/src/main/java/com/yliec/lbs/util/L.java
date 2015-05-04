package com.yliec.lbs.util;

import android.app.Activity;

import com.yliec.lbs.MyApplication;

/**
 * Created by Lecion on 5/5/15.
 */
public class L {
    public static MyApplication app(Activity aty) {
        return (MyApplication)aty.getApplication();
    }
}
