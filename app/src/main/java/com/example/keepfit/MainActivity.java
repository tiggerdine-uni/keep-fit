package com.example.keepfit;

import android.os.Bundle;

import com.example.keepfit.db.AppDatabase;
import com.example.keepfit.db.entity.Goal;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabase db = AppDatabase.getAppDatabase(this);
        populate(db);

        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewpager);

        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void populate(AppDatabase db) {
        db.goalDao().nuke();
        Goal goal1 = new Goal("Goal 1", 10000);
        Goal goal2 = new Goal("Goal 2", 8000);
        Goal goal3 = new Goal("Goal 3", 12500);
        Goal goal4 = new Goal("Goal 4", 6500);
        db.goalDao().insert(goal1);
        db.goalDao().insert(goal2);
        db.goalDao().insert(goal3);
        db.goalDao().insert(goal4);
    }
}
