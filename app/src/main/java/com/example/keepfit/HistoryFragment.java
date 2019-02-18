package com.example.keepfit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

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
                if (day != null) {
                    Toast.makeText(getContext(), "You walked " + day.steps + " steps on " + date.toString() + ".", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No activity recorded on " + date + ".", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
