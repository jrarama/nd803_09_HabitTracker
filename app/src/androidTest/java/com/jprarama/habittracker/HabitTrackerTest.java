package com.jprarama.habittracker;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.junit.Test;

import static com.jprarama.habittracker.HabitTrackerTest.DatabaseContract.HabitTable;

/**
 * Created by joshua on 6/7/16.
 */
public class HabitTrackerTest extends ApplicationTestCase<Application> {
    private static final String TAG = HabitTrackerTest.class.getName();

    public HabitTrackerTest() {
        super(Application.class);
    }

    public static final class DatabaseContract {
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

            private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + HabitTable.TABLE_NAME + " (" +
                            HabitTable._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                            HabitTable.COLUMN_NAME_HABIT + TEXT_TYPE + COMMA_SEP +
                            HabitTable.COLUMN_NAME_MINUTES + INT_TYPE +
                            " )";

            private static final String SQL_DELETE_ENTRIES =
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

    private class HabitDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Habits.db";

        public HabitDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(HabitTable.SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(HabitTable.SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    private void insertDummyValues(SQLiteDatabase db) {
        HabitTable.insert(db, "Walking", 10);
        HabitTable.insert(db, "Sleeping", 20);
        HabitTable.insert(db, "Programming", 100);
    }

    @Test
    public void testOperations() {
        HabitDbHelper helper = new HabitDbHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        // Delete all first
        HabitTable.deleteAll(db);

        Log.d(TAG, "Testing insert");
        insertDummyValues(db);

        // Query
        queryCursor(db);

        Log.d(TAG, "Testing delete all");
        HabitTable.deleteAll(db);

        Log.d(TAG, "After deletion:");
        // Query
        queryCursor(db);

        Log.d(TAG, "Insert again");
        insertDummyValues(db);

        Log.d(TAG, "Test update");
        HabitTable.update(db, 1, 15);
        HabitTable.update(db, 2, 25);
        HabitTable.update(db, 3, 105);

        queryCursor(db);
    }

    private void queryCursor(SQLiteDatabase db) {
        Cursor c = HabitTable.query(db, HabitTable.ALL_COLUMNS);
        if (c.moveToFirst()) {
            do {
                Habit habit = Habit.getFromCursor(c);
                Log.d(TAG, String.format("Id: %d, Habit: %s, Minutes: %d",
                        habit.getId(), habit.getHabit(), habit.getMinutes()));
            } while (c.moveToNext());
        }
    }

    private static class Habit {
        int id;
        String habit;
        int minutes;

        public Habit(int id, String habit, int minutes) {
            this.id = id;
            this.habit = habit;
            this.minutes = minutes;
        }

        public int getId() {
            return id;
        }

        public String getHabit() {
            return habit;
        }

        public int getMinutes() {
            return minutes;
        }

        public static Habit getFromCursor(Cursor c) {
            int id = c.getInt(c.getColumnIndexOrThrow(HabitTable._ID));
            String habit = c.getString(c.getColumnIndexOrThrow(HabitTable.COLUMN_NAME_HABIT));
            int minutes = c.getInt(c.getColumnIndexOrThrow(HabitTable.COLUMN_NAME_MINUTES));
            return new Habit(id, habit, minutes);
        }
    }
}
