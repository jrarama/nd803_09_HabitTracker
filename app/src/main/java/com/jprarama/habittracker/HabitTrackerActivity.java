package com.jprarama.habittracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jprarama.habittracker.DatabaseContract.HabitTable;

import org.junit.Test;

public class HabitTrackerActivity extends AppCompatActivity {
    private static final String TAG = HabitTrackerActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_tracker);

        testOperations();
    }


    private void insertDummyValues(SQLiteDatabase db) {
        HabitTable.insert(db, "Walking", 10);
        HabitTable.insert(db, "Sleeping", 20);
        HabitTable.insert(db, "Programming", 100);
    }

    @Test
    public void testOperations() {
        HabitDbHelper helper = new HabitDbHelper(this);
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

        Log.d(TAG, "Deleting database");
        db.close();
        helper.deleteDatabase(this);

        Log.d(TAG, "Recreate db and query");
        db = helper.getWritableDatabase();
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

}
