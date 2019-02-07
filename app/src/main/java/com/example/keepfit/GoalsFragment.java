package com.example.keepfit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.keepfit.db.AppDatabase;
import com.example.keepfit.db.entity.Goal;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class GoalsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.goal_list, container, false);
        AppDatabase db = AppDatabase.getAppDatabase(getContext());
        populate(db);
        List<Goal> goals = db.goalDao().loadAllGoals();
        GoalAdapter adapter = new GoalAdapter(getActivity(), goals);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        return rootView;
    }

    private void populate(AppDatabase db) {
        db.goalDao().nuke();
        db.goalDao().insert(new Goal("Goal 1", 10000));
        db.goalDao().insert(new Goal("Goal 2", 15000));
        db.goalDao().insert(new Goal("Goal 3", 7500));
    }
}
