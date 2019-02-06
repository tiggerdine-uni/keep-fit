package com.example.keepfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

public class StatusFragment extends Fragment {

    int mTheRealLife = 0;
    int mJustFantasy = 10000;
    TextView mProgressTextView;
    ProgressWheel mWheel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();

                final View myView = inflater.inflate(R.layout.dialog_record, null);

                builder.setView(myView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText editText = myView.findViewById(R.id.edit_text);
                                mTheRealLife += Integer.parseInt(editText.getText().toString());
                                refresh();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.create().show();
            }
        });

        mProgressTextView = view.findViewById(R.id.progress_text_view);

        mWheel = view.findViewById(R.id.progress_wheel);

        refresh();

        return view;
    }

    // TODO can we stop doing this?
    private void refresh() {
        float progress = (float) mTheRealLife / mJustFantasy;
        if (progress > 1) {
            progress = 1;
        }
        mProgressTextView.setText(Math.round(progress * 100) + "%");
        setBarColor(progress);
        mWheel.setProgress(progress);
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
