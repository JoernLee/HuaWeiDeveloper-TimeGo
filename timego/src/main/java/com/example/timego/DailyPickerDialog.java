package com.example.timego;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by Jason-Chen on 2017-04-09.
 */
public class DailyPickerDialog extends DatePickerDialog {
    public DailyPickerDialog(Context context, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, listener, year, monthOfYear, dayOfMonth);



       // ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
       // ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
    }
}
