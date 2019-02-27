package com.example.keepfit.db;

import android.content.Context;

import com.example.keepfit.db.converter.DateConverter;
import com.example.keepfit.db.dao.DayDao;
import com.example.keepfit.db.dao.GoalDao;
import com.example.keepfit.db.entity.Day;
import com.example.keepfit.db.entity.Goal;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Day.class, Goal.class}, version = 4, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    "goal-database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract DayDao dayDao();

    public abstract GoalDao goalDao();
}
