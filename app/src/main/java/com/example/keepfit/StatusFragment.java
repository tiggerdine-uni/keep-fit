package com.example.keepfit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import androidx.fragment.app.Fragment;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class StatusFragment extends Fragment {

    private static StatusFragment instance = null;
    // TODO do all these things really need to be class variables?
    private TextView statusTv;
    private TextView progressTextView;
    private KonfettiView viewKonfetti;
    private ProgressWheel wheel;
    private AppDatabase db;
    private List<Goal> goals;
    private ArrayAdapter spinnerArrayAdapter;
    private Spinner spinner;
    private Goal activeGoal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    static StatusFragment getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        db = AppDatabase.getAppDatabase(getContext());
        goals = db.goalDao().loadAllVisibleGoals();
        spinner = view.findViewById(R.id.spinner);
        spinnerArrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, goals);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Goal selectedGoal = (Goal) spinner.getSelectedItem();
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.active_goal_id_key), selectedGoal.goalId);
                    editor.commit();
                    Log.v("StatusFragment", "putting id " + selectedGoal.goalId);
                    Day today = today();
                    today.goalId = selectedGoal.goalId;
                    db.dayDao().update(today);
                    refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        activeGoal = (Goal) spinner.getSelectedItem();
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_steps, null);
                final EditText et = dialogView.findViewById(R.id.et);
                final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                builder.setView(dialogView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // hide keyboard
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                Date date = Utils.getDay();
                                Day today = db.dayDao().findDayWithDate(date);
                                int addSteps = Integer.parseInt(et.getText().toString());
                                if(today == null) {
                                    db.dayDao().insert(new Day(date, addSteps));
                                } else {
                                    today.steps += addSteps;
                                    db.dayDao().update(today);
                                }
                                // steps += Integer.parseInt(et.getText().toString());
                                refresh();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // hide keyboard
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            }
                        });
                final AlertDialog alertDialog = builder.create();
                alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                            return true;
                        }
                        return false;
                    }
                });
                alertDialog.show();
                et.requestFocus();
                // show keyboard
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        progressTextView = view.findViewById(R.id.progress_text_view);
        statusTv = view.findViewById(R.id.status_tv);
        wheel = view.findViewById(R.id.progress_wheel);
        viewKonfetti = view.findViewById(R.id.viewKonfetti);
        refresh();
        return view;
    }

    private Day today() {
        Date date = Utils.getDay();
        Day today = db.dayDao().findDayWithDate(date);
        if (today == null) {
            db.dayDao().insert(new Day(date, 0));
        }
        return db.dayDao().findDayWithDate(date);
    }

    void refresh() {
        goals.clear();
        goals.addAll(db.goalDao().loadAllVisibleGoals());
        spinnerArrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int activeGoalId = sharedPref.getInt(getString(R.string.active_goal_id_key), 0);
        // Log.v("StatusFragment", "activeGoalId = " + activeGoalId);
        AppDatabase db = AppDatabase.getAppDatabase(getContext());
        activeGoal = db.goalDao().findGoalWithId(activeGoalId);
        if (activeGoal == null) {
            statusTv.setText("No goals.");
        } else {
            spinner.setSelection(spinnerArrayAdapter.getPosition(activeGoal));
            Date date = Utils.getDay();
            Day today = db.dayDao().findDayWithDate(date);
            int steps;
            if (today == null) {
                steps = 0;
            } else {
                steps = today.steps;
            }
            float progress = (float) steps / activeGoal.steps;
            if (progress >= 1) {
                progress = 1;

                // TODO don't do this exactly here
                viewKonfetti.build()
                        // TODO don't hardcode these colors
                        .addColors(Color.parseColor("#fa1434"), Color.parseColor("#48ba2d"), Color.parseColor("#1899dd"))
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(12, 5))
                        .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                        .streamFor(300, 5000L);
            }
            String statusText = steps + "/" + activeGoal.steps;
            statusTv.setText(statusText);
            String progressText = (int) (progress * 100) + "%";
            progressTextView.setText(progressText);
            setBarColor(progress);
            wheel.setProgress(progress);
        }
    }

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
