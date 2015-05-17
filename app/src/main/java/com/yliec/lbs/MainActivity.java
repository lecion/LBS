package com.yliec.lbs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.yliec.lbs.tracker.TrackerService;
import com.yliec.lbs.util.L;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String LOCATION_LOG = "LocationLog";

    private Button btnStart;

    private Button btnRecord;

    private EditText etCarNumber;

    private MapView mapView;

    private BaiduMap baiduMap;

    private LocationClient locationClient;

    private double mLatitude;

    private double mLongtitude;

    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //原创专栏
        //新媒体
        //BT
        //识货

        //左边重邮在线
        //幽幽黄桷兰

        initLocation();
        Log.d("MainActivity", "onCreate");
        if (getIntent() != null && getIntent().getExtras() != null) {
            findViewById(R.id.container).setVisibility(View.GONE);
            Track track = getIntent().getParcelableExtra("track");
            drawTrack(track);
        }
    }

    private void drawTrack(Track track) {
        List<LatLng> pointList= track.getPoints();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.points(pointList).width(7).color(Color.RED);
        baiduMap.addOverlay(polylineOptions);
        LatLng start = pointList.get(0);
        LatLng end = pointList.get(pointList.size() - 1);
        addStartPoint(start);
        addEndPoint(end);

    }
    private void addStartPoint(LatLng start) {
        OverlayOptions startOverlay = new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start))
                .zIndex(9);
        baiduMap.addOverlay(startOverlay);
    }

    private void addEndPoint(LatLng end) {
        OverlayOptions startOverlay = new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end))
                .zIndex(9);
        baiduMap.addOverlay(startOverlay);
    }

    private void initView() {
        btnRecord = (Button) findViewById(R.id.btn_records);
        btnStart = (Button) findViewById(R.id.btn_tracking);
        etCarNumber = (EditText) findViewById(R.id.et_car_number);
        btnStart.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
    }

    private void initLocation() {
        L.app(this).setBaiduMap(baiduMap);
        //开启定位图层
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(19);
        baiduMap.animateMapStatus(msu);

        //定位初始化
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(new MyLocationListener());

        //对locationClient进行一些配置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
//        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //每隔5秒进行请求
        option.setScanSpan(3000);

        locationClient.setLocOption(option);
        locationClient.start();

        //取消百度地图logo
        mapView.removeViewAt(1);
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
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng, 19);
        baiduMap.animateMapStatus(msu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (baiduMap != null) {
            baiduMap.setMyLocationEnabled(true);
            if (!locationClient.isStarted()) {
                locationClient.start();
            }
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
            case R.id.btn_records:
                if (L.app(this).isTracking()) {
                    Toast.makeText(this, "请先停止本次记录！", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(this, RecordActivity.class));
                }
                break;
            case R.id.btn_tracking:
                if (!TextUtils.isEmpty(etCarNumber.getText().toString())) {
                    if (!L.app(this).isTracking()) {
                        Toast.makeText(this, "行程记录开始...", Toast.LENGTH_SHORT).show();
                        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
                        startTrackerService();
                        L.app(this).setTracking(true);
                    } else {
                        stopTrackerService();
                        showFinishTrackingInfo();
                        L.app(this).setTracking(false);
                    }
                    updateTrackingBtnState();
                } else {
                    Toast.makeText(this, "请输入车牌号!", Toast.LENGTH_SHORT).show();
                }
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
                            startActivity(new Intent(MainActivity.this, RecordActivity.class));
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

    private void startTrackerService(){
        Intent intent = new Intent(this, TrackerService.class);
        intent.putExtra("car", etCarNumber.getText().toString());
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
            MyLocationData locationData = new MyLocationData.Builder().accuracy(10)
                    .direction(bdLocation.getDirection())
                    .latitude(mLatitude)
                    .longitude(mLongtitude).build();
            baiduMap.setMyLocationData(locationData);
            centerToMyLocation();
        }
    }

}
