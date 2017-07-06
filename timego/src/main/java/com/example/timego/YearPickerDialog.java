package com.example.timego;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

/**
 * Created by Jason-Chen on 2017-04-09.
 */
public class YearPickerDialog extends DatePickerDialog {

    public YearPickerDialog(Context context, int theme, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, listener, year, monthOfYear, dayOfMonth);


 /*       this.setTitle(year + "年");
        try {
            ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
            ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
        }
        catch (Exception e)
        {}*/

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
       // this.setTitle(year + "年" );
    }
}


