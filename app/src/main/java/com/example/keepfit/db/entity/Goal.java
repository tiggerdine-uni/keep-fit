package com.example.keepfit.db.entity;

import androidx.annotation.Nullable;
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

    public Goal(String name, int steps) {
        this.name = name;
        this.steps = steps;
        visible = 1;
    }

    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(((Goal) obj).goalId == goalId) {
            return true;
        }
        return false;
    }
}
