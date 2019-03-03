package com.example.keepfit;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * A keyboard helper class.
 */
public class Keyboard {

    /**
     * Hides the keyboard.
     *
     * @param context the context
     */
    public static void hide(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
    }

    /**
     * Shows the keyboard.
     *
     * @param context the context
     */
    public static void show(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

}
