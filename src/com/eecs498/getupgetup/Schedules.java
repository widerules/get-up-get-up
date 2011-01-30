package com.eecs498.getupgetup;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Schedules {

    // This string is used to identify the alarm id passed to SetAlarm from the
    // list of alarms.
    public static final String SCHEDULE_ID = "schedule_id";

    /**
     * Creates a new Schedule.
     */
    public static Uri addSchedule(ContentResolver contentResolver) {
        ContentValues values = new ContentValues();
        values.put(Schedule.Columns.ENABLED, true);
        values.put(Schedule.Columns.LABEL, "New Schedule");
        return contentResolver.insert(Schedule.Columns.CONTENT_URI, values);
    }

    public static void enableSchedule(final Context context, final int id,
                    boolean enabled) {
        enableScheduleInternal(context, id, enabled);
    }

    private static void enableScheduleInternal(final Context context,
                    final int id, boolean enabled) {
        enableScheduleInternal(context,
                        getSchedule(context.getContentResolver(), id), enabled);
    }

    private static void enableScheduleInternal(final Context context,
                    final Schedule sched, boolean enabled) {
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues(2);
        values.put(Schedule.Columns.ENABLED, enabled ? 1 : 0);

        // TODO calculate the different times that the alarm needs to go off on
        // and enable alarm for those times

        resolver.update(ContentUris.withAppendedId(
                        Schedule.Columns.CONTENT_URI, sched.id), values, null,
                        null);
    }

    /**
     * Queries all schedules
     * 
     * @return cursor over all schedules
     */
    public static Cursor getSchedulesCursor(ContentResolver contentResolver) {
        return contentResolver.query(Schedule.Columns.CONTENT_URI,
                        Schedule.Columns.SCHEDULE_QUERY_COLUMNS, null, null,
                        Schedule.Columns.DEFAULT_SORT_ORDER);
    }

    /**
     * Return a Schedule object representing the schedule id in the database.
     * Returns null if no schedule exists.
     */
    public static Schedule getSchedule(ContentResolver contentResolver,
                    int scheduleId) {
        Cursor cursor =
                        contentResolver.query(
                                        ContentUris.withAppendedId(
                                                        Schedule.Columns.CONTENT_URI,
                                                        scheduleId),
                                        Schedule.Columns.SCHEDULE_QUERY_COLUMNS,
                                        null, null, null);
        Schedule sched = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                sched = new Schedule(cursor);
            }
            cursor.close();
        }
        return sched;
    }

    public static void setSchedule(Context context, int id, String label,
                    int monday, int tuesday, int wednesday, int thursday,
                    int friday, int saturday, int sunday) {
        ContentValues values = new ContentValues(9);
        ContentResolver resolver = context.getContentResolver();
        
    }

}
