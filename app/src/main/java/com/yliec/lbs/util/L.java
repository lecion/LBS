package com.yliec.lbs.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yliec.lbs.MyApplication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具类
 * Created by Lecion on 5/5/15.
 */
public class L {
    public static MyApplication app(Activity aty) {
        return (MyApplication)aty.getApplication();
    }

    public static MyApplication app(Service service) {
        return (MyApplication)service.getApplication();
    }

    /**
     * 时间戳转换为日期
     * @param timeStamp 时间戳
     * @param pattern 转换格式
     * @return
     */
    public static String stamp2Date(long timeStamp, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(timeStamp * 1000);
    }

    /**
     * 时间戳转换为日期
     * @param timeStamp 时间戳
     * @return
     */
    public static String stamp2Date(long timeStamp) {
        return stamp2Date(timeStamp, "MM月dd日 HH:mm");
    }

    /**
     * 日期转换为时间戳
     * @param dateStr 日期字符串
     * @return
     */
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

    /**
     * 封装的Toast
     * @param context
     * @param str
     */
    public static void t(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    /**
     * 进行屏幕截图
     * @param aty
     * @return
     */
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

    /**
     * 保存图片到本地
     * @param aty
     * @param bitmap
     * @param fileName
     * @return
     */
    public static boolean savePic(Activity aty, Bitmap bitmap, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = aty.openFileOutput(fileName, Context.MODE_PRIVATE);
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
        return savePic(aty, takeScreenShot(aty), System.currentTimeMillis() + ".png");
    }

    /**
     * 分享截图
     * @param aty
     * @param fileName
     * @param text
     */
    public static void shareAct(Activity aty, String fileName, String text) {
        Uri uri = null;
        try {
            FileInputStream fis = aty.openFileInput(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            uri = Uri.parse(MediaStore.Images.Media.insertImage(aty.getContentResolver(), bitmap, null, null));
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_SUBJECT, "路径分享");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        aty.startActivity(Intent.createChooser(intent, aty.getTitle()));
    }

    /**
     * 分享截图
     * @param aty
     * @param bitmap
     * @param text
     */
    public static void shareAct(Activity aty, Bitmap bitmap, String text) {
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(aty.getContentResolver(), bitmap, null, null));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_SUBJECT, "路径分享");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        aty.startActivity(Intent.createChooser(intent, aty.getTitle()));
    }

    /**
     * 进行分享
     * @param aty
     * @param bitmap
     * @param text
     */
    public static void share(Activity aty, Bitmap bitmap, String text) {
        String fileName = System.currentTimeMillis() + ".png";
//        savePic(aty, bitmap, fileName);
        shareAct(aty, bitmap, text);
    }
}
