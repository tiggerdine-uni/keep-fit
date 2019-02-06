package com.example.keepfit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.keepfit.db.AppDatabase;
import com.example.keepfit.db.entity.Goal;

import java.util.List;

import androidx.fragment.app.Fragment;

public class GoalsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);
        AppDatabase db = AppDatabase.getAppDatabase(getContext());
        populate(db);
        List<Goal> goals = db.goalDao().loadAllGoals();
        String s = "";
        for (Goal goal : goals) {
            s += goal.name + " " + goal.steps + "\n";
        }
        TextView textView = view.findViewById(R.id.text_view);
        textView.setText(s);
        return view;
    }

    private void populate(AppDatabase db) {
        db.goalDao().insert(new Goal("Bobs", 10000));
        db.goalDao().insert(new Goal("Vegana", 15000));
    }
}
