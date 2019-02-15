package com.example.keepfit.db.dao;

import com.example.keepfit.db.entity.Day;
import com.example.keepfit.db.entity.Goal;

import java.util.Date;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DayDao {
    @Query("SELECT * FROM day WHERE date = :date")
    Day findDayWithDate(Date date);

    @Insert
    void insert(Day day);

    @Query("DELETE FROM day")
    void nuke();

    @Update
    void update(Day today);
}
