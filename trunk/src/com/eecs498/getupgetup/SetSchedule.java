package com.eecs498.getupgetup;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class SetSchedule extends PreferenceActivity {
    private int mId;
    private EditTextPreference mLabel;
    private PreferenceScreen mDaysAndTimes;
    private boolean mEnabled;

    // Back up the times
    private long mMonday;
    private long mTuesday;
    private long mWednesday;
    private long mThursday;
    private long mFriday;
    private long mSaturday;
    private long mSunday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.schedule_prefs);

        // This has to be here to get the save/cancel buttons to highlight on
        // their own.
        getListView().setItemsCanFocus(true);

        // Get each pref so we can retrieve the value later.
        mLabel = (EditTextPreference) findPreference("schedule_label");
        mLabel.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference,
                            Object newValue) {
                // Set the summary based on the new label
                preference.setSummary((String) newValue);
                return true;
            }
        });
        
        Intent i = getIntent();
        mId = i.getIntExtra(Schedules.SCHEDULE_ID, -1);
        
        mDaysAndTimes = (PreferenceScreen) findPreference("schedule_days_and_times_screen");
        mDaysAndTimes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            
            public boolean onPreferenceClick(Preference preference) {
                // TODO make a new view for alarms
                Intent i = new Intent(SetSchedule.this, DaysAndTimes.class);
                i.putExtra(Schedules.SCHEDULE_ID, mId);
                startActivity(i);
                return true;
            }
        });


        // load schedule details from database
        Schedule schedule = Schedules.getSchedule(getContentResolver(), mId);
        mLabel.setText(schedule.label);
        mLabel.setSummary(schedule.label);

        mEnabled = schedule.enabled;
        // Back up the alarm times
        mMonday = schedule.monday;
        mTuesday = schedule.tuesday;
        mWednesday = schedule.wednesday;
        mThursday = schedule.thursday;
        mFriday = schedule.friday;
        mSaturday = schedule.saturday;
        mSunday = schedule.sunday;
        
        

    }

    @Override
    public void onBackPressed() {
        saveSchedule();
        finish();
    }

    private void saveSchedule() {
        final String label = mLabel.getText();
        Schedules.setSchedule(this, mId, label, mEnabled, mMonday, mTuesday,
                        mWednesday, mThursday, mFriday, mSaturday, mSunday);

        // TODO popup with a toast
    }

}
