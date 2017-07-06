package com.example.timego;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

/**
 * Created by Jason-Chen on 2017-03-26.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TimeGo.db";
    private static final int VERSION = 1;


    //建表语句

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {


    }


    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        SimpleDateFormat table_name = new SimpleDateFormat("yy-MM-dd");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + table_name);
        onCreate(sqLiteDatabase);
    }





}
