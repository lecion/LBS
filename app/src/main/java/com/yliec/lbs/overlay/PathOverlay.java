package com.yliec.lbs.overlay;

import android.graphics.Color;
import android.graphics.Paint;

import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Projection;
import com.yliec.lbs.bean.PathInfo;

import java.util.List;

/**
 * Created by Lecion on 5/2/15.
 */
public class PathOverlay extends Overlay {
    private List<PathInfo> pathInfos = null;
    private Projection projection;
    private Paint paint;

    public PathOverlay(List<PathInfo> pathInfos, Projection projection) {
        this.pathInfos = pathInfos;
        this.projection = projection;
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(false);
        paint.setStrokeMiter(3);
        paint.setStyle(Paint.Style.STROKE);
    }




}
