package com.yliec.lbs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.yliec.lbs.bean.Track;

import java.util.List;


public class ShowActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String LOCATION_LOG = "LocationLog";

    private MapView mapView;

    private BaiduMap baiduMap;

    private LocationClient locationClient;

    private double mLatitude;

    private double mLongtitude;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        //原创专栏
        //新媒体
        //BT
        //识货

        //左边重邮在线
        //幽幽黄桷兰

        initLocation();
        if (getIntent() != null) {
            if (getIntent().getParcelableExtra("track") != null) {
                Track track = getIntent().getParcelableExtra("track");
                drawTrack(track);
            }
            double latitude = getIntent().getDoubleExtra("latitude", -1);
            double longtitude = getIntent().getDoubleExtra("longtitude", -1);
            if (latitude != -1 && longtitude != -1) {
                drawPoint(latitude, longtitude);
            }
        }
    }

    private void drawPoint(double latitude, double longtitude) {
        LatLng latLng = new LatLng(latitude, longtitude);
        locationAt(latLng);
        OverlayOptions startOverlay = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka))
                .zIndex(9);
        baiduMap.addOverlay(startOverlay);
    }

    private void drawTrack(Track track) {
        List<LatLng> pointList= track.getPoints();
        locationAt(pointList.get(0));
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.points(pointList).width(7).color(Color.RED);
        baiduMap.addOverlay(polylineOptions);
        LatLng start = pointList.get(0);
        LatLng end = pointList.get(pointList.size() - 1);
        addStartPoint(start);
        addEndPoint(end);

    }
    private void addStartPoint(LatLng start) {
        OverlayOptions startOverlay = new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_st))
                .zIndex(9);
        baiduMap.addOverlay(startOverlay);
    }

    private void addEndPoint(LatLng end) {
        OverlayOptions startOverlay = new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_en))
                .zIndex(9);
        baiduMap.addOverlay(startOverlay);
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
    }

    private void initLocation() {
        //开启定位图层
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(19);
        baiduMap.animateMapStatus(msu);

//        //定位初始化
//        locationClient = new LocationClient(this);
//        locationClient.registerLocationListener(new MyLocationListener());
//
//        //对locationClient进行一些配置
//        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true);
//        option.setCoorType("bd09ll");
////        option.setIsNeedAddress(true);
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//        //每隔5秒进行请求
//        option.setScanSpan(3000);
//
//        locationClient.setLocOption(option);
//        locationClient.start();

        //取消百度地图logo
        mapView.removeViewAt(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            if (id == R.id.homeAsUp) {
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
            }
            return true;
        }

        if (id == R.id.menu_my_location) {
            centerToMyLocation();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 定位到我的位置
     */
    private void centerToMyLocation() {
        LatLng latLng = new LatLng(mLatitude, mLongtitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng, 19);
        baiduMap.animateMapStatus(msu);
    }

    /**
     * 定位到指定位置
     */
    private void locationAt(LatLng latLng) {
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng, 19);
        baiduMap.animateMapStatus(msu);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭定位
        baiduMap.setMyLocationEnabled(false);
        locationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mapView == null) {
                return;
            }
            mLatitude = bdLocation.getLatitude();
            mLongtitude = bdLocation.getLongitude();
            MyLocationData locationData = new MyLocationData.Builder().accuracy(10)
                    .direction(bdLocation.getDirection())
                    .latitude(mLatitude)
                    .longitude(mLongtitude).build();
            baiduMap.setMyLocationData(locationData);

        }
    }

}
