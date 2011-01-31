package com.eecs498.getupgetup;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class Alarm implements Parcelable {
    public static final String AUTHORITY = "com.eecs498.getupgetup";
    public static final Parcelable.Creator<Alarm> CREATOR =
                    new Parcelable.Creator<Alarm>() {
                        public Alarm createFromParcel(Parcel p) {
                            return new Alarm(p);
                        }

                        public Alarm[] newArray(int size) {
                            return new Alarm[size];
                        }
                    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeInt(schedule_id);
        p.writeString(day_text);
        p.writeInt(enabled ? 1 : 0);
        p.writeInt(hour);
        p.writeInt(minutes);
        p.writeInt(daysOfWeek.getCoded());
        p.writeLong(time);
    }

    public static class Columns implements BaseColumns {
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://"
                        + AUTHORITY + "/alarm");

        /**
         * Hour in 24-hour localtime 0 - 23.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String SCHEDULE_ID = "schedule_id";

        /**
         * name of the day
         * <P>
         * Type: STRING
         * </P>
         */
        public static final String DAY_TEXT = "day_text";

        /**
         * Hour in 24-hour localtime 0 - 23.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String HOUR = "hour";

        /**
         * Minutes in localtime 0 - 59
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String MINUTES = "minutes";

        /**
         * Days of week coded as integer
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String DAYS_OF_WEEK = "daysofweek";

        /**
         * Alarm time in UTC milliseconds from the epoch.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String ALARM_TIME = "alarmtime";

        /**
         * True if alarm is active
         * <P>
         * Type: BOOLEAN
         * </P>
         */
        public static final String ENABLED = "enabled";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = _ID + " ASC";

        // Used when filtering enabled alarms.
        public static final String WHERE_ENABLED = ENABLED + "=1";

        static final String[] ALARM_QUERY_COLUMNS = { _ID, SCHEDULE_ID, DAY_TEXT, HOUR,
                        MINUTES, DAYS_OF_WEEK, ALARM_TIME, ENABLED };

        /**
         * These save calls to cursor.getColumnIndexOrThrow() THEY MUST BE KEPT
         * IN SYNC WITH ABOVE QUERY COLUMNS
         */
        public static final int ALARM_ID_INDEX = 0;
        public static final int ALARM_SCHEDULE_ID_INDEX = 1;
        public static final int ALARM_DAY_TEXT_ID_INDEX = 2;
        public static final int ALARM_HOUR_INDEX = 3;
        public static final int ALARM_MINUTES_INDEX = 4;
        public static final int ALARM_DAYS_OF_WEEK_INDEX = 5;
        public static final int ALARM_TIME_INDEX = 6;
        public static final int ALARM_ENABLED_INDEX = 7;
    }

    // Public fields
    public int id;
    public int schedule_id;
    public String day_text;
    public boolean enabled;
    public int hour;
    public int minutes;
    public DaysOfWeek daysOfWeek;
    public long time;

    public Alarm(Cursor c) {
        id = c.getInt(Columns.ALARM_ID_INDEX);
        schedule_id = c.getInt(Columns.ALARM_SCHEDULE_ID_INDEX);
        day_text = c.getString(Columns.ALARM_DAY_TEXT_ID_INDEX);
        enabled = c.getInt(Columns.ALARM_ENABLED_INDEX) == 1;
        hour = c.getInt(Columns.ALARM_HOUR_INDEX);
        minutes = c.getInt(Columns.ALARM_MINUTES_INDEX);
        daysOfWeek = new DaysOfWeek(c.getInt(Columns.ALARM_DAYS_OF_WEEK_INDEX));
        time = c.getLong(Columns.ALARM_TIME_INDEX);
    }

    public Alarm(Parcel p) {
        id = p.readInt();
        schedule_id = p.readInt();
        day_text = p.readString();
        enabled = p.readInt() == 1;
        hour = p.readInt();
        minutes = p.readInt();
        daysOfWeek = new DaysOfWeek(p.readInt());
        time = p.readLong();
    }

    /*
     * Days of week code as a single int. 0x00: no day 0x01: Monday 0x02:
     * Tuesday 0x04: Wednesday 0x08: Thursday 0x10: Friday 0x20: Saturday 0x40:
     * Sunday
     */
    static final class DaysOfWeek {

        private static int[] DAY_MAP = new int[] { Calendar.MONDAY,
                        Calendar.TUESDAY, Calendar.WEDNESDAY,
                        Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY,
                        Calendar.SUNDAY, };

        // Bitmask of all repeating days
        private int mDays;

        DaysOfWeek(int days) {
            mDays = days;
        }

        public String toString(Context context, boolean showNever) {
            StringBuilder ret = new StringBuilder();

            // no days
            if (mDays == 0) {
                return showNever ? context.getText(R.string.never).toString()
                                : "";
            }

            // every day
            if (mDays == 0x7f) {
                return context.getText(R.string.every_day).toString();
            }

            // count selected days
            int dayCount = 0, days = mDays;
            while (days > 0) {
                if ((days & 1) == 1)
                    dayCount++;
                days >>= 1;
            }

            // short or long form?
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] dayList =
                            (dayCount > 1) ? dfs.getShortWeekdays() : dfs
                                            .getWeekdays();

            // selected days
            for (int i = 0; i < 7; i++) {
                if ((mDays & (1 << i)) != 0) {
                    ret.append(dayList[DAY_MAP[i]]);
                    dayCount -= 1;
                    if (dayCount > 0)
                        ret.append(context.getText(R.string.day_concat));
                }
            }
            return ret.toString();
        }

        private boolean isSet(int day) {
            return ((mDays & (1 << day)) > 0);
        }

        public int getCoded() {
            return mDays;
        }

        public int getNextAlarm(Calendar c) {
            if (mDays == 0) {
                return -1;
            }

            int today = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7;

            int day = 0;
            int dayCount = 0;
            for (; dayCount < 7; dayCount++) {
                day = (today + dayCount) % 7;
                if (isSet(day)) {
                    break;
                }
            }
            return dayCount;
        }
    }
}
