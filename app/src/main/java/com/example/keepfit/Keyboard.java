package com.example.keepfit;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class Keyboard {

    public static void hide(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
    }

    public static void show(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

}
