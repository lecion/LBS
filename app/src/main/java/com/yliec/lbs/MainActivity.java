package com.yliec.lbs;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
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
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final String LOCATION_LOG = "LocationLog";
    private MapView mapView;

    private BaiduMap baiduMap;

    private LocationClient locationClient;

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
    private BMapManager mBMapMan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBMapMan=new BMapManager(getApplication());
        mBMapMan.init(new MKGeneralListener() {
            @Override
            public void onGetPermissionState(int i) {

            }
        });
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
//        baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
//        baiduMap.setTrafficEnabled(true);
        initMyLocation();
        drawOverlay();
    }

    private void drawOverlay() {
        List<LatLng> latLngs = new ArrayList<>();
        PolylineOptions polylineOptions = new PolylineOptions();
        LatLng p1 = point(106.531491,29.574657);
        LatLng p2 = point(106.531792,29.574948);
        LatLng p3 = point(106.532062,29.57512);
        LatLng p4 = point(106.532596,29.575317);
        LatLng p5 = point(106.533566,29.575458);
        LatLng p6 = point(106.533899,29.575246);
        LatLng p7 = point(106.534119,29.57505);
        LatLng p8 = point(106.534375,29.574873);
        LatLng p9 = point(106.534766,29.574881);
        latLngs.add(p1);
        latLngs.add(p2);
        latLngs.add(p3);
        latLngs.add(p4);
        latLngs.add(p5);
        latLngs.add(p6);
        latLngs.add(p7);
        latLngs.add(p8);
        latLngs.add(p9);

        polylineOptions.points(latLngs).color(Color.RED).width(7);

        baiduMap.addOverlay(polylineOptions);
    }

    public LatLng point(double latitude, double longtitude) {
        return new LatLng(longtitude, latitude);
    }

    private void initMyLocation() {
        locationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        //对locationClient进行一些配置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09II");
        option.setIsNeedAddress(true);
        //每隔5秒进行请求
        option.setScanSpan(5000);
        locationClient.setLocOption(option);
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
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
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

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mapView == null) {
                return;
            }
            MyLocationData locationData = new MyLocationData.Builder().accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            baiduMap.setMyLocationData(locationData);
            mLatitude = bdLocation.getLatitude();
            mLongtitude = bdLocation.getLongitude();
            Log.d(LOCATION_LOG, String.format("经度：%s, 纬度:", mLongtitude, mLatitude));
            if (isFirstLocation) {
               centerToMyLocation();
                isFirstLocation = false;
                Toast.makeText(MainActivity.this, bdLocation.getAddrStr(), Toast.LENGTH_LONG).show();
            }
        }
    }


}
