package com.example.keepfit.db.entity;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Day {
    @PrimaryKey
    public Date date;

    public int steps;

    public int goalId;

    public Day(Date date, int steps) {
        this.date = date;
        this.steps = steps;
    }
}
