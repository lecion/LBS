package com.yliec.lbs.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.widget.Toast;

import com.yliec.lbs.MyApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static String date2Stamp(String dateStr) {
        String time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日H时m分");
        Date d;
        try {

            d = sdf.parse(dateStr);
            long l = d.getTime();
            String str = String.valueOf(l);
            time = str.substring(0, 10);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static void t(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}
