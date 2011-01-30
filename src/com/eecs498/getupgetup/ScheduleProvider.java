package com.eecs498.getupgetup;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ScheduleProvider extends ContentProvider {
    private SQLiteOpenHelper mOpenHandler;

    private static final int SCHEDULES = 1;
    private static final int SCHEDULES_ID = 2;
    private static final UriMatcher sURIMatcher = new UriMatcher(
                    UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI("com.eecs498.getupgetup", "schedule", SCHEDULES);
        sURIMatcher.addURI("com.eecs498.getupgetup", "schedule/#",
                        SCHEDULES_ID);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "schedules.db";
        private static final int DATABASE_VERSION = 5;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE schedules (_id INTEGER PRIMARY KEY, "
                            + "label TEXT, enabled INTEGER, monday INTEGER, "
                            + "tuesday INTEGER, wednesday INTEGER, "
                            + "thursday INTEGER, friday INTEGER, "
                            + "saturday INTEGER, sunday INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS schedules");
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        mOpenHandler = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                    String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Generate the body of the query
        int match = sURIMatcher.match(uri);
        switch (match) {
            case SCHEDULES:
                qb.setTables("schedules");
                break;
            case SCHEDULES_ID:
                qb.setTables("schedules");
                qb.appendWhere("_id=");
                qb.appendWhere(uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mOpenHandler.getReadableDatabase();
        Cursor ret =
                        qb.query(db, projection, selection, selectionArgs,
                                        null, null, sortOrder);

        if (ret != null) {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }

    @Override
    public String getType(Uri uri) {
        int match = sURIMatcher.match(uri);
        switch (match) {
            case SCHEDULES:
                return "vnd.android.cursor.dir/schedules";
            case SCHEDULES_ID:
                return "vnd.android.cursor.item/schedules";
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                    String[] selectionArgs) {
        int count;
        long rowId = 0;
        int match = sURIMatcher.match(uri);
        SQLiteDatabase db = mOpenHandler.getWritableDatabase();
        switch (match) {
            case SCHEDULES_ID:
                String segment = uri.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                count = db.update("schedules", values, "_id=" + rowId, null);
                break;
            default:
                throw new UnsupportedOperationException("Cannot update URL: "
                                + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sURIMatcher.match(uri) != SCHEDULES)
            throw new IllegalArgumentException("Cannot insert into URL: " + uri);

        ContentValues values;
        if (initialValues != null)
            values = new ContentValues(initialValues);
        else
            values = new ContentValues();

        if (!values.containsKey(Schedule.Columns.LABEL))
            values.put(Schedule.Columns.LABEL, "");

        if (!values.containsKey(Schedule.Columns.ENABLED))
            values.put(Schedule.Columns.ENABLED, 0);

        if (!values.containsKey(Schedule.Columns.MONDAY))
            values.put(Schedule.Columns.MONDAY, -1);

        if (!values.containsKey(Schedule.Columns.TUESDAY))
            values.put(Schedule.Columns.TUESDAY, -1);

        if (!values.containsKey(Schedule.Columns.WEDNESDAY))
            values.put(Schedule.Columns.WEDNESDAY, -1);

        if (!values.containsKey(Schedule.Columns.THURSDAY))
            values.put(Schedule.Columns.THURSDAY, -1);

        if (!values.containsKey(Schedule.Columns.FRIDAY))
            values.put(Schedule.Columns.FRIDAY, -1);

        if (!values.containsKey(Schedule.Columns.SATURDAY))
            values.put(Schedule.Columns.SATURDAY, -1);

        if (!values.containsKey(Schedule.Columns.SUNDAY))
            values.put(Schedule.Columns.SUNDAY, -1);

        SQLiteDatabase db = mOpenHandler.getWritableDatabase();
        long rowId = db.insert("schedules", Schedule.Columns.LABEL, values);
        if (rowId < 0)
            throw new SQLException("Failed to insert row into " + uri);

        Uri newUri =
                        ContentUris.withAppendedId(
                                        Schedule.Columns.CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHandler.getWritableDatabase();
        int count;
        long rowId = 0;
        switch (sURIMatcher.match(uri)) {
            case SCHEDULES:
                count = db.delete("schedules", where, whereArgs);
                break;
            case SCHEDULES_ID:
                String segment = uri.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                if (TextUtils.isEmpty(where)) {
                    where = "_id=" + segment;
                }
                else {
                    where = "_id=" + segment + " AND (" + where + ")";
                }
                count = db.delete("schedules", where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: "
                                + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
