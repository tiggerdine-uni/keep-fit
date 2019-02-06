package com.example.keepfit.db.dao;

import com.example.keepfit.db.entity.Goal;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface GoalDao {
    @Query("SELECT * FROM goal")
    List<Goal> loadAllGoals();

    @Query("SELECT * FROM goal WHERE visible = 1")
    List<Goal> loadAllVisibleGoals();

    @Insert
    void insert(Goal goal);

    @Update
    void update(Goal goal);

    @Delete
    void delete(Goal goal);
}
