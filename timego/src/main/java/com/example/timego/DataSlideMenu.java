package com.example.timego;

/**
 * Created by Jason-Chen on 2017-04-27.
 */
public class DataSlideMenu {
    private static int mCurrentPosition =0;
    private static boolean slideMenuStation =false;
    private static boolean hourAble = false;
    private static boolean minuteAble = false;
    private static int  mLanguageSelection = 0;
    private static int statisticsChange=1;//统计界面饼图与柱状图切换，1表示饼图，2表示柱状图。默认为1；
    private static String dailyWechart = "";


    public static int getA() {
        return mCurrentPosition;
    }

    public static void setA(int a) {
        DataSlideMenu.mCurrentPosition = a;
    }

    public static boolean getB() {
        return slideMenuStation;
    }

    public static void setB(boolean a) {
        DataSlideMenu.slideMenuStation = a;
    }

    public static boolean getHA() {
        return hourAble;
    }

    public static void setHA(boolean a) {
        DataSlideMenu.hourAble = a;
    }

    public static boolean getMA() {
        return minuteAble;
    }

    public static void setMA(boolean a) {
        DataSlideMenu.minuteAble = a;
    }

    public static int getLanguageSelection() {
        return mLanguageSelection;
    }

    public static void setLanguageSelection(int languageSelection) {
        DataSlideMenu.mLanguageSelection = languageSelection;
    }

    public static int getStatisticsState() {
        return statisticsChange;
    }

    public static void setStatisticsState(int a) {
        DataSlideMenu.statisticsChange = a;
    }

    public static String getDailyWechart() {
        return dailyWechart;
    }

    public static void setDailyWechart(String dailyWechart) {
        DataSlideMenu.dailyWechart = dailyWechart;
    }
}
