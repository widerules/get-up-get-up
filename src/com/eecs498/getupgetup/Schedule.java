package com.eecs498.getupgetup;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class Schedule {
    public static final String AUTHORITY = "com.eecs498.getupgetup";
    public static final Parcelable.Creator<Schedule> CREATOR =
                    new Parcelable.Creator<Schedule>() {
                        public Schedule createFromParcel(Parcel p) {
                            return new Schedule(p);
                        }

                        public Schedule[] newArray(int size) {
                            return new Schedule[size];
                        }
                    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeString(label);
        p.writeInt(enabled ? 1 : 0);
        // p.writeInt(hour);
        // p.writeInt(minutes);
        // p.writeInt(daysOfWeek.getCoded());
        // p.writeLong(time);
        // p.writeInt(vibrate ? 1 : 0);
        // p.writeParcelable(alert, flags);
        // p.writeInt(silent ? 1 : 0);
    }

    public String toString(Context context, int schedule_id) {
        Cursor c =
                        Alarms.getEnabledScheduleAlarmsCursor(
                                        context.getContentResolver(),
                                        schedule_id);

        String ret = new String();
        if (!c.moveToFirst())
            return ret;
        
        Alarm alarm;
        
        c.moveToFirst();
        do {
            alarm = new Alarm(c);
            ret += (alarm.day_text.charAt(0) == 'T') ? ("" + alarm.day_text.charAt(0) + alarm.day_text.charAt(1)) : ("" + alarm.day_text.charAt(0));
            ret += " " + alarm.hour + ":" + (alarm.minutes < 10 ? ("0" + alarm.minutes)
                            : alarm.minutes) + " ";
        } while (c.moveToNext());
        return ret;
    }

    public static class Columns implements BaseColumns {
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://"
                        + AUTHORITY + "/schedule");

        /**
         * Message to show when alarm triggers Note: not currently used
         * <P>
         * Type: STRING
         * </P>
         */
        public static final String LABEL = "label";

        /**
         * True if alarm is active
         * <P>
         * Type: BOOLEAN
         * </P>
         */
        public static final String ENABLED = "enabled";

        /**
         * Alarm time on Mondays in UTC milliseconds from the epoch.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String MONDAY = "monday";

        /**
         * Alarm time on Mondays in UTC milliseconds from the epoch.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String TUESDAY = "tuesday";

        /**
         * Alarm time on Mondays in UTC milliseconds from the epoch.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String WEDNESDAY = "wednesday";

        /**
         * Alarm time on Mondays in UTC milliseconds from the epoch.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String THURSDAY = "thursday";

        /**
         * Alarm time on Mondays in UTC milliseconds from the epoch.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String FRIDAY = "friday";

        /**
         * Alarm time on Mondays in UTC milliseconds from the epoch.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String SATURDAY = "saturday";

        /**
         * Alarm time on Mondays in UTC milliseconds from the epoch.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String SUNDAY = "sunday";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = _ID + " ASC";

        // Used when filtering enabled alarms.
        public static final String WHERE_ENABLED = ENABLED + "=1";

        static final String[] SCHEDULE_QUERY_COLUMNS = { _ID, LABEL, ENABLED,
                        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY,
                        SUNDAY };

        /**
         * These save calls to cursor.getColumnIndexOrThrow() THEY MUST BE KEPT
         * IN SYNC WITH ABOVE QUERY COLUMNS
         */
        public static final int SCHEDULE_ID_INDEX = 0;
        public static final int SCHEDULE_LABEL_INDEX = 1;
        public static final int SCHEDULE_ENABLED_INDEX = 2;
        public static final int SCHEDULE_MONDAY_INDEX = 3;
        public static final int SCHEDULE_TUESDAY_INDEX = 4;
        public static final int SCHEDULE_WEDNESDAY_INDEX = 5;
        public static final int SCHEDULE_THURSDAY_INDEX = 6;
        public static final int SCHEDULE_FRIDAY_INDEX = 7;
        public static final int SCHEDULE_SATURDAY_INDEX = 8;
        public static final int SCHEDULE_SUNDAY_INDEX = 9;
    }

    // ////////////////////////////
    // End column definitions
    // ////////////////////////////

    // Public fields
    public int id;
    public String label;
    public boolean enabled;
    public long monday;
    public long tuesday;
    public long wednesday;
    public long thursday;
    public long friday;
    public long saturday;
    public long sunday;

    public Schedule(Cursor c) {
        id = c.getInt(Columns.SCHEDULE_ID_INDEX);
        label = c.getString(Columns.SCHEDULE_LABEL_INDEX);
        enabled = c.getInt(Columns.SCHEDULE_ENABLED_INDEX) == 1;
        monday = c.getInt(Columns.SCHEDULE_MONDAY_INDEX);
        tuesday = c.getInt(Columns.SCHEDULE_TUESDAY_INDEX);
        wednesday = c.getInt(Columns.SCHEDULE_WEDNESDAY_INDEX);
        thursday = c.getInt(Columns.SCHEDULE_THURSDAY_INDEX);
        friday = c.getInt(Columns.SCHEDULE_FRIDAY_INDEX);
        saturday = c.getInt(Columns.SCHEDULE_SATURDAY_INDEX);
        sunday = c.getInt(Columns.SCHEDULE_SUNDAY_INDEX);
    }

    public Schedule(Parcel p) {
        id = p.readInt();
        label = p.readString();
        enabled = p.readInt() == 1;
        monday = p.readLong();
        tuesday = p.readLong();
        wednesday = p.readLong();
        thursday = p.readLong();
        friday = p.readLong();
        saturday = p.readLong();
        sunday = p.readLong();
    }

}
