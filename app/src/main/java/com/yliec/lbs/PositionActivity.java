package com.yliec.lbs;

import android.annotation.TargetApi;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.yliec.lbs.bean.Track;
import com.yliec.lbs.util.L;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;


public class PositionActivity extends ActionBarActivity implements View.OnClickListener{

    private TextView tvTime;
    private TextView tvDate;
    private TextView tvSecond;
    private Button btnQuery;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int seconds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        timePicker = new TimePicker(this);
        datePicker = new DatePicker(this);
        datePicker.init(year, month, day, null);

        tvTime = (TextView) findViewById(R.id.tv_time);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvSecond = (TextView) findViewById(R.id.tv_second);
        btnQuery = (Button) findViewById(R.id.btn_query);
        tvTime.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.homeAsUp) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
            } else {
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, upIntent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_query:
                if (TextUtils.isEmpty(tvDate.getText()) || TextUtils.isEmpty(tvTime.getText()) || TextUtils.isEmpty(tvSecond.getText())) {
                    Toast.makeText(this, "不选择完整时间让人家怎么查~", Toast.LENGTH_LONG).show();
                } else {
                    int second = Integer.parseInt(tvSecond.getText().toString());
                    if (second > 60 || second < 0) {
                        L.t(this, "你家的表的秒针能走出" + second + "秒 o.o?");
                        return;
                    }
                    this.seconds = second;
                    String time = String.format("%s年%s月%s日%s时%s分%s秒", year, month, day, hour, minute, seconds);
                    Log.d("query", "查询点" + L.date2Stamp(time));

                    List<Track> track = Track.where("begintime < ? and endtime > ?", L.date2Stamp(time), L.date2Stamp(time)).find(Track.class);
                    if (track != null && track.size() > 0) {
                        Cursor cursor = DataSupport.findBySQL("select * from point where track_id = ? order by abs(timestamp - ?) asc", String.valueOf(track.get(0).getId()), L.date2Stamp(time));
                        if (cursor.moveToNext()) {
                            //找到一个最近的点
                            String result = String.valueOf(cursor.getLong(cursor.getColumnIndex("timestamp")));
                            Log.d("findPoint", "找到点在路径" + track.get(0).getId() + " " + result);
                            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                            double longtitude = cursor.getDouble(cursor.getColumnIndex("longtitude"));
                            Intent i = new Intent(this, ShowActivity.class);
                            i.putExtra("latitude", latitude);
                            i.putExtra("longtitude", longtitude);
                            startActivity(i);
                        } else {
                            L.t(this, "没有找到这个时间的位置哟~");
                        }
                    } else {
                        L.t(this, "没有找到这个时间的位置哟~");
                    }

                }


                break;

            case R.id.tv_date:
                if (datePicker.getParent() != null) {
                    ((ViewGroup)datePicker.getParent()).removeView(datePicker);
                }
                new MaterialDialog.Builder(this)
                        .title("日期")
                        .customView(datePicker, false)
                        .positiveText("确定")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                if (datePicker.getParent() != null) {
                                    ((ViewGroup)datePicker.getParent()).removeView(datePicker);
                                }
                                year = datePicker.getYear();
                                month = datePicker.getMonth() + 1;
                                day = datePicker.getDayOfMonth();
                                tvDate.setText(year + "-" + month+ "-" + day);
                            }
                        }).show();
                break;

            case R.id.tv_time:
                if (timePicker.getParent() != null) {
                    ((ViewGroup)timePicker.getParent()).removeView(timePicker);
                }
                new MaterialDialog.Builder(this)
                        .title("日期")
                        .customView(timePicker, false)
                        .positiveText("确定")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                if (timePicker.getParent() != null) {
                                    ((ViewGroup)timePicker.getParent()).removeView(timePicker);
                                }
                                hour = timePicker.getCurrentHour();
                                minute = timePicker.getCurrentMinute();
                                tvTime.setText(hour + ":"+ minute);
                            }
                        }).show();
                break;
        }
    }
}
