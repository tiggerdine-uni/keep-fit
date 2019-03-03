package com.example.keepfit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepfit.db.AppDatabase;
import com.example.keepfit.db.entity.Day;
import com.example.keepfit.db.entity.Goal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GoalFragment extends Fragment {

    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private AppDatabase db;
    private List<Goal> goals;
    private View view;
    private GoalAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        builder = new AlertDialog.Builder(getActivity());
        view = inflater.inflate(R.layout.fragment_goal, container, false);

        db = AppDatabase.getAppDatabase(getContext());
        goals = db.goalDao().loadAllVisibleGoals();

        adapter = new GoalAdapter(getActivity(), goals);

        createListView();

        createFloatingActionButton();

        return view;
    }

    private void createFloatingActionButton() {
        FloatingActionButton fab = view.findViewById(R.id.goal_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = inflater.inflate(R.layout.dialog_goal, null);
                TextView goalTv = view1.findViewById(R.id.goal_tv);
                goalTv.setText("New Goal");
                final EditText nameEt = view1.findViewById(R.id.goal_name_et);
                final EditText stepsEt = view1.findViewById(R.id.goal_steps_et);
                builder.setView(view1)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Keyboard.hide(getContext());
                                String nameString = nameEt.getText().toString();
                                String stepsString = stepsEt.getText().toString();
                                if (nameString.trim().isEmpty()) {
                                    Toast.makeText(getContext(), "Please enter a name.", Toast.LENGTH_SHORT).show();
                                } else if (stepsString.isEmpty()) {
                                    Toast.makeText(getContext(), "Please enter a number of steps.", Toast.LENGTH_SHORT).show();
                                } else {
                                    int steps = Integer.parseInt(stepsString);
                                    if (steps == 0) {
                                        Toast.makeText(getContext(), "0 steps? What kind of a goal is that?", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Goal goal = new Goal(nameString, steps);
                                        db.goalDao().insert(goal);
                                        adapter.clear();
                                        adapter.addAll(db.goalDao().loadAllVisibleGoals());
                                        adapter.notifyDataSetChanged();
                                        StatusFragment.getInstance().refresh();
                                    }
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

                // TODO this is meant to control the behaviour of pressing enter on keyboard
                alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        // TODO this doesn't work right :(
                        // Log.v("GoalFragment", "keyCode" + keyCode + ", name has focus? " + nameEt.hasFocus() + ", steps has focus? " + stepsEt.hasFocus());
                        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && stepsEt.hasFocus()) {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                            return true;
                        }

                        return false;
                    }
                });
                alertDialog.show();
                nameEt.requestFocus();
                Keyboard.show(getContext());
            }
        });
    }

    private void createListView() {
        final ListView listView = view.findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                Boolean goalEditing = sharedPrefs.getBoolean(getString(R.string.settings_goal_editing_key), true);
                if (!goalEditing) {
                    Toast.makeText(getContext(), R.string.toast_goal_editing_disabled, Toast.LENGTH_SHORT).show();
                } else {
                    final Goal clickedGoal = (Goal) adapterView.getItemAtPosition(position);
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    int activeGoalId = sharedPref.getInt(getString(R.string.active_goal_id_key), 0);
                    Log.v("StatusFragment", "getting id " + activeGoalId);
                    if (clickedGoal.goalId == activeGoalId) {
                        Toast.makeText(getContext(), R.string.toast_cannot_edit, Toast.LENGTH_SHORT).show();
                    } else {
                        View view1 = inflater.inflate(R.layout.dialog_goal, null);
                        TextView goalTv = view1.findViewById(R.id.goal_tv);
                        goalTv.setText("Edit Goal");
                        final EditText nameEt = view1.findViewById(R.id.goal_name_et);
                        nameEt.setText(clickedGoal.name);
                        final EditText stepsEt = view1.findViewById(R.id.goal_steps_et);
                        stepsEt.setText("" + clickedGoal.steps);
                        builder.setView(view1)
                                .setPositiveButton("Edit Goal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        Keyboard.hide(getContext());
                                        String nameString = nameEt.getText().toString();
                                        String stepsString = stepsEt.getText().toString();
                                        if (nameString.trim().isEmpty()) {
                                            Toast.makeText(getContext(), "Please enter a name.", Toast.LENGTH_SHORT).show();
                                        } else if (stepsString.isEmpty()) {
                                            Toast.makeText(getContext(), "Please enter a number of steps.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            int steps = Integer.parseInt(stepsString);
                                            if (steps == 0) {
                                                Toast.makeText(getContext(), "0 steps? What kind of a goal is that?", Toast.LENGTH_SHORT).show();
                                            } else {
                                                clickedGoal.name = nameString;
                                                clickedGoal.steps = steps;
                                                db.goalDao().update(clickedGoal);
                                                adapter.clear();
                                                adapter.addAll(db.goalDao().loadAllVisibleGoals());
                                                adapter.notifyDataSetChanged();
                                                StatusFragment.getInstance().refresh();
                                            }
                                        }
                                    }
                                })
                                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        Keyboard.hide(getContext());
                                    }
                                })
                                .setNegativeButton("Delete Goal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO maybe confirm?
                                        if(safeToDelete(clickedGoal)) {
                                            // Log.v("delete", "clicked goal is safe to delete");
                                            db.goalDao().delete(clickedGoal);
                                        } else {
                                            // Log.v("delete", "clicked goal is not safe to delete");
                                            clickedGoal.visible = 0;
                                            db.goalDao().update(clickedGoal);
                                        }
                                        adapter.clear();
                                        adapter.addAll(db.goalDao().loadAllVisibleGoals());
                                        adapter.notifyDataSetChanged();
                                        StatusFragment.getInstance().refresh();
                                    }
                                });
                        final AlertDialog alertDialog = builder.create();
                        // TODO keyboard stuff
                        alertDialog.show();
                    }
                }
            }
        });
    }

    private boolean safeToDelete(Goal clickedGoal) {
        List<Day> matches = db.dayDao().findDaysWithGoal(clickedGoal.goalId);
        if (matches.isEmpty()) {
            return true;
        }
        return false;
    }
}