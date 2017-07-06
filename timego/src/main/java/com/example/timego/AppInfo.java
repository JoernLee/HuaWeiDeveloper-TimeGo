package com.example.timego;

import android.graphics.drawable.Drawable;

/**
 * Created by Jason-Chen on 2017-03-24.
 */
public class AppInfo {
    private String appLabel;    //应用程序标签
    private Drawable appIcon ;  //应用程序图像
    private String openTime="1993-08-24 10:24:00" ;    //应用程序最近一次打开的时间
    private String appColor="00000000";
    private long runTime=0;
    private long[] arrayMonthrunTime=new long[30];
    public String getAppLabel() {
        return appLabel;
    }
    public void setAppLabel(String appName) {
        this.appLabel = appName;
    }
    public Drawable getAppIcon() {
        return appIcon;
    }
    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
    public String getOpenTime(){
        return openTime ;
    }
    public void setOpenTime(String openTime){
        this.openTime=openTime ;
    }
    public long getRunTime(){
        return runTime ;
    }
    public void setRunTime(long runTime){
        this.runTime=runTime ;
    }
    public void addRunTime(int runTime)
    {
        this.runTime= this.runTime+runTime;
    }
    public long[] getArrayMonthrunTime(){
        return arrayMonthrunTime ;
    }
    public void addArrayMonthrunTime(long runTime,int i){
        this.arrayMonthrunTime[i]=runTime ;
    }
    public String getAppColor() {
        return appColor;
    }
    public void setAppColor(String appColor) {
        this.appColor = appColor;
    }
}
