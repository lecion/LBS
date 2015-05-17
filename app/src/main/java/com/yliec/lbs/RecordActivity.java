package com.yliec.lbs;

import android.annotation.TargetApi;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yliec.lbs.bean.Track;
import com.yliec.lbs.util.L;

import java.util.ArrayList;
import java.util.List;


public class RecordActivity extends ActionBarActivity {
    private ListView lvRecord;
    private List<Track> trackList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        trackList = new ArrayList<>();
        trackList = Track.findAll(Track.class);
        lvRecord = (ListView) findViewById(R.id.lv_record);
        lvRecord.setAdapter(new RecordAdapter());
        Log.d("RecordActivity", trackList.get(0).getBeginTime()+"");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class RecordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return trackList.size();
        }

        @Override
        public Object getItem(int position) {
            return trackList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return trackList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = LayoutInflater.from(RecordActivity.this).inflate(R.layout.item_record, null);
                holder.tvCarNumber = (TextView) convertView.findViewById(R.id.tv_car_number);
                holder.tvBeginPlace = (TextView)convertView.findViewById(R.id.tv_start_place);
                holder.tvBeginTime = (TextView) convertView.findViewById(R.id.tv_start_time);
                holder.tvEndPlace = (TextView) convertView.findViewById(R.id.tv_end_place);
                holder.tvEndTime = (TextView) convertView.findViewById(R.id.tv_end_time);
                holder.btnPlay = (Button) convertView.findViewById(R.id.btn_play);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            final Track track = (Track) getItem(position);
            holder.tvCarNumber.setText(track.getCarNumber());
            holder.tvBeginPlace.setText(track.getBeginPlace());
            holder.tvBeginTime.setText(L.stamp2Date(track.getBeginTime()));
            holder.tvEndPlace.setText(track.getEndPlace());
            holder.tvEndTime.setText(L.stamp2Date(track.getEndTime()));
            holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(RecordActivity.this, MainActivity.class);
                    i.putExtra("track", track);
                    startActivity(i);
                }
            });
            return convertView;
        }

        public class ViewHolder{
            TextView tvCarNumber;
            TextView tvBeginPlace;
            TextView tvBeginTime;
            TextView tvEndPlace;
            TextView tvEndTime;
            Button btnPlay;
        }
    }
}
