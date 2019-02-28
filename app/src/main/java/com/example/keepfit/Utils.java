package com.example.keepfit;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static Date getDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
