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

        // Get the database.
        AppDatabase db = AppDatabase.getAppDatabase(this);

        // Manipulate the database.
        manip(db);

        setContentView(R.layout.activity_main);

        // Find the pager.
        ViewPager viewPager = findViewById(R.id.viewpager);

        // Create an adapter.
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        // Connect the pager and the adapter.
        viewPager.setAdapter(adapter);

        // Find the tabs.
        TabLayout tabLayout = findViewById(R.id.tabs);

        // Connect the tabs and the pager.
        tabLayout.setupWithViewPager(viewPager);

        // Get the sensor manager.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Get the step counter sensor.
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop listening to the step counter.
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if the step counter is enabled.
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean scEnabled = sharedPrefs.getBoolean(getString(R.string.settings_step_counter_key), true);
        // If it is...
        if (scEnabled) {
            // ... listen to the step counter.
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

    /**
     * Manipulates the database.
     *
     * @param db the database to be manipulated
     */
    private void manip(AppDatabase db) {
        // If there are no goals...
        if (db.goalDao().loadAllVisibleGoals().isEmpty()) {
            // ... add one.
            Goal goal = new Goal("My First Goal", 1000);
            db.goalDao().insert(goal);
        }

        // These lines were used to test the database.
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

        // These lines were used for screenshots.
//        db.dayDao().nuke();
//        db.goalDao().nuke();
//        Goal goal = new Goal("2K", 2000);
//        db.goalDao().insert(goal);
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY,0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
//        Day day = new Day(cal.getTime(), 1506);
//        Goal goal2 = db.goalDao().findVisibleGoalWithName("2K");
//        day.goalId = goal2.goalId;
//        db.dayDao().insert(day);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Add 1 step.
        StatusFragment.getInstance().record(1);
//        Toast.makeText(this, "" + event.values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing.
    }
}
