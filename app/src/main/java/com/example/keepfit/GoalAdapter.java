package com.example.keepfit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.keepfit.db.entity.Goal;

import java.util.List;

public class GoalAdapter extends ArrayAdapter<Goal> {

    public GoalAdapter(Context context, List<Goal> goals) {
        super(context, 0, goals);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.goal_list_item, parent, false);
        }

        Goal currentGoal = getItem(position);

        TextView goalName = listItemView.findViewById(R.id.goal_name);
        goalName.setText(currentGoal.name);

        TextView goalSteps = listItemView.findViewById(R.id.goal_steps);
        goalSteps.setText("" + currentGoal.steps);

        return listItemView;
    }
}
