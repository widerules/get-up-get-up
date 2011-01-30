package com.eecs498.getupgetup;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.FrameLayout;
import android.widget.ListView;

public class SetSchedule extends PreferenceActivity {
    private EditTextPreference mLabel;

    private int mId;

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

        // load schedule details from database
        Schedule schedule = Schedules.getSchedule(getContentResolver(), mId);
        mLabel.setText(schedule.label);
        mLabel.setSummary(schedule.label);
    }
    
    private void saveSchedule() {
        final String label = mLabel.getText();
        
    }
}
