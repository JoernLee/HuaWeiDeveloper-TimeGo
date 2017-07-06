package com.example.timego;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jason-Chen on 2017-04-26.
 */
public class DBManager {

    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private static final String KEY_TIME = "time";
    private static final String KEY_COLOR = "color";
    private static final String KEY_NAME = "name";
    private static final String KEY_RUNTIME = "runtime";
    public DBManager(Context context) {
        helper = new DatabaseHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    public void deleteTable(String table_name) {
        table_name = "T" + table_name;//数据表名称不能以数字开头；
        String deleteTable = "DROP TABLE " + table_name;
        try {
            db.execSQL(deleteTable);
        } catch (Exception e) {
        }


    }

    public void creatTableDaily(String table_name) {
        table_name = "T" + table_name;//数据表名称不能以数字开头；
        String CREATE_TABLE = "create table " + table_name + "("
                + KEY_TIME + " text not null primary key,"
                + KEY_NAME + " text not null,"
                + KEY_COLOR + " text not null,"
                + KEY_RUNTIME + " integer not null)";
        try {
            db.execSQL(CREATE_TABLE);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void creatTableMonth(String table_name, String[] column_name, int dayNumOfMonth) {

        table_name = "T" + table_name;//数据表名称不能以数字开头；
        String CREATE_TABLE = "create table " + table_name + "(" + KEY_NAME + " text not null primary key,";
        for (int i = 0; i < dayNumOfMonth - 1; i++) {
            CREATE_TABLE += "C" + column_name[i] + " REAL,";
        }
        CREATE_TABLE += "C" + column_name[dayNumOfMonth - 1] + " REAL)";

        try {
            db.execSQL(CREATE_TABLE);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void creatTableYear(String table_name) {

        table_name = "T" + table_name;//数据表名称不能以数字开头；
        String CREATE_TABLE = "create table " + table_name + "(" + KEY_NAME + " text not null primary key,";
        for (int i = 1; i < 10; i++) {
            CREATE_TABLE += "C0" + i + " REAL,";
        }
        for (int i = 10; i < 12; i++) {
            CREATE_TABLE += "C" + i + " REAL,";
        }
        CREATE_TABLE += "C12 REAL)";
        try {
            db.execSQL(CREATE_TABLE);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void addAppInfo(String table_name, AppInfo appInfo) {
        //  tryCreatTable();

        table_name = "T" + table_name;//数据表名称不能以数字开头；
        //使用ContentValues添加数据
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, appInfo.getOpenTime());
        values.put(KEY_NAME, appInfo.getAppLabel());
        values.put(KEY_COLOR, appInfo.getAppColor());
        values.put(KEY_RUNTIME, appInfo.getRunTime());
        try {
            db.replace(table_name, null, values);

        } catch (Exception e) {
        }



    }

    public void addAppInfoMonth(String table_name, String appname, String column_name, double sumTime) {
        // tryCreatTable();

        table_name = "T" + table_name;//数据表名称不能以数字开头；
        column_name = "C" + column_name;
        //使用ContentValues添加数据
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, appname);
        values.put(column_name, sumTime);
        try {
            db.replace(table_name, null, values);
        } catch (Exception e) {
        }




    }

    //更新AppInfo
    public void updateAppInfoMonth(String table_name, String appName, String column_name, double runtime) {
        // tryCreatTable();

        table_name = "T" + table_name;//数据表名称不能以数字开头；
        column_name = "C" + column_name;
        ContentValues values = new ContentValues();
        values.put(column_name, runtime);//key为字段名，value为值
        try {
            db.update(table_name, values, "name=?", new String[]{appName});
        } catch (Exception e) {
        }

    }

    public Map<String, List> selectAppInfoDaily(String table_name) {
        Map<String, List> dailyAppInfoMap = new HashMap<>();
        table_name = "T" + table_name;//数据表名称不能以数字开头；
        Cursor cursor = null;

        String sql = "select name from sqlite_master where type ='table' and name ='" + table_name + "'";
        cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            cursor.close();

            return dailyAppInfoMap;
        }
        String selectQuery = "select name from " + table_name;
        cursor = db.rawQuery(selectQuery, null);
        Set<String> appName = new HashSet<>();
        if (cursor.moveToFirst()) {
            do {
                appName.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        for (String appname : appName) {

            selectQuery = "select time,name from " + table_name + " where name='" + appname + "'";
            cursor = db.rawQuery(selectQuery, null);
            List apptime = new ArrayList();
            if (cursor.moveToFirst()) {
                do {
                    apptime.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            dailyAppInfoMap.put(appname, apptime);

        }
        cursor.close();


        return dailyAppInfoMap;
    }

    public Map<String, double[]> selectAppInfoMonth(String table_name) {
        Map<String, double[]> appInfoMap = new HashMap<>();

        table_name = "T" + table_name;//数据表名称不能以数字开头；
        String selectQuery = "select * from " + table_name;

        String sql = "select name from sqlite_master where type ='table' and name ='" + table_name + "'";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            cursor.close();

            return appInfoMap;
        }
        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    double[] runtime = new double[cursor.getColumnCount() - 1];
                    String appName = null;
                    appName = cursor.getString(0);
                    for (int i = 0; i < cursor.getColumnCount() - 1; i++)
                        runtime[i] = cursor.getDouble(i + 1);
                    appInfoMap.put(appName, runtime);
                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {

        }

        return appInfoMap;
    }

    public Map<String, double[]> selectAppInfoYear(String table_name) {
        Map<String, double[]> appInfoMap = new HashMap<>();

        table_name = "T" + table_name;//数据表名称不能以数字开头；
        String selectQuery = "select * from " + table_name;

        String sql = "select name from sqlite_master where type ='table' and name ='" + table_name + "'";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            cursor.close();

            return appInfoMap;
        }
        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    double[] runtime = new double[cursor.getColumnCount() - 1];
                    String appName = null;
                    appName = cursor.getString(0);
                    for (int i = 0; i < cursor.getColumnCount() - 1; i++)
                        runtime[i] = cursor.getDouble(i + 1);
                    appInfoMap.put(appName, runtime);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {

        }
        cursor.close();

        return appInfoMap;
    }

    public List judgeAppExist(String table_name, String appName, String column_name) {
        table_name = "T" + table_name;//数据表名称不能以数字开头；
        List returnInfo = new ArrayList();
        column_name = "C" + column_name;
        String selectQuery = "select name," + column_name + " from " + table_name + " where name = '" + appName + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        double runtime;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            runtime = cursor.getDouble(1);
            returnInfo.add(runtime);
            System.out.println("w");
        } else {

        }
        cursor.close();

        return returnInfo;
    }
    public boolean judgeTableExist(String table_name) {
        table_name = "T" + table_name;//数据表名称不能以数字开头；
        String sql = "select name from sqlite_master where type ='table' and name ='" + table_name + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();

            return true;
        }


    }
    public void addAppInfoYear(String table_name, final String appname, final String[] column_name, final double[] sumTime) {
        // tryCreatTable();

        table_name = "T" + table_name;//数据表名称不能以数字开头；
//        for(int i=0;i<column_name.length;i++)
//            column_name[i]="C"+column_name[i];
        //使用ContentValues添加数据
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, appname);
        for (int i = 0; i < 12; i++) {
            values.put(column_name[i], sumTime[i]);
        }
        try {
            db.replace(table_name, null, values);
        } catch (Exception e) {
        }


    }

    //删除AppInfo
    public void deleteAppInfo(String table_name, String name) {
        table_name = "T" + table_name;//数据表名称不能以数字开头；

        db.delete(table_name, KEY_NAME + "=?", new String[]{name});

    }

    public List<AppInfo> getAppInfo(String table_name) {
        List<AppInfo> selectAppInfo = new ArrayList<>();
        table_name = "T" + table_name;//数据表名称不能以数字开头；
        Cursor cursor = null;


        String sql = "select name from sqlite_master where type ='table' and name ='" + table_name + "'";
        cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return selectAppInfo;
        }

        String selectQuery = "select * from " + table_name ;
        try {


            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    AppInfo appInfo=new AppInfo();
                    appInfo.setOpenTime(cursor.getString(0));
                    appInfo.setAppLabel(cursor.getString(1));
                    appInfo.setAppColor(cursor.getString(2));
                    appInfo.setRunTime(Integer.parseInt(cursor.getString(3)));
                    selectAppInfo.add(appInfo);
                } while (cursor.moveToNext());
            }
        }
        catch (Exception e){}
        cursor.close();
        return selectAppInfo;
    }


    //尝试建表
    public void tryCreatTable() {
        SimpleDateFormat nowDate = new SimpleDateFormat("yyyyMMdd");
        String tableName = nowDate.format(new Date()).toString();
        this.creatTableDaily(tableName);//创建每日数据表
        Calendar cal = Calendar.getInstance();
        int dayNumOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String[] column_name = new String[dayNumOfMonth];
        for (int i = 0; i < dayNumOfMonth; i++) {
            column_name[i] = nowDate.format(cal.getTime()).toString();
            cal.add(Calendar.DAY_OF_MONTH, +1);
        }

        nowDate = new SimpleDateFormat("yyyyMM");
        tableName = nowDate.format(new Date()).toString();
        this.creatTableMonth(tableName, column_name, dayNumOfMonth);//创建月数据总表

        nowDate = new SimpleDateFormat("yyyy");
        tableName = nowDate.format(new Date()).toString();
        this.creatTableYear(tableName);//创建年数据总表
    }

    public void closeDB() {
        db.close();
    }

}
