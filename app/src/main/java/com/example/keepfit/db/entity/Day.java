package com.example.keepfit.db.entity;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity        = Goal.class,
                                  parentColumns = "dayId",
                                  childColumns  = "goalId"))
public class Day {
    @PrimaryKey(autoGenerate = true)
    public int dayId;

    public Date date;

    public int goalId;
}
