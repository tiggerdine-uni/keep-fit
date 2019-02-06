package com.example.keepfit.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Day.class, Goal.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DayDao dayDao();
    public abstract GoalDao goalDao();
}
