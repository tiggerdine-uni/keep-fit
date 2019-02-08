package com.example.keepfit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.keepfit.db.AppDatabase;
import com.example.keepfit.db.entity.Goal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.fragment.app.Fragment;

public class GoalFragment extends Fragment {

    List<Goal> goals;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_goal, container, false);

        final AppDatabase db = AppDatabase.getAppDatabase(getContext());
        populate(db);
        goals = db.goalDao().loadAllGoals();

        final GoalAdapter adapter = new GoalAdapter(getActivity(), goals);
        final ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getContext(), "position " + position + ", id " + id, Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fab = rootView.findViewById(R.id.goal_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view1 = inflater.inflate(R.layout.dialog_new_goal, null);
                final EditText nameEt = view1.findViewById(R.id.new_goal_name_et);
                final EditText stepsEt = view1.findViewById(R.id.new_goal_steps_et);
                final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                builder.setView(view1)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // hide keyboard
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                Goal goal = new Goal(nameEt.getText().toString(), Integer.parseInt(stepsEt.getText().toString()));
                                db.goalDao().insert(goal);
                                adapter.clear();
                                adapter.addAll(db.goalDao().loadAllGoals());
                                adapter.notifyDataSetChanged();
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
                // show keyboard
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        return rootView;
    }



    private void populate(AppDatabase db) {
        db.goalDao().nuke();
        db.goalDao().insert(new Goal("Goal 1", 10000));
        db.goalDao().insert(new Goal("Goal 2", 15000));
        db.goalDao().insert(new Goal("Goal 3", 7500));
    }
}
