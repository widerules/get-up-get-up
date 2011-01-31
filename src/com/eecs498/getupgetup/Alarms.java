package com.eecs498.getupgetup;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateFormat;

public class Alarms {
    private final static String DM12 = "E h:mm aa";
    private final static String DM24 = "E k:mm";
    private final static String[] DAYS_TEXT = { "Monday", "Tuesday",
                    "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

    /**
     * Creates a new Alarm.
     */
    public static Uri addAlarm(ContentResolver contentResolver, int schedule_id, String day_text) {
        ContentValues values = new ContentValues();
        values.put(Alarm.Columns.SCHEDULE_ID, schedule_id);
        values.put(Alarm.Columns.DAY_TEXT, day_text);
        values.put(Alarm.Columns.HOUR, 8);
        return contentResolver.insert(Alarm.Columns.CONTENT_URI, values);
    }

    /**
     * Creates alarms for each day of the week
     */
    public static void addAlarms(ContentResolver contentResolver,
                    int schedule_id) {
        for (String s : DAYS_TEXT)
            addAlarm(contentResolver, schedule_id, s);
    }

    /**
     * Queries all alarms
     * 
     * @return cursor over all alarms
     */
    public static Cursor getAlarmsCursor(ContentResolver contentResolver) {
        return contentResolver.query(Alarm.Columns.CONTENT_URI,
                        Alarm.Columns.ALARM_QUERY_COLUMNS, null, null,
                        Alarm.Columns.DEFAULT_SORT_ORDER);
    }

    // Private method to get a more limited set of alarms from the database.
    private static Cursor getFilteredAlarmsCursor(
                    ContentResolver contentResolver) {
        return contentResolver.query(Alarm.Columns.CONTENT_URI,
                        Alarm.Columns.ALARM_QUERY_COLUMNS,
                        Alarm.Columns.WHERE_ENABLED, null, null);
    }

    /**
     * Return an Alarm object representing the alarm id in the database. Returns
     * null if no alarm exists.
     */
    public static Alarm getAlarm(ContentResolver contentResolver, int alarmId) {
        Cursor cursor =
                        contentResolver.query(ContentUris.withAppendedId(
                                        Alarm.Columns.CONTENT_URI, alarmId),
                                        Alarm.Columns.ALARM_QUERY_COLUMNS,
                                        null, null, null);
        Alarm alarm = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                alarm = new Alarm(cursor);
            }
            cursor.close();
        }
        return alarm;
    }

    /**
     * A convenience method to set an alarm in the Alarms content provider.
     * 
     * @param id
     *            corresponds to the _id column
     * @param enabled
     *            corresponds to the ENABLED column
     * @param hour
     *            corresponds to the HOUR column
     * @param minutes
     *            corresponds to the MINUTES column
     * @param daysOfWeek
     *            corresponds to the DAYS_OF_WEEK column
     * @param time
     *            corresponds to the ALARM_TIME column
     * @param vibrate
     *            corresponds to the VIBRATE column
     * @param message
     *            corresponds to the MESSAGE column
     * @param alert
     *            corresponds to the ALERT column
     */
    public static void setAlarm(Context context, int id, int schedule_id,
                    boolean enabled, int hour, int minutes,
                    Alarm.DaysOfWeek daysOfWeek) {

        ContentValues values = new ContentValues(8);
        ContentResolver resolver = context.getContentResolver();
        // Set the alarm_time value if this alarm does not repeat. This will be
        // used later to disable expired alarms.
        long time = 0;

        time = calculateAlarm(hour, minutes, daysOfWeek).getTimeInMillis();

        values.put(Alarm.Columns.SCHEDULE_ID, schedule_id);
        values.put(Alarm.Columns.ENABLED, enabled ? 1 : 0);
        values.put(Alarm.Columns.HOUR, hour);
        values.put(Alarm.Columns.MINUTES, minutes);
        values.put(Alarm.Columns.ALARM_TIME, time);
        values.put(Alarm.Columns.DAYS_OF_WEEK, daysOfWeek.getCoded());
        resolver.update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI,
                        id), values, null, null);

        // TODO setNextAlert(context);
    }

    public static void enableAlarm(final Context context, final int id,
                    boolean enabled) {
        enableAlarmInternal(context, id, enabled);
        // TODO setNextAlert(context);
    }

    private static void enableAlarmInternal(final Context context,
                    final int id, boolean enabled) {
        enableAlarmInternal(context,
                        getAlarm(context.getContentResolver(), id), enabled);
    }

    private static void enableAlarmInternal(final Context context,
                    final Alarm alarm, boolean enabled) {
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues(2);
        values.put(Alarm.Columns.ENABLED, enabled ? 1 : 0);

        // If we are enabling the alarm, calculate alarm time since the time
        // value in Alarm may be old.
        if (enabled) {
            long time = 0;

            time =
                            calculateAlarm(alarm.hour, alarm.minutes,
                                            alarm.daysOfWeek).getTimeInMillis();

            values.put(Alarm.Columns.ALARM_TIME, time);
        }

        resolver.update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI,
                        alarm.id), values, null, null);
    }

    public static Alarm calculateNextAlert(final Context context) {
        Alarm alarm = null;
        long minTime = Long.MAX_VALUE;
        long now = System.currentTimeMillis();
        Cursor cursor = getFilteredAlarmsCursor(context.getContentResolver());
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Alarm a = new Alarm(cursor);
                    // A time of 0 indicates this is a repeating alarm, so
                    // calculate the time to get the next alert.
                    if (a.time == 0) {
                        a.time =
                                        calculateAlarm(a.hour, a.minutes,
                                                        a.daysOfWeek)
                                                        .getTimeInMillis();
                    }
                    else if (a.time < now) {
                        // Expired alarm, disable it and move along.
                        enableAlarmInternal(context, a, false);
                        continue;
                    }
                    if (a.time < minTime) {
                        minTime = a.time;
                        alarm = a;
                    }
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }
        return alarm;
    }

    /**
     * Disables non-repeating alarms that have passed. Called at boot.
     */
    public static void disableExpiredAlarms(final Context context) {
        Cursor cur = getFilteredAlarmsCursor(context.getContentResolver());
        long now = System.currentTimeMillis();

        if (cur.moveToFirst()) {
            do {
                Alarm alarm = new Alarm(cur);
                // A time of 0 means this alarm repeats. If the time is
                // non-zero, check if the time is before now.
                if (alarm.time != 0 && alarm.time < now) {
                    enableAlarmInternal(context, alarm, false);
                }
            }
            while (cur.moveToNext());
        }
        cur.close();
    }

    static Calendar calculateAlarm(int hour, int minute,
                    Alarm.DaysOfWeek daysOfWeek) {

        // start with now
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        int nowHour = c.get(Calendar.HOUR_OF_DAY);
        int nowMinute = c.get(Calendar.MINUTE);

        // if alarm is behind current time, advance one day
        if (hour < nowHour || hour == nowHour && minute <= nowMinute) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        int addDays = daysOfWeek.getNextAlarm(c);
        /*
         * Log.v("** TIMES * " + c.getTimeInMillis() + " hour " + hour +
         * " minute " + minute + " dow " + c.get(Calendar.DAY_OF_WEEK) +
         * " from now " + addDays);
         */
        if (addDays > 0)
            c.add(Calendar.DAY_OF_WEEK, addDays);
        return c;
    }

    static String formatTime(final Context context, int hour, int minute,
                    Alarm.DaysOfWeek daysOfWeek) {
        Calendar c = calculateAlarm(hour, minute, daysOfWeek);
        return formatDayAndTime(context, c);
    }

    /**
     * Shows day and time -- used for lock screen
     */
    private static String formatDayAndTime(final Context context, Calendar c) {
        String format = get24HourMode(context) ? DM24 : DM12;
        return (c == null) ? "" : (String) DateFormat.format(format, c);
    }

    /**
     * @return true if clock is set to 24-hour mode
     */
    static boolean get24HourMode(final Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }
}
