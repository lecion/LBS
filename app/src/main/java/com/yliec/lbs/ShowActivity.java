package com.yliec.lbs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.yliec.lbs.bean.Track;
import com.yliec.lbs.util.L;

import java.util.List;

/**
 * 路径回访界面
 */
public class ShowActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String LOCATION_LOG = "LocationLog";

    private MapView mapView;

    private BaiduMap baiduMap;

//    private LocationClient locationClient;

    private double mLatitude;

    private double mLongtitude;

    private Track track;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initLocation();
        if (getIntent() != null) {
            //判断是否是路径回访
            if (getIntent().getParcelableExtra("track") != null) {
                track = getIntent().getParcelableExtra("track");
                //绘制路径
                drawTrack(track);
            } else {
                //获取经纬度
                double latitude = getIntent().getDoubleExtra("latitude", -1);
                double longtitude = getIntent().getDoubleExtra("longtitude", -1);
                if (latitude != -1 && longtitude != -1) {
                    //绘制点
                    drawPoint(latitude, longtitude);
                }
            }
        }
    }

    /**
     * 根据经纬度在地图上绘制点
     * @param latitude
     * @param longtitude
     */
    private void drawPoint(double latitude, double longtitude) {
        LatLng latLng = new LatLng(latitude, longtitude);
        locationAt(latLng);
        OverlayOptions startOverlay = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka))
                .zIndex(9);
        baiduMap.addOverlay(startOverlay);
    }

    /**
     * 根据路径对象在地图上绘制路径
     * @param track
     */
    private void drawTrack(Track track) {
        //得到路径中的Point的list
        List<LatLng> pointList= track.getPoints();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.points(pointList).width(7).color(Color.RED);
        //根据得到的list调用百度地图api绘制路径
        baiduMap.addOverlay(polylineOptions);
        //地图定位到起点显示
        locationAt(pointList.get(0));
        LatLng start = pointList.get(0);
        LatLng end = pointList.get(pointList.size() - 1);
        //添加起点
        addStartPoint(start);
        //添加结束点
        addEndPoint(end);

    }

    /**
     * 添加起点图标
     * @param start
     */
    private void addStartPoint(LatLng start) {
        OverlayOptions startOverlay = new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_st))
                .zIndex(9);
        baiduMap.addOverlay(startOverlay);
    }

    /**
     * 添加结束点图标
     * @param end
     */
    private void addEndPoint(LatLng end) {
        OverlayOptions startOverlay = new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_en))
                .zIndex(9);
        baiduMap.addOverlay(startOverlay);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
//        findViewById(R.id.btn_screen_shot).setOnClickListener(this);
        //开启定位图层
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(19);
        baiduMap.animateMapStatus(msu);
        mapView.removeViewAt(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
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

        if (id == R.id.menu_share) {
            //截图并分享
            baiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    L.share(ShowActivity.this, bitmap, "好友分享的路径");
                }
            });
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
//        locationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_screen_shot:
//                final String fileName = System.currentTimeMillis() + ".png";
//
//                baiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
//                    @Override
//                    public void onSnapshotReady(Bitmap bitmap) {
//                        L.share(ShowActivity.this, bitmap, "好友分享的路径");
//                    }
//                });
//                break;
        }
    }

}
