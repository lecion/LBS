package com.yliec.lbs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.yliec.lbs.tracker.TrackerService;
import com.yliec.lbs.util.L;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String LOCATION_LOG = "LocationLog";

    private Button btnStart;

    private Button btnClear;

    private MapView mapView;

    private BaiduMap baiduMap;

    private LocationClient locationClient;

    private OverlayOptions polylineOptions;
    Overlay polyline = null;

    /**
     * 定位监听器
     */
    private MyLocationListener myLocationListener;

    private MyLocationConfiguration.LocationMode currentMode = MyLocationConfiguration.LocationMode.NORMAL;

    /**
     * 是否第一次定位
     */
    private volatile boolean isFirstLocation = true;

    private double mLatitude;

    private double mLongtitude;

    Handler handler = new Handler();
    private boolean isStopLocClient = false;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //启动定时器检测
        handler.postDelayed(new CheckGps(), 3000);
        initView();
        initLocation();
    }

    private void initView() {

        mapView = (MapView) findViewById(R.id.bmapView);
        //取消百度地图logo
        mapView.removeViewAt(1);
        baiduMap = mapView.getMap();
        L.app(this).setBaiduMap(baiduMap);

        btnStart = (Button) findViewById(R.id.btn_tracking);
        btnClear = (Button) findViewById(R.id.btn_clear);

        btnStart.setOnClickListener(this);
        btnClear.setOnClickListener(this);
    }

    private void initLocation() {
        //开启定位图层
        baiduMap.setMyLocationEnabled(true);
        //定位初始化
        locationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);

        //对locationClient进行一些配置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //每隔5秒进行请求
        option.setScanSpan(3000);

        locationClient.setLocOption(option);
        locationClient.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
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
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng, 20);
        baiduMap.animateMapStatus(msu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        baiduMap.setMyLocationEnabled(true);
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
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
            case R.id.btn_tracking:
                if (!L.app(this).isTracking()) {
                    startTrackerService();
                    L.app(this).setTracking(true);
                } else {
                    stopTrackerService();
                    showFinishTrackingInfo();
                    L.app(this).setTracking(false);
                }
                updateTrackingBtnState();
                break;
            case R.id.btn_clear:
                break;
        }
    }

    private void showFinishTrackingInfo() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this).setTitle("记录完成").setMessage("是否查看本次路径？")
                    .setNegativeButton("不要看", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("现在看", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
        }
        dialog.show();
    }

    private void stopTrackerService() {
        stopService(new Intent(this, TrackerService.class));
    }

    private void updateTrackingBtnState() {
        btnStart.setText(L.app(this).isTracking() ? getString(R.string.stop_tracking) : getString(R.string.start_tracking));
    }

    private void startTrackerService() {
        Intent intent = new Intent(this, TrackerService.class);
        startService(intent);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mapView == null) {
                return;
            }
            mLatitude = bdLocation.getLatitude();
            mLongtitude = bdLocation.getLongitude();
            MyLocationData locationData = new MyLocationData.Builder().accuracy(bdLocation.getRadius())
                    .latitude(mLatitude)
                    .longitude(mLongtitude).build();
            baiduMap.setMyLocationData(locationData);
            centerToMyLocation();
        }
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
