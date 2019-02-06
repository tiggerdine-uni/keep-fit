package com.example.keepfit.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Goal {
    @PrimaryKey(autoGenerate = true)
    public int goalId;

    public String name;

    public int steps;

    public int visible;
}
