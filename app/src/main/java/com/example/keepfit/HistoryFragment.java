package com.example.keepfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.keepfit.db.AppDatabase;
import com.example.keepfit.db.entity.Day;

import java.util.Calendar;
import java.util.Date;

import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        DatePicker datePicker = view.findViewById(R.id.date_picker);
        Calendar cal = Calendar.getInstance();
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Log.v("HistoryFragment", "year = " + year + ", monthOfYear = " + monthOfYear + ", dayOfMonth = " + dayOfMonth);
                Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
                // Log.v("HistoryFragment", "date = " + date);
                AppDatabase db = AppDatabase.getAppDatabase(getContext());
                Day day = db.dayDao().findDayWithDate(date);
//                if (day != null) {
//                    Toast.makeText(getContext(), "You walked " + day.steps + " steps on " + date.toString() + ".", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "No activity recorded on " + date + ".", Toast.LENGTH_SHORT).show();
//                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_history, null);
                builder.setView(dialogView).setPositiveButton("Add Steps", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                        View stepsDialogView = inflater.inflate(R.layout.dialog_steps, null);
                        builder2.setView(stepsDialogView);
                        AlertDialog stepsDialog = builder2.create();
                        stepsDialog.show();
                    }
                });
                AlertDialog historyDialog = builder.create();
                historyDialog.show();
            }
        });
        return view;
    }
}
