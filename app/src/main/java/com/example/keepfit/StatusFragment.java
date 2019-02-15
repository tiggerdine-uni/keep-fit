package com.example.keepfit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.keepfit.db.AppDatabase;
import com.example.keepfit.db.dao.GoalDao;
import com.example.keepfit.db.entity.Goal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StatusFragment extends Fragment {

    private static StatusFragment instance = null;
    // TODO do all these things really need to be class variables?
    // TODO this resets steps every time we come back from settingsactivity
    int steps = 0;
    TextView statusTv;
    TextView progressTextView;
    ProgressWheel wheel;
    AppDatabase db;
    List<Goal> goals;
    ArrayAdapter spinnerArrayAdapter;
    Spinner spinner;
    Goal activeGoal;
    boolean check;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    public static StatusFragment getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        db = AppDatabase.getAppDatabase(getContext());
        goals = db.goalDao().loadAllVisibleGoals();
        spinner = view.findViewById(R.id.spinner);
        spinnerArrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, goals);
        spinner.setAdapter(spinnerArrayAdapter);
        check = false;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (check == false) {
                    Toast.makeText(getContext(), "not doing it", Toast.LENGTH_SHORT).show();
                    check = true;
                } else {
                    Toast.makeText(getContext(), "doing it", Toast.LENGTH_SHORT).show();

                    Goal selectedGoal = (Goal) spinner.getSelectedItem();
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.active_goal_id_key), selectedGoal.goalId);
                    editor.commit();
                    Log.v("StatusFragment", "putting id " + selectedGoal.goalId);
                    refresh();
                }

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
                View view1 = inflater.inflate(R.layout.dialog_steps, null);
                final EditText et = view1.findViewById(R.id.et);
                final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                builder.setView(view1)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // hide keyboard
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                steps += Integer.parseInt(et.getText().toString());
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
        refresh();
        return view;
    }

    public void refresh() {
        goals.clear();
        goals.addAll(db.goalDao().loadAllGoals());
        spinnerArrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int activeGoalId = sharedPref.getInt(getString(R.string.active_goal_id_key), 0);
        Log.v("StatusFragment", "activeGoalId = " + activeGoalId);
        activeGoal = AppDatabase.getAppDatabase(getContext()).goalDao().findGoalWithId(activeGoalId);
        spinner.setSelection(spinnerArrayAdapter.getPosition(activeGoal));
        float progress = (float) steps / activeGoal.steps;
        if (progress > 1) {
            progress = 1;
        }
        statusTv.setText(steps + "/" + activeGoal.steps);
        progressTextView.setText((int) (progress * 100) + "%");
        setBarColor(progress);
        wheel.setProgress(progress);
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
