package com.yliec.lbs.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yliec.lbs.MyApplication;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
        return format.format(timeStamp * 1000);
    }

    public static String stamp2Date(long timeStamp) {
        return stamp2Date(timeStamp, "MM月dd日 HH:mm");
    }

    public static String date2Stamp(String dateStr) {
        String time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日H时m分s秒");
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

    public static Bitmap takeScreenShot(Activity aty) {
        Bitmap bitmap = null;
        View view = aty.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);
        int statusHeight  = frame.top;
        Log.d("takeScreenShot", "状态栏高度:" + statusHeight);
        Point point = new Point();
        aty.getWindowManager().getDefaultDisplay().getSize(point);
        int width = point.x;
        int height = point.y;
        bitmap = Bitmap.createBitmap(bitmap, 0, statusHeight, width, height - statusHeight);
        return bitmap;
    }

    private static boolean savePic(Bitmap bitmap, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean shotBitmap(Activity aty) {
        return savePic(takeScreenShot(aty), "sdcard/" + System.currentTimeMillis() + ".png");
    }
}
