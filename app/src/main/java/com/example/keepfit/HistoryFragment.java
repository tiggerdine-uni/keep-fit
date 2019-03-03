package com.example.keepfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepfit.db.AppDatabase;
import com.example.keepfit.db.entity.Day;
import com.example.keepfit.db.entity.Goal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        DatePicker datePicker = view.findViewById(R.id.date_picker);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        datePicker.setMaxDate(cal.getTimeInMillis());
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Log.v("HistoryFragment", "year = " + year + ", monthOfYear = " + monthOfYear + ", dayOfMonth = " + dayOfMonth);
                final Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
//                Log.v("HistoryFragment", "date = " + date);
                final AppDatabase db = AppDatabase.getAppDatabase(getContext());
                final Day day = db.dayDao().findDayWithDate(date);
//                if (day != null) {
//                    Toast.makeText(getContext(), "You walked " + day.steps + " steps on " + date.toString() + ".", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "No activity recorded on " + date + ".", Toast.LENGTH_SHORT).show();
//                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_history, null);
                TextView historyTextView = dialogView.findViewById(R.id.history_text_view);

                String pattern = "EEEE dd MMMM yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                final String dateString = simpleDateFormat.format(date);
                String historyText = "Date: " + dateString;
                if (day == null) {
                    historyText += "\nNo activity.";
                } else {
                    Goal goal = db.goalDao().findGoalWithId(day.goalId);
                    if (goal == null) {
                        historyText += "\nSteps: " + day.steps;
                    } else {
                        float proportion = (float) day.steps / goal.steps;
                        if (proportion > 1) proportion = 1;
                        historyText += "\nGoal: "  + goal.name + " (" + goal.steps +
                                      ")\nSteps: " + day.steps +
                                       "\n%: "     + (int) (proportion * 100) + "%";
                    }
                }
                historyTextView.setText(historyText);
                builder.setView(dialogView)
                        .setPositiveButton("Add Steps", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                                Boolean historicActivityRecording = sharedPrefs.getBoolean(getString(R.string.settings_historic_activity_recording_key), false);
                                if(historicActivityRecording) {
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                                    View stepsDialogView = inflater.inflate(R.layout.dialog_steps, null);
                                    TextView tv = stepsDialogView.findViewById(R.id.dialog_steps_tv);
                                    tv.setText("Date: " + dateString + "\nHow many steps?");
                                    final EditText et = stepsDialogView.findViewById(R.id.et);
                                    builder2.setView(stepsDialogView)
                                            .setPositiveButton("Add Steps", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Keyboard.hide(getContext());
                                                    int addSteps = Integer.parseInt(et.getText().toString());
                                                    if (addSteps > 0) {
                                                        if (day == null) {
                                                            db.dayDao().insert(new Day(date, addSteps));
                                                        } else {
                                                            day.steps += addSteps;
                                                            db.dayDao().update(day);
                                                        }
                                                        StatusFragment statusFragment = StatusFragment.getInstance();
                                                        statusFragment.refresh();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Keyboard.hide(getContext());
                                                }
                                            });
                                    final AlertDialog stepsDialog = builder2.create();
                                    stepsDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                                    keyCode == KeyEvent.KEYCODE_ENTER) {
                                                stepsDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                                                return true;
                                            }
                                            return false;
                                        }
                                    });
                                    stepsDialog.show();
                                    Keyboard.show(getContext());
                                } else {
                                    Toast.makeText(getContext(), "Enable historic activity recording to record historic activity.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null);
                AlertDialog historyDialog = builder.create();
                historyDialog.show();
            }
        });
        return view;
    }
}
