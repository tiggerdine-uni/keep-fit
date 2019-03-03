package com.example.keepfit;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.keepfit.db.AppDatabase;
import com.example.keepfit.db.entity.Day;
import com.example.keepfit.db.entity.Goal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class StatusFragment extends Fragment {

    private static StatusFragment instance = null;
    private TextView statusTv;
    private TextView progressTextView;
    private KonfettiView viewKonfetti;
    private ProgressWheel wheel;
    private AppDatabase db;
    private List<Goal> goals;
    private ArrayAdapter spinnerArrayAdapter;
    private Spinner spinner;
    private Goal selectedGoal;

    static StatusFragment getInstance() {
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        /**
         *
         */
        db = AppDatabase.getAppDatabase(getContext());
        goals = db.goalDao().loadAllVisibleGoals();
        spinner = view.findViewById(R.id.spinner);
        spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.goal_spinner_item, goals);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Goal selectedGoal = (Goal) spinner.getSelectedItem();
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.active_goal_id_key), selectedGoal.goalId);
                editor.apply();
//                Log.v("StatusFragment", "putting id " + selectedGoal.goalId);
                Day today = today();
                today.goalId = selectedGoal.goalId;
                db.dayDao().update(today);
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectedGoal = (Goal) spinner.getSelectedItem();

        /**
         *
         */
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_steps, null);
                TextView tv = dialogView.findViewById(R.id.dialog_steps_tv);
                tv.setText("How many steps?");
                final EditText et = dialogView.findViewById(R.id.et);
                builder.setView(dialogView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Keyboard.hide(getContext());
                                int n = Integer.parseInt(et.getText().toString());
                                if(n > 0) {
                                    recordActivity(n);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Keyboard.hide(getContext());
                            }
                        });
                final AlertDialog alertDialog = builder.create();
                alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                      keyCode == KeyEvent.KEYCODE_ENTER) {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                            return true;
                        }
                        return false;
                    }
                });
                alertDialog.show();
                Keyboard.show(getContext());
            }
        });

        //
        progressTextView = view.findViewById(R.id.progress_text_view);
        statusTv = view.findViewById(R.id.status_tv);
        wheel = view.findViewById(R.id.progress_wheel);
        viewKonfetti = view.findViewById(R.id.viewKonfetti);

        //
        refresh();

        //
        return view;
    }

    public void recordActivity(int n) {
        Date date = Utils.getDay();
        Day today = db.dayDao().findDayWithDate(date);
        boolean armNotification = false;
        boolean armConfetti = false;
        if (today.steps < (float) selectedGoal.steps / 2) {
            armNotification = true;
        }
        if (today.steps < selectedGoal.steps) {
            armConfetti = true;
        }
        if (today == null) {
            db.dayDao().insert(new Day(date, n));
        } else {
            today.steps += n;
            db.dayDao().update(today);
        }
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean notifications = sharedPrefs.getBoolean(getString(R.string.settings_notifications_key), false);
        if (notifications && armNotification && today.steps >= (float) selectedGoal.steps / 2) {
            showNotification();
//            Toast.makeText(getContext(), "Kappa123", Toast.LENGTH_SHORT).show();
        }
        if (armConfetti && today.steps >= selectedGoal.steps) {
            makeConfetti();
        }
        refresh();
    }

    private void showNotification() {
        /**
         * The heads-up notification appears the moment your app issues the notification and it disappears after a moment, but remains visible in the notification drawer as usual.
         *
         * Example conditions that might trigger heads-up notifications include the following:
         *
         * The user's activity is in fullscreen mode (the app uses fullScreenIntent).
         * The notification has high priority and uses ringtones or vibrations on devices running Android 7.1 (API level 25) and lower.
         * The notification channel has high importance on devices running Android 8.0 (API level 26) and higher.
         */
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("0", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "0")
                // TODO change
                .setSmallIcon(R.drawable.ic_walk)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());
    }

    /**
     *
     */
    private void makeConfetti() {
        viewKonfetti.build()
                // TODO don't hardcode these colors
                .addColors(Color.parseColor("#a864fd"),
                           Color.parseColor("#29cdff"),
                           Color.parseColor("#78ff44"),
                           Color.parseColor("#ff718d"),
                           Color.parseColor("#fdff6a"))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(10, 5))
                .setPosition(-50f, viewKonfetti.getWidth() + 50f,
                             -50f, -50f)
                .streamFor(300, 5000L);
    }

    /**
     *
     * @return
     */
    private Day today() {
        Date date = Utils.getDay();
        Day today = db.dayDao().findDayWithDate(date);
        if (today == null) {
            db.dayDao().insert(new Day(date, 0));
        }
        return db.dayDao().findDayWithDate(date);
    }

    /**
     *
     */
    void refresh() {
        goals.clear();
        goals.addAll(db.goalDao().loadAllVisibleGoals());
        spinnerArrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int activeGoalId = sharedPref.getInt(getString(R.string.active_goal_id_key), 0);
        // Log.v("StatusFragment", "activeGoalId = " + activeGoalId);
        AppDatabase db = AppDatabase.getAppDatabase(getContext());
        selectedGoal = db.goalDao().findGoalWithId(activeGoalId);
        if (selectedGoal == null) {
            statusTv.setText(getString(R.string.no_goals));
        } else {
            spinner.setSelection(spinnerArrayAdapter.getPosition(selectedGoal));
            Date date = Utils.getDay();
            Day today = db.dayDao().findDayWithDate(date);
            int steps;
            if (today == null) {
                steps = 0;
            } else {
                steps = today.steps;
            }
            float progress = (float) steps / selectedGoal.steps;
            if (progress >= 1) {
                progress = 1;
            }
            String statusText = steps + "/" + selectedGoal.steps;
            statusTv.setText(statusText);
            String progressText = (int) (progress * 100) + "%";
            progressTextView.setText(progressText);
            setBarColor(progress);
            wheel.setProgress(progress);
        }
    }

    /**
     *
     * @param progress
     */
    private void setBarColor(float progress) {
        switch ((int) Math.floor(progress * 10)) {
            case 0:
                wheel.setBarColor(0xfff44336);
                break;
            case 1:
                wheel.setBarColor(0xffec6d39);
                break;
            case 2:
                wheel.setBarColor(0xffe4923d);
                break;
            case 3:
                wheel.setBarColor(0xffddb240);
                break;
            case 4:
                wheel.setBarColor(0xffd5cd43);
                break;
            case 5:
                wheel.setBarColor(0xffb7cd45);
                break;
            case 6:
                wheel.setBarColor(0xff95c647);
                break;
            case 7:
                wheel.setBarColor(0xff78be49);
                break;
            case 8:
                wheel.setBarColor(0xff5eb64b);
                break;
            default:
                wheel.setBarColor(0xff4caf50);
                break;
        }
    }
}
