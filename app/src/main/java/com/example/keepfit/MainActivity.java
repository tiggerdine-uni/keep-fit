package com.example.keepfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.keepfit.db.AppDatabase;
import com.example.keepfit.db.entity.Goal;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabase db = AppDatabase.getAppDatabase(this);
        manip(db);

        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewpager);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean scEnabled = sharedPrefs.getBoolean(getString(R.string.settings_step_counter_key), true);
        if (scEnabled) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void manip(AppDatabase db) {
        if (db.goalDao().loadAllVisibleGoals().isEmpty()) {
            Goal goal = new Goal("My First Goal", 1000);
            db.goalDao().insert(goal);
        }
//        db.goalDao().nuke();
//        db.dayDao().nuke();
//        Goal goal1 = new Goal("Goal 1", 10000);
//        goal1.visible = 0;
//        Goal goal2 = new Goal("Goal 2", 8000);
//        Goal goal3 = new Goal("Goal 3", 12500);
//        Goal goal4 = new Goal("Goal 4", 6500);
//        db.goalDao().insert(goal1);
//        db.goalDao().insert(goal2);
//        db.goalDao().insert(goal3);
//        db.goalDao().insert(goal4);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Toast.makeText(this, "" + event.values[0], Toast.LENGTH_SHORT).show();
        StatusFragment.getInstance().recordActivity(1);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
