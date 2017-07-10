package com.example.timego;

/**
 * Created by Jason_Chen on 2016-09-11.
 */

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBUtil {
    private ArrayList<String> arrayList1 = new ArrayList<String>();
    private ArrayList<String> arrayList2 = new ArrayList<String>();
    private ArrayList<String> arrayList3 = new ArrayList<String>();

    private HttpConnSoap Soap = new HttpConnSoap();

    public List<HashMap<String, String>> selectAllPlatformInfor(final Handler myhandler, String databaseName, String tableName)//获取电站最新的num条数据
    {
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        list.clear();
        arrayList1.clear();
        arrayList2.clear();
        arrayList3.clear();
        arrayList1.add("databaseName");
        arrayList1.add("tableName");
        arrayList2.add(databaseName);
        arrayList2.add(tableName);

        new Thread() {
            public void run() {
               arrayList3 = Soap.GetWebServer("selectAllPlatformInfor", arrayList1, arrayList2);

                Message msg = new Message();
                msg.what = 0x123;
                msg.obj = arrayList3;
                myhandler.sendMessage(msg);
            }
        }.start();
        return list;
    }
    public List<HashMap<String, String>> insertPlatformListInfo(String databaseName, String tableName, String Pname, String Pvalue)//获取电站最新的num条数据
    {
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        list.clear();
        arrayList1.clear();
        arrayList2.clear();
        arrayList3.clear();
        arrayList1.add("databaseName");
        arrayList1.add("tableName");
        arrayList1.add("Pname");
        arrayList1.add("Pvalue");

        arrayList2.add(databaseName);
        arrayList2.add(tableName);
        arrayList2.add(Pname);
        arrayList2.add(Pvalue);

        new Thread() {
            public void run() {
                arrayList3 = Soap.GetWebServer("insertPlatformListInfo", arrayList1, arrayList2);
                Message msg = new Message();
                msg.what = 0x123;
                msg.obj = arrayList3;
                //myhandler.sendMessage(msg);
            }
        }.start();
        return list;
    }



    public List<HashMap<String, String>> getOneInfo(final Handler myhandler, String select_date, String name)//获取电站某项参数的所有数据
    {
        HashMap<String, String> tempHash = new HashMap<String, String>();
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        tempHash.put("序号", "序号");
        tempHash.put("时间", "时间");
        tempHash.put("组件温度1", "组件温度1");
        list.clear();
        arrayList1.clear();
        arrayList2.clear();
        arrayList3.clear();
        arrayList1.add("select_date");
        arrayList1.add("name");
        arrayList2.add(select_date);
        arrayList2.add(name);
        list.add(tempHash);
        //android4.0以后不允许在主线程中访问网络，因为万一主线程阻塞了，
        // 会使得界面没有响应。我们开启线程访问即可。
        new Thread()//开启新线程，获取服务器数据
        {
            public void run() {
                arrayList1 = Soap.GetWebServer("selectOneCargoInfor", arrayList1, arrayList2);
                Message msg = new Message();
                msg.what = 0x123;
                msg.obj = arrayList1;
                myhandler.sendMessage(msg);
            }
        }.start();

        return list;
    }

    public void insertCargoInfo(String Cname, String Cnum) {
        arrayList1.clear();
        arrayList2.clear();
        arrayList1.add("Cname");
        arrayList1.add("Cnum");
        arrayList2.add(Cname);
        arrayList2.add(Cnum);

        new Thread() {
            public void run() {
                try {
                    Soap.GetWebServer("insertCargoInfo", arrayList1, arrayList2);
                } catch (Exception e) {

                }
            }
        }.start();
    }

    public void deleteCargoInfo(String num) {
        arrayList1.clear();
        arrayList2.clear();
        arrayList1.add("num");
        arrayList2.add(num);

        new Thread() {
            public void run() {
                try {
                    Soap.GetWebServer("deleteCargoInfo", arrayList1, arrayList2);
                } catch (Exception e) {

                }
            }
        }.start();
    }

}
