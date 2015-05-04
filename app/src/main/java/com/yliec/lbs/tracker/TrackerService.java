package com.yliec.lbs.tracker;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.yliec.lbs.util.L;

import java.util.ArrayList;
import java.util.List;

public class TrackerService extends Service {

    public static final String TAG = "TrackerService";

    private LocationClient locationClient;

    private int scanSpan = 3000;

    Handler handler = new Handler();

    private boolean isStopLocClient = false;

    /**
     * 总路径
     */
    private List<LatLng> path;

    /**
     * 每次取最后四个点进行绘制
     */
    private List<LatLng> points_tem;

    private BaiduMap baiduMap;

    private boolean isFirstLocation = true;

    public TrackerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        initLocation();
        //启动定时器检测
        handler.postDelayed(new CheckGps(), 3000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        isStopLocClient = true;
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
        }
        super.onDestroy();
    }

    private void initLocation() {
        path = new ArrayList<>();
        //定位初始化
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(new MyLocationListener());

        //对locationClient进行一些配置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //默认每隔3秒进行请求
        option.setScanSpan(scanSpan);
        locationClient.setLocOption(option);
        locationClient.start();
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                return;
            }
            if (baiduMap == null) {
                baiduMap = L.app(TrackerService.this).getBaiduMap();
            }
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();

            if (isFirstLocation) {
                isFirstLocation = false;
            }
            LatLng point = new LatLng(latitude, longitude);
            if (point.latitude != 0 && point.longitude != 0) {
                //                Log.d(LOCATION_LOG, String.format("经度：%s, 纬度:%s", mLongtitude, mLatitude));
                addPointToPath(point);
                if (path.size() == 5) {
                    drawStart(path);
                } else if (path.size() > 7) {
                    points_tem = path.subList(path.size() - 4, path.size());
                    Log.d("drawPath", String.format("绘制:%s 到 %s 的点", path.size() - 4, path.size()) + "  " + points_tem.toString());
                    drawPath(path);
                }
            } else {
                Toast.makeText(TrackerService.this, "定位失败", Toast.LENGTH_LONG);
            }


        }
    }

    private void addPointToPath(LatLng point) {
        path.add(point);
    }

    private void drawPath(List<LatLng> points) {
        PolylineOptions polylineOptions = new PolylineOptions().points(points).color(Color.RED).width(7);
        baiduMap.addOverlay(polylineOptions);
        Log.d("drawPath", "绘制完成");
    }

    private void drawStart(List<LatLng> points) {
        double lat = 0.0;
        double lng = 0.0;

        for (LatLng ll : points) {
            lat += ll.latitude;
            lng += ll.longitude;
        }

        LatLng avePoint = new LatLng(lat / points.size(), lng / points.size());
        points.add(avePoint);
        baiduMap.addOverlay(new DotOptions().center(avePoint).color(Color.GREEN).radius(15));
    }

    private class CheckGps implements Runnable {
        @Override
        public void run() {
            if (!locationClient.isStarted()) {
                locationClient.start();
            }
            if (!isStopLocClient) {
                handler.postDelayed(this, 3000);
            }
        }
    }

}
