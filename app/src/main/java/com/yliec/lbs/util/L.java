package com.yliec.lbs.util;

import android.app.Activity;
import android.app.Service;

import com.yliec.lbs.MyApplication;

/**
 * Created by Lecion on 5/5/15.
 */
public class L {
    public static MyApplication app(Activity aty) {
        return (MyApplication)aty.getApplication();
    }

    public static MyApplication app(Service service) {
        return (MyApplication)service.getApplication();
    }
}
