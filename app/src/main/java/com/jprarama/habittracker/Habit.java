package com.jprarama.habittracker;

import android.database.Cursor;

import com.jprarama.habittracker.DatabaseContract.HabitTable;

/**
 * Created by joshua on 7/7/16.
 */
public class Habit {
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