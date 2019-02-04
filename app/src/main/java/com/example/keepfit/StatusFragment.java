package com.example.keepfit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

public class StatusFragment extends Fragment {

    ProgressWheel mWheel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "My milkshake brings all the boys to the yard.", Toast.LENGTH_SHORT).show();
            }
        });

        mWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);

        float progress = 0.85f;

        mWheel.setProgress(progress);

        setBarColor(progress);

        return view;
    }

    private void setBarColor(float progress) {
        switch ((int) Math.floor(progress * 10)) {
            case 0:
                mWheel.setBarColor(0xfff44336);
                break;
            case 1:
                mWheel.setBarColor(0xffec6d39);
                break;
            case 2:
                mWheel.setBarColor(0xffe4923d);
                break;
            case 3:
                mWheel.setBarColor(0xffddb240);
                break;
            case 4:
                mWheel.setBarColor(0xffd5cd43);
                break;
            case 5:
                mWheel.setBarColor(0xffb7cd45);
                break;
            case 6:
                mWheel.setBarColor(0xff95c647);
                break;
            case 7:
                mWheel.setBarColor(0xff78be49);
                break;
            case 8:
                mWheel.setBarColor(0xff5eb64b);
                break;
            default:
                mWheel.setBarColor(0xff4caf50);
        }
    }
}
