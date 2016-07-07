package com.jprarama.habittracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by joshua on 7/7/16.
 */
public final class DatabaseContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";

    private static final String COMMA_SEP = ", ";

    private DatabaseContract() {}

    public static abstract class HabitTable implements BaseColumns {
        public static final String TABLE_NAME = "habits";
        public static final String COLUMN_NAME_HABIT = "habit_name";
        public static final String COLUMN_NAME_MINUTES = "minutes";
        public static final String[] ALL_COLUMNS = {
                _ID, COLUMN_NAME_HABIT, COLUMN_NAME_MINUTES
        };

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + HabitTable.TABLE_NAME + " (" +
                        HabitTable._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                        HabitTable.COLUMN_NAME_HABIT + TEXT_TYPE + COMMA_SEP +
                        HabitTable.COLUMN_NAME_MINUTES + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + HabitTable.TABLE_NAME;

        public static void insert(SQLiteDatabase db, String habit, int minutes) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_HABIT, habit);
            values.put(COLUMN_NAME_MINUTES, minutes);

            db.insert(TABLE_NAME, null, values);
        }

        public static Cursor query(SQLiteDatabase db, String[] projection) {
            Cursor c = db.query(
                    TABLE_NAME,  // The table to query
                    projection,  // The columns to return
                    null,  // The columns for the WHERE clause
                    null,  // The values for the WHERE clause
                    null,  // don't group the rows
                    null,  // don't filter by row groups
                    null   // The sort order
            );

            return c;
        }

        public static int deleteAll(SQLiteDatabase db) {
            return db.delete(TABLE_NAME, "1", null);
        }

        public static int update(SQLiteDatabase db, int id, int minutes) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_MINUTES, minutes);

            // Which row to update, based on the ID
            String selection = _ID + " = ?";
            String[] selectionArgs = { String.valueOf(id) };

            return db.update(
                    TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

        }

    }
}