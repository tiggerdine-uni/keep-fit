package com.example.keepfit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import androidx.fragment.app.Fragment;

public class StatusFragment extends Fragment {

    // TODO rename
    int theRealLife = 0;
    // TODO rename
    int justFantasy = 10000;
    TextView progressTextView;
    ProgressWheel wheel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                // TODO rename
                View view1 = inflater.inflate(R.layout.dialog_add, null);
                // TODO rename
                final EditText et = view1.findViewById(R.id.et);
                final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                builder.setView(view1)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // hide keyboard
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                theRealLife += Integer.parseInt(et.getText().toString());
                                refresh();
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
                        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                            return true;
                        }
                        return false;
                    }
                });
                alertDialog.show();
                et.requestFocus();
                // show keyboard
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        progressTextView = view.findViewById(R.id.progress_text_view);
        wheel = view.findViewById(R.id.progress_wheel);
        refresh();
        return view;
    }

    private void refresh() {
        float progress = (float) theRealLife / justFantasy;
        if (progress > 1) {
            progress = 1;
        }
        progressTextView.setText((int) (progress * 100) + "%");
        setBarColor(progress);
        wheel.setProgress(progress);
    }

    private void setBarColor(float progress) {
        switch ((int) Math.floor(progress * 10)) {
            case 0:
                wheel.setBarColor(0xfff44336);
                break;
            case 1:
                wheel.setBarColor(0xffec6d39);
                break;
            case 2:
                wheel.setBarColor(0xffe4923d);
                break;
            case 3:
                wheel.setBarColor(0xffddb240);
                break;
            case 4:
                wheel.setBarColor(0xffd5cd43);
                break;
            case 5:
                wheel.setBarColor(0xffb7cd45);
                break;
            case 6:
                wheel.setBarColor(0xff95c647);
                break;
            case 7:
                wheel.setBarColor(0xff78be49);
                break;
            case 8:
                wheel.setBarColor(0xff5eb64b);
                break;
            default:
                wheel.setBarColor(0xff4caf50);
                break;
        }
    }
}
