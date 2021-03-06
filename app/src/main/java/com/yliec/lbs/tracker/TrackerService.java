package com.yliec.lbs.tracker;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.yliec.lbs.R;
import com.yliec.lbs.bean.Point;
import com.yliec.lbs.bean.Track;
import com.yliec.lbs.util.L;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 主要的路径记录服务类，开启服务后间隔一定时间在后台记录每一个位置，并连接成为一条路径
 */
public class TrackerService extends Service {

    public static final String TAG = "TrackerService";

    private LocationClient locationClient;

    private int scanSpan = 3000;

    Handler handler = new Handler();

    private boolean isStopLocClient = false;

    private Track track;

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

    private List<LatLng> line;

    private LatLng curPoint;

    private LatLng lastPoint;

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
        track = new Track();
        line = new LinkedList<>();
        initLocation();
        //启动定时器检测
        handler.postDelayed(new CheckGps(), 10000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        track.setCarNumber(intent.getStringExtra("car"));
        track.setBeginTime(System.currentTimeMillis() / 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        addEndPoint(curPoint);
        saveTrack();
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
        option.setAddrType("all");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //默认每隔3秒进行请求
        option.setScanSpan(scanSpan);
        locationClient.setLocOption(option);
        //启动定位客户端
        locationClient.start();
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                return;
            }

            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            //获得当前点
            curPoint = new LatLng(latitude, longitude);
            if (baiduMap == null) {
                baiduMap = L.app(TrackerService.this).getBaiduMap();
            }

            Log.d("MyLocationListener", bdLocation.getAddrStr() + "  " + bdLocation.getStreet());

            if (curPoint.latitude != 0 && curPoint.longitude != 0) {
                String place = TextUtils.isEmpty(bdLocation.getAddrStr()) ? "未知" : bdLocation.getAddrStr();
                if (!isFirstLocation) {
                    //如果是不是第一次定位，则绘制当前点和上一次点连接的路径
                    drawLine();
                    MyLocationData locData = new MyLocationData.Builder().
                            accuracy(10)
                            .direction(bdLocation.getDirection())
                            .latitude(latitude)
                            .longitude(longitude).build();
                    baiduMap.setMyLocationData(locData);
                    track.setEndPlace(place);
                } else {
                    isFirstLocation = false;
                    //是第一次定位，仅添加当前点作为起始点
                    addStartPoint(curPoint);
                    track.setBeginPlace(place);
                }
                //记录本次定位的点
                lastPoint = curPoint;
                //                Log.d(LOCATION_LOG, String.format("经度：%s, 纬度:%s", mLongtitude, mLatitude));
//                addPointToTrack(latitude, longitude);
//                addPointToPath(curPoint);
//                if (path.size() == 5) {
//                    drawStart(path);
//                } else if (path.size() > 7) {
//                    points_tem = path.subList(path.size() - 4, path.size());
//                    Log.d("drawPath", String.format("绘制:%s 到 %s 的点", path.size() - 4, path.size()) + "  " + points_tem.toString());
//                    drawPath(path);
//                }
            } else {
                Toast.makeText(TrackerService.this, "定位失败", Toast.LENGTH_LONG);
            }

        }
    }

    /**
     * 添加开始点
     * @param start
     */
    private void addStartPoint(LatLng start) {
        OverlayOptions startOverlay = new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start))
                .zIndex(9);
        baiduMap.addOverlay(startOverlay);
    }

    /**
     * 添加结束点
     * @param end
     */
    private void addEndPoint(LatLng end) {
        OverlayOptions startOverlay = new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end))
                .zIndex(9);
        baiduMap.addOverlay(startOverlay);
    }

    /**
     * 将点存入路径，并录入数据库
     * @param latitude
     * @param longitude
     */
    private void addPointToTrack(double latitude, double longitude) {
        //存点
        Point point = new Point();
        point.setLatitude(latitude);
        point.setLongtitude(longitude);
        point.setTimestamp(System.currentTimeMillis() / 1000);
        point.save();
        track.getPointList().add(point);
    }

    private void addPointToPath(LatLng point) {
        path.add(point);
    }

    /**
     * 绘制两个点连接的线段
     */
    private void drawLine() {
        //地点变化之后才绘制线段
        if (curPoint != lastPoint) {
            addPointToTrack(curPoint.latitude, curPoint.longitude);
            line.clear();
            line.add(curPoint);
            line.add(lastPoint);
            Log.d("drawPath", String.format("绘制:%s 和 %s ", lastPoint, curPoint));
            track.setDistance(track.getDistance()+ DistanceUtil.getDistance(curPoint, lastPoint));
            PolylineOptions polylineOptions = new PolylineOptions().points(line).color(Color.RED).width(7);
            baiduMap.addOverlay(polylineOptions);
        }
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

    /**
     * 保存本次记录到数据库
     */
    private void saveTrack() {
        track.setEndTime(System.currentTimeMillis() / 1000);
        track.save();
    }

    /**
     * 每3秒检查定位客户端，以防服务终止而不能继续定位
     */
    private class CheckGps implements Runnable {
        @Override
        public void run() {
            if (!isStopLocClient) {
                if (!locationClient.isStarted()) {
                    locationClient.start();
                }
                handler.postDelayed(this, 3000);
            } else {
                if (locationClient.isStarted()) {
                    locationClient.stop();
                }
            }
        }
    }


}
