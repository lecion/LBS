package com.yliec.lbs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yliec.lbs.bean.Track;
import com.yliec.lbs.util.L;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


public class RecordActivity extends ActionBarActivity {
    private ListView lvRecord;
    private List<Track> trackList;
    private RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        trackList = new ArrayList<>();
        trackList = Track.order("id desc").find(Track.class);
        lvRecord = (ListView) findViewById(R.id.lv_record);
        recordAdapter = new RecordAdapter();
        lvRecord.setAdapter(recordAdapter);
        lvRecord.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("RecordActivity", recordAdapter.getItem(position).toString());
                new DeleteDialog(position).show(getSupportFragmentManager(), "delete");
                return false;
            }
        });
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
        // automatically handle clicks on the Home/Up btnShowStart, so long
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
                    Intent i = new Intent(RecordActivity.this, ShowActivity.class);
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

    public class DeleteDialog extends DialogFragment {
        private int position;
        public DeleteDialog(int position) {
            this.position = position;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(RecordActivity.this).setTitle("确认删除该记录？").setNegativeButton("不忍下手", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setPositiveButton("残忍删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doDelete();
                }

                private void doDelete() {
                    Track track = (Track) recordAdapter.getItem(position);
                    if (track != null) {
                        if (DataSupport.delete(Track.class, track.getId()) > 0) {
                            trackList.remove(position);
                            recordAdapter.notifyDataSetChanged();
                            Toast.makeText(RecordActivity.this, "删除成功", Toast.LENGTH_SHORT);
                        }
                    }
                }
            }).create();
        }


    }

}
