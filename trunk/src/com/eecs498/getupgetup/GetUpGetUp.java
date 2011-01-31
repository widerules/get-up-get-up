package com.eecs498.getupgetup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GetUpGetUp extends Activity implements OnItemClickListener {

    final static String PREFERENCES = "GetUpGetUp";

    private SharedPreferences mPrefs;
    private LayoutInflater mFactory;
    private ListView mSchedulesList;
    private Cursor mCursor;

    private class ScheduleAdapter extends CursorAdapter {
        public ScheduleAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View ret = mFactory.inflate(R.layout.schedule_info, parent, false);
            return ret;
        }

        public void bindView(View view, Context context, Cursor cursor) {
            final Schedule schedule = new Schedule(cursor);

            CheckBox on_button =
                            (CheckBox) view.findViewById(R.id.schedule_enable_button);
            on_button.setChecked(schedule.enabled);
            on_button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    boolean is_checked = ((CheckBox) v).isChecked();
                    Schedules.enableSchedule(GetUpGetUp.this, schedule.id,
                                    is_checked);
                    // TODO show a toast about when all the alarms are going to
                    // go off
                }
            });

            // The name of the schedule gets set
            TextView schedule_label =
                            (TextView) view.findViewById(R.id.schedule_label);
            if (schedule.label != null && schedule.label.length() != 0) {
                schedule_label.setText(schedule.label);
                schedule_label.setVisibility(View.VISIBLE);
            }
            else {
                schedule_label.setVisibility(View.GONE);
            }

            // The schedule days and times get set
            TextView days_and_times_view =
                            (TextView) view.findViewById(R.id.days_and_times);
            final String days_and_times_str =
                            schedule.toString(GetUpGetUp.this, false);
            schedule.toString(GetUpGetUp.this, false);
            if (days_and_times_str != null && days_and_times_str.length() != 0) {
                days_and_times_view.setText(days_and_times_str);
                days_and_times_view.setVisibility(View.VISIBLE);
            }
            else {
                days_and_times_view.setVisibility(View.GONE);
            }

        }
    }

    /** private static final int ACTIVITY_CREATE_SCHEDULE = 0;
    private static final int ACTIVITY_SHOW_NIGHT_CLOCK = 1;
    private static final int ACTIVITY_PLAY_GAME = 2;
    
	private static final int ADD_SCHEDULE_ID = Menu.FIRST;
    private static final int SHOW_NIGHT_CLOCK_ID = Menu.FIRST + 1;
    private static final int PLAY_GAME_ID = Menu.FIRST + 2;*/
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFactory = LayoutInflater.from(this);
        mPrefs = getSharedPreferences(PREFERENCES, 0);
        mCursor = Schedules.getSchedulesCursor(getContentResolver());

        updateLayout();
    }

    private void updateLayout() {
        setContentView(R.layout.get_up_get_up);
        mSchedulesList = (ListView) findViewById(R.id.schedules_list);
        mSchedulesList.setAdapter(new ScheduleAdapter(this, mCursor));
        mSchedulesList.setVerticalScrollBarEnabled(true);
        mSchedulesList.setOnItemClickListener(this);
        mSchedulesList.setOnCreateContextMenuListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.addSchedule:
        	createSchedule();
            return true;
        case R.id.nightClock:
            displayNightClock();
            return true;
        case R.id.playGame:
        	playGame();
            return true; 
        case R.id.quickNap:
        	quickNap();
            return true; 
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void createSchedule() {
        Uri uri = Schedules.addSchedule(getContentResolver());
        
        String segment = uri.getPathSegments().get(1);
        int newId = Integer.parseInt(segment);
        Intent intent = new Intent(this, SetSchedule.class);
        Alarms.addAlarms(getContentResolver(), newId);
        intent.putExtra(Schedules.SCHEDULE_ID, newId);  
        startActivity(intent);
    }
    
    private void displayNightClock() {
        Intent intent = new Intent(this, NightClock.class);
        startActivity(intent);
    }
    
    private void playGame() {
        Intent intent = new Intent(this, Slider.class);
        startActivity(intent);
    }
    
    private void quickNap() {
        Intent alrm_intent = new Intent(this, alarmReceiver.class); 
        AlarmManager alm=(AlarmManager)getSystemService(ALARM_SERVICE); 
        PendingIntent alrm_pending=PendingIntent.getBroadcast(this, 0 , alrm_intent, 0);                         
        alm.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+10000 ,alrm_pending);
    }

    public void onItemClick(AdapterView parent, View v, int pos, long id) {
        Intent intent = new Intent(this, SetSchedule.class);
        intent.putExtra(Schedules.SCHEDULE_ID, (int) id);
        startActivity(intent);
    }

}

