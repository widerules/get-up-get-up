package com.eecs498.getupgetup;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

public class DaysAndTimes extends Activity implements
                TimePickerDialog.OnTimeSetListener {
    private Cursor mCursor;
    private Schedule mSchedule;
    private ListView mDaysAndTimesList;
    private LayoutInflater mFactory;
    private int mId;
    
    private int mAlarmId;
    private int mAlarmHourOfDay;
    private int mAlarmMinutes;
    private boolean mAlarmEnabled;
    private Alarm.DaysOfWeek mAlarmDayOfWeek;

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
                            ""
                                            + alarm.hour
                                            + ":"
                                            + (alarm.minutes < 10 ? ("0" + alarm.minutes)
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
                    Alarms.enableAlarm(DaysAndTimes.this, alarm.id, is_checked);
                }
            });

            view.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    new TimePickerDialog(
                                    DaysAndTimes.this,
                                    DaysAndTimes.this,
                                    alarm.hour,
                                    alarm.minutes,
                                    DateFormat.is24HourFormat(DaysAndTimes.this)).show();
                    mAlarmId = alarm.id;
                    mAlarmEnabled = alarm.enabled;
                    mAlarmDayOfWeek = alarm.daysOfWeek;
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateLayout();
    }
    
    public void updateLayout() {
        Intent i = getIntent();
        mId = i.getIntExtra(Schedules.SCHEDULE_ID, -1);
        mFactory = LayoutInflater.from(this);
        mCursor = Alarms.getScheduleAlarmsCursor(getContentResolver(), mId);

        setContentView(R.layout.days_and_times);
        mDaysAndTimesList = (ListView) findViewById(R.id.days_and_times_list);
        mDaysAndTimesList.setAdapter(new DaysAndTimesAdapter(this, mCursor));
        mDaysAndTimesList.setVerticalScrollBarEnabled(true);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Since alarm was set user intends to use that alarm
        boolean enable_alarm = true;
        Alarms.setAlarm(this, mAlarmId, mId, enable_alarm, hourOfDay, minute, mAlarmDayOfWeek);
    }
}
