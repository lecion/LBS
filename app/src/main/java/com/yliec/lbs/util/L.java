package com.yliec.lbs.util;

import android.app.Activity;
import android.app.Service;

import com.yliec.lbs.MyApplication;

import java.text.SimpleDateFormat;

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

    public static String stamp2Date(long timeStamp, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(timeStamp);
    }

    public static String stamp2Date(long timeStamp) {
        return stamp2Date(timeStamp, "MM月dd日 HH:mm");
    }
}
