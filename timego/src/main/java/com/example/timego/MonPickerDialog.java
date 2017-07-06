package com.example.timego;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

/**
 * Created by Jason-Chen on 2017-04-09.
 */
public class MonPickerDialog extends DatePickerDialog {
    public MonPickerDialog(Context context,int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);

/*        this.setTitle(year + "年" + (monthOfYear + 1) + "月");
        try {
            ((ViewGroup) ((ViewGroup)  this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
        }
       catch (Exception e)
       {
           System.out.println(e);
       }*/

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
      //  this.setTitle(year + "年" + (month + 1) + "月");
    }
}
