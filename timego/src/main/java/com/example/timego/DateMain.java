package com.example.timego;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/18..
 */

 public class DateMain {
    int month;
    int date;
    int dayofweek;
    private int allowHourGlide;
    private int allowMinuteGlide;

    DateMain(int biasDate){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, biasDate);
        month = c.get(Calendar.MONTH) + 1;
        date = c.get(Calendar.DATE );
        dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
        allowHourGlide = 5;
        allowMinuteGlide = 24;
    }
    List<String[]> glideHourDate(boolean init, List<String[]> listItem, int top, int end, List<String[]> listMinuteItem ) {
        //init为true---初始化为全部灰色模块
        if (init) {
            int listNumber = 0;
            //前面加10个白色方块---方便0：00能到中间
            for (int i = 0; i < top; i++) {
                String[] item = new String[18];
                item[0] = "0";//0-10m存储小时信息块
                item[1] = "#66696969";
                item[2] = "#icon10m";
                item[3] = "0";//10-20m存储小时信息块
                item[4] = "#66696969";
                item[5] = "#icon20m";
                item[6] = "0";//20-30m存储小时信息块
                item[7] = "#66696969";
                item[8] = "#icon30m";
                item[9] = "0";//30-40m存储小时信息块
                item[10] = "#66696969";
                item[11] = "#icon40m";
                item[12] = "0";//40-50m存储小时信息块
                item[13] = "#66696969";
                item[14] = "#icon50m";
                item[15] = "0";//50-60m存储小时信息块
                item[16] = "#66696969";
                item[17] = "#icon60m";
                listItem.add(item);
                listNumber++;
            }
            for (int day = 0; day < allowHourGlide; day++) {
                for (int hour = 0; hour < allowMinuteGlide; hour++) {
                    String[] item = new String[18];
                    if (hour == 0) {
                        item[0] = "0";//0-10m存储小时信息块
                        item[1] = "#4169E1";
                        item[2] = "#icon10m";
                    } else {
                        item[0] = "0";//0-10m存储小时信息块
                        item[1] = "#66696969";
                        item[2] = "#icon10m";
                    }

                    item[3] = "0";//10-20m存储小时信息块
                    item[4] = "#66696969";
                    item[5] = "#icon20m";
                    item[6] = "0";//20-30m存储小时信息块
                    item[7] = "#66696969";
                    item[8] = "#icon30m";
                    item[9] = "0";//30-40m存储小时信息块
                    item[10] = "#66696969";
                    item[11] = "#icon40m";
                    item[12] = "0";//40-50m存储小时信息块
                    item[13] = "#66696969";
                    item[14] = "#icon50m";
                    item[15] = "0";//50-60m存储小时信息块
                    item[16] = "#66696969";
                    item[17] = "#icon60m";
                    listItem.add(item);
                    listNumber++;
                }
            }
            for (int j = 0; j < end; j++) {
                String[] item = new String[18];
                item[0] = "0";//0-10m存储小时信息块
                item[1] = "#66696969";
                item[2] = "#icon10m";
                item[3] = "0";//10-20m存储小时信息块
                item[4] = "#66696969";
                item[5] = "#icon20m";
                item[6] = "0";//20-30m存储小时信息块
                item[7] = "#66696969";
                item[8] = "#icon30m";
                item[9] = "0";//30-40m存储小时信息块
                item[10] = "#66696969";
                item[11] = "#icon40m";
                item[12] = "0";//40-50m存储小时信息块
                item[13] = "#66696969";
                item[14] = "#icon50m";
                item[15] = "0";//50-60m存储小时信息块
                item[16] = "#66696969";
                item[17] = "#icon60m";
                listItem.add(item);
                listNumber++;
            }
        } else {
            /* 5.3 Joern更新
            1. 左边小时块根据右边对应分钟块中使用app的分钟块进行蓝色填充
            2. 填充根据10min/5min内的app占用块数的多少进行浅蓝-深蓝的颜色填充
            */
            //遍历所有实际小时块
            int hourDiv = 6;
            String blue1 = "#27CDF2";
            String blue2 = "#27B9F2";
            String blue3 = "#27A4F2";
            String blue4 = "#3B7FD9";
            String blue5 = "#3D71D9";

            for (int x = top; x < top + 120; x++) {
                String[] currentHour = new String[18];
                //得到对应小时块的1Hour信息
                currentHour = listItem.get(x);
                //遍历6次，代表小时细分的区间
                for (int y = 0; y < hourDiv; y++) {
                    //求得每1min区间的代表色
                    String[][] temp = new String[10][18];
                    HashMap<String, Integer> compare10 = new HashMap<String, Integer>();
                    int counter = 0;
                    for (int z = 0; z < 10; z++) {
                        temp[z] = listMinuteItem.get((x - top) * 60 + y * 10 + z);
                        for (int w = 0; w < 6; w++) {
                            if (compare10.containsKey(temp[z][1 + 3 * w])) {
                                counter = (Integer) compare10.get(temp[z][1 + 3 * w]);
                                compare10.put(temp[z][1 + 3 * w], ++counter);
                            } else {
                                compare10.put(temp[z][1 + 3 * w], 1);
                            }
                        }
                    }
                    //10min内的颜色和出现次数已经记录在compare10里面了，求最大次数对应的颜色
                    String mainColor = "#66696969";
                    int maxCount = 0;

                    for (String name : compare10.keySet()) {
                        if (!name.equals("#66696969")) {
                            maxCount = compare10.get(name) + maxCount;
                        }
                    }
                    if (maxCount > 0 && maxCount < 12){
                        mainColor = blue1;
                    }else if (maxCount >= 12 && maxCount < 24){
                        mainColor = blue2;
                    }else if (maxCount >= 24 && maxCount < 36){
                        mainColor = blue3;
                    }else if (maxCount >= 36 && maxCount < 48){
                        mainColor = blue4;
                    }else if (maxCount >= 48 && maxCount <= 60){
                        mainColor = blue5;
                    }
                    //listItem进行set修正颜色
                    currentHour[1+3*y] = mainColor;
                }
                listItem.set(x, currentHour);
                }


                //根据对应分钟内容（10m内颜色最多的）渲染小时块
                //遍历所有小时item
           /* for(int x = top;x < top + 120 ; x++ ){
                //遍历每个小时item的10min区间
                String[] currentHour = new String[18];
                currentHour = listItem.get(x);
                for(int y=0; y < 6; y++){
                    //求得每1min区间的代表色
                    String[][] temp = new String[10][18];
                    HashMap<String, Integer> compare10 = new HashMap<String, Integer>();
                    int counter = 0;
                    for(int z = 0;z < 10;z ++){
                        temp[z] = listMinuteItem.get((x - top) * 60 + y * 10 + z);
                        for(int w =0 ; w < 6;w ++){
                            if(compare10.containsKey(temp[z][1+3*w])){
                                counter = (Integer)compare10.get(temp[z][1+3*w]);
                                compare10.put(temp[z][1+3*w], ++counter);
                            }else {
                                compare10.put(temp[z][1+3*w], 1);
                            }
                        }
                    }
                    //10min内的颜色和出现次数已经记录在compare10里面了，求最大次数对应的颜色
                    String mainColor = "#66696969";
                    int maxCount = 0;

                    for(String name : compare10.keySet()){
                        if(compare10.get(name) > maxCount){
                            maxCount = compare10.get(name);
                            mainColor = name;
                        }
                    }

                    //listItem进行set修正颜色
                    currentHour[1+3*y] = mainColor;
                }
                listItem.set(x, currentHour);
            }*/
            }
        return listItem;
    }

    List<String[]> glideMinuteDate(boolean init, List<String[]> listItem,final List<String[]> appInfo ){
        //init为true---初始化为全部灰色模块
        if (init){
            int listNumber = 0;
          /*  int topWhiteBlock = 10;
            //前面加10个白色方块---方便0：00能到中间
            for(int i = 0; i < topWhiteBlock; i++){

            }*/
            for (int day = 0 ;day < allowHourGlide; day++) {
                for (int hour = 0; hour < allowMinuteGlide; hour++) {
                    for (int minute = 0; minute < 60; minute++) {
                        String[] item = new String[18];
                        item[0] = "";//0-10s存储分钟信息块
                        item[1] = "#66696969";
                        item[2] = "#icon10m";
                        item[3] = "";//10-20s存储分钟信息块
                        item[4] = "#66696969";
                        item[5] = "#icon20m";
                        item[6] = "";//20-30s存储分钟信息块
                        item[7] = "#66696969";
                        item[8] = "#icon30m";
                        item[9] = "";//30-40s存储分钟信息块
                        item[10] = "#66696969";
                        item[11] = "#icon40m";
                        item[12] = "";//40-50s存储分钟信息块
                        item[13] = "#66696969";
                        item[14] = "#icon50m";
                        item[15] = "";//50-60s存储分钟信息块
                        item[16] = "#66696969";
                        item[17] = "#icon60m";
                        listItem.add(item);
                        listNumber++;

                    }
                }
            }
        }else {
            //init为false---根据传入的appinfo修改listItem从而更新adapter
            for (String[] anAppInfo : appInfo) {
                //分离得到开始和结束时间段
                String colorApp = anAppInfo[3];
                String nameApp = anAppInfo[0];
                int startHour = Integer.parseInt(anAppInfo[2].split(":")[0]);
                int startMinute = Integer.parseInt(anAppInfo[2].split(":")[1]);
                int startSecond = Integer.parseInt(anAppInfo[2].split(":")[2]);
                int startDay = Integer.parseInt(anAppInfo[1].split("-")[2]);
                int startMonth = Integer.parseInt(anAppInfo[1].split("-")[1]);
                int startYear = Integer.parseInt(anAppInfo[1].split("-")[0]);
                Calendar calToday=Calendar.getInstance();
                int today = calToday.get(Calendar.DAY_OF_MONTH);
                int tomonth = calToday.get(Calendar.MONTH)+1;
                int toyear = calToday.get(Calendar.YEAR);
                String[] startMinuteInfo = new String[18];
                int idnum;
                //对于几个特殊日期需要处理
//                if(today > 4 && today < 30){
//                    startMinuteInfo =  listItem.get((4 + startDay - today) * 1440 + startHour*60 + startMinute);
//                }
//                if(today > 4)
//                {
//                    idnum=(today- startDay) * 1440 + startHour*60 + startMinute;
//                    startMinuteInfo =  listItem.get(idnum);
//                }
//                else
//                {
                //填充日期是该月日期，比如今天5.3，那么5.3 5.2 5.1属于如下情况
                    if(tomonth==startMonth)
                    {
                        idnum=(4-(today- startDay)) * 1440 + startHour*60 + startMinute;
                        startMinuteInfo =  listItem.get(idnum);
                    }
                    //与今日不属于一个月，比如4.30 4.29，需要加上上个月全天进行处理
                    else
                    {
                        Calendar calToday1=Calendar.getInstance();
                        calToday1.add(Calendar.MONTH,-1);
                        int dayNUm=calToday1.getActualMaximum(Calendar.DATE);
                        idnum=(4-(dayNUm- startDay+today)) * 1440 + startHour*60 + startMinute;
                        startMinuteInfo =  listItem.get(idnum);
                    }

                //}

                //判断当前扫描的时间属于一分钟内哪个10s并替换颜色
                switch (startSecond/10){
                    case 0:
                        startMinuteInfo[1] = colorApp;
                        startMinuteInfo[0] = nameApp;
                        break;
                    case 1:
                        startMinuteInfo[4] = colorApp;
                        startMinuteInfo[3] = nameApp;
                        break;
                    case 2:
                        startMinuteInfo[7] = colorApp;
                        startMinuteInfo[6] = nameApp;
                        break;
                    case 3:
                        startMinuteInfo[10] = colorApp;
                        startMinuteInfo[9] = nameApp;
                        break;
                    case 4:
                        startMinuteInfo[13] = colorApp;
                        startMinuteInfo[12] = nameApp;
                        break;
                    case 5:
                        startMinuteInfo[16] = colorApp;
                        startMinuteInfo[15] = nameApp;
                        break;
                }
              //  int idnum=(4 + startDay - today) * 1440 + startHour*60+startMinute;
                listItem.set(idnum, startMinuteInfo);
            }
        }
        return listItem;
    }



}
