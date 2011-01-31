package com.eecs498.getupgetup;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.DigitalClock;
import android.widget.ListView;
import android.widget.TextView;

public class DaysAndTimes extends Activity implements OnItemClickListener {
    private Cursor mCursor;
    private Schedule mSchedule;
    private ListView mDaysAndTimesList;
    private LayoutInflater mFactory;
    private int mId;

    static final int TIME_DIALOG_ID = 0;

    private class DaysAndTimesAdapter extends CursorAdapter {
        public DaysAndTimesAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View ret = mFactory.inflate(R.layout.alarm_info, parent, false);
            return ret;
        }

        public void bindView(View view, Context context, Cursor cursor) {
            final Alarm alarm = new Alarm(cursor);

            TextView alarm_time = (TextView) view.findViewById(R.id.alarm_time);
            String alarm_time_str =
                            "" + alarm.hour + ":" + (alarm.minutes < 10 ? ("0" + alarm.minutes)
                                            : alarm.minutes);
            alarm_time.setText(alarm_time_str);

            TextView day_text = (TextView) view.findViewById(R.id.day_of_week);
            day_text.setText(alarm.day_text);

            CheckBox on_button =
                            (CheckBox) view.findViewById(R.id.alarm_enable_button);
            on_button.setChecked(alarm.enabled);

            on_button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    boolean is_checked = cb.isChecked();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        mId = i.getIntExtra(Schedules.SCHEDULE_ID, -1);
        mFactory = LayoutInflater.from(this);
        // mSchedule = Schedules.getSchedule(getContentResolver(), mId);
        // mCursor = Schedules.getSchedulesCursor(getContentResolver());
        mCursor = Alarms.getAlarmsCursor(getContentResolver());

        setContentView(R.layout.days_and_times);
        mDaysAndTimesList = (ListView) findViewById(R.id.days_and_times_list);
        mDaysAndTimesList.setAdapter(new DaysAndTimesAdapter(this, mCursor));
        mDaysAndTimesList.setVerticalScrollBarEnabled(true);
        mDaysAndTimesList.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView parent, View v, int pos, long id) {
        showDialog(0);
        Intent intent = new Intent(this, SetSchedule.class);
        intent.putExtra(Schedules.SCHEDULE_ID, (int) id);
        startActivity(intent);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                // TODO show the time picker
        }
        return null;
    }
}
