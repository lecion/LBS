package com.yliec.lbs;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RunActivity extends Activity implements OnLongClickListener {

    MapView bmapView;
    BaiduMap map;
    // 地图相关的类
    LocationClient locationClient;
    LocationClientOption clientOption;
    private LatLng point;
    private MarkerOptions gpsOptions;
    private Overlay gps;
    boolean isMyLocationEnable = true;
    StringBuilder lalongBulider = new StringBuilder();
    ArrayList<LatLng> path = new ArrayList<LatLng>();
    boolean isStop = false;
    LatLng startPoint = null;
    String startAddress;
    LatLng currentPoint = null;
    LatLng lastPoint = null;
    OverlayOptions polylineOption;
    Overlay polyline;

    private MarkerOptions startOptions;
    private Overlay startOverlay;

    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (!isStop) {
                currentPoint = new LatLng(location.getLatitude(),
                        location.getLongitude());
                // 首次获得ＧＰＳ
                if (startPoint == null) {
                    startPoint = currentPoint;
                    lastPoint = startPoint;
                    path.add(currentPoint);
                    if (location.getAddrStr() != null) {
                        startAddress = location.getAddrStr();
                    }
                    drawStart(startPoint);
                } else {
//							caculateDistance(p);
                    path.add(currentPoint);
//                    drawCurrent(currentPoint);
                    lastPoint = currentPoint;
                    if (polyline != null) {
                        polyline.remove();
                        polyline = null;
                    }
                    polylineOption = new PolylineOptions()
                            .points(path)
                            .width(7)
                            .color(Color.RED);
                    polyline = map.addOverlay(polylineOption);
                }

                if (isMyLocationEnable) {
                    map.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(currentPoint, 19));
                }
            }
        }
    };

    //
    private void caculateDistance(LatLng p) {
        double d = DistanceUtil.getDistance(lastPoint, p);
        totalDistance += d / (double) 1000;
        DecimalFormat format = new DecimalFormat("##0.00");

//        tv_distance.setText(format.format(totalDistance));
//        tv_speed.setText(format.format(totalDistance / secondCount * 3600));
//        tv_energy.setText(format.format(50 * totalDistance));

    }

    // draw current point
    private void drawCurrent(LatLng p) {
        Log.d("drawCurrent", p.toString());
        if (gps != null) {
            gps.remove();
        }
        gpsOptions = new MarkerOptions().draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon)).position(p);
        gps = map.addOverlay(gpsOptions);

    }

    // draw startPoint
    private void drawStart(LatLng startPoint) {
        if (startOverlay != null) {
            startOverlay.remove();
        }
        startOptions = new MarkerOptions()
                .draggable(true)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon))
                .position(startPoint);
        startOverlay = map.addOverlay(startOptions);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bmapView = (MapView) findViewById(R.id.bmapView);
        bmapView.setDrawingCacheEnabled(true);
        bmapView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        bmapView.showZoomControls(false);
        map = bmapView.getMap();
        isMyLocationEnable = true;
        initMap();
//		start();
    }

    private int secondCount;
    private double totalDistance;

    private void initMap() {
        locationClient = new LocationClient(this);
        clientOption = new LocationClientOption();
        clientOption.setScanSpan(1000);
        clientOption.setIsNeedAddress(true);
        clientOption.setLocationMode(LocationMode.Hight_Accuracy);
        clientOption.setCoorType("bd09ll");
        clientOption.setOpenGps(true);
        locationClient.setLocOption(clientOption);
        locationClient.registerLocationListener(new MyLocationListener());
        locationClient.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
        bmapView.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bmapView != null) {
            locationClient.stop();
            bmapView.onDestroy();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bmapView.onPause();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Util.toastMsg("请先结束运动");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        return true;
    }

}
