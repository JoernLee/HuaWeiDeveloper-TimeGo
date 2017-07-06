package com.example.timego;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jason-Chen on 2017-03-18.
 */

public class DataChartClass {

    public static void initPieChart(PieChart pieChart) {
        pieChart.setHoleColorTransparent(true);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(40f);  //半径
        pieChart.setTransparentCircleRadius(43f); // 半透明圈
        //pieChart.setHoleRadius(0)  //实心圆
        pieChart.setDrawSliceText(true);//设置隐藏饼图上文字，只显示百分比
        pieChart.setDescription("统计饼状图(h)");
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(90); // 初始旋转角度
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setRotationEnabled(true); // 可以手动旋转
        pieChart.setUsePercentValues(false);  //显示成百分比
        pieChart.setCenterText("0");  //饼状图中间的文字
        pieChart.setCenterTextSize(20);
        Legend mLegend = pieChart.getLegend();  //设置比例图

        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);  //左下边显示
        mLegend.setXEntrySpace(2f);
        mLegend.setYEntrySpace(2f);
        mLegend.setWordWrapEnabled(true);
        mLegend.setFormSize(6f);// 字体
        mLegend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        mLegend.setForm(Legend.LegendForm.SQUARE);//设置比例块形状，默认为方块
        pieChart.animateXY(1000, 1000);  //设置动画


    }


    public static void initBarChart(BarChart barChart) {
        barChart.setDescription("");
        barChart.setHasTransientState(true);
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("You need to provide data for the chart.");
        // 是否显示表格颜色
        barChart.setDrawGridBackground(false);
        // 设置是否可以触摸
        barChart.setTouchEnabled(true);
        // 是否可以拖拽
        barChart.setDragEnabled(true);
        // 是否可以缩放
        barChart.setScaleEnabled(false);
        // 集双指缩放
        barChart.setPinchZoom(false);
        // 设置背景
        barChart.setBackgroundColor(Color.parseColor("#01000000"));
        // 如果打开，背景矩形将出现在已经画好的绘图区域的后边。
        barChart.setDrawGridBackground(false);
        // 集拉杆阴影
        barChart.setDrawBarShadow(false);
        // 图例
        barChart.getLegend().setEnabled(true);


        // 隐藏右边的坐标轴 (就是右边的0 - 100 - 200 - 300 ... 和图表中横线)
        barChart.getAxisRight().setEnabled(false);
        // 隐藏左边的左边轴 (同上)
//        barChart.getAxisLeft().setEnabled(false);

        // 网格背景颜色
        barChart.setGridBackgroundColor(Color.parseColor("#00000000"));
        // 是否显示表格颜色
        barChart.setDrawGridBackground(false);
        // 设置边框颜色
        barChart.setBorderColor(Color.parseColor("#00000000"));
        // 说明颜色
        barChart.setDescriptionColor(Color.parseColor("#00000000"));
        // 拉杆阴影
        barChart.setDrawBarShadow(false);
        // 打开或关闭绘制的图表边框。（环绕图表的线）
        barChart.setDrawBorders(false);


        Legend mLegend = barChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);  //左下边显示
        mLegend.setXEntrySpace(2f);
        mLegend.setYEntrySpace(2f);
        mLegend.setWordWrapEnabled(true);
        mLegend.setFormSize(6f);// 字体
        mLegend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        mLegend.setForm(Legend.LegendForm.SQUARE);//设置比例块形状，默认为方块

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyYValueFormatter1());//自定义y数据格式化方式
        barChart.animateY(1000); // 立即执行的动画,Y轴

    }

    public static void initLineChart(LineChart lineChart) {
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框

        // no description text
        lineChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable / disable grid background
        lineChart.setDrawGridBackground(false); // 是否显示表格颜色
        lineChart.setGridBackgroundColor(Color.rgb(227, 135, 0)); // 表格的的颜色，在这里是是给颜色设置一个透明度

        // enable touch gestures
        lineChart.setTouchEnabled(true); // 设置是否可以触摸

        // enable scaling and dragging
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(true);// 是否可以缩放
        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);//
        lineChart.setBackgroundColor(Color.argb(0, 0, 0, 0));// 设置背景
        lineChart.getAxisRight().setEnabled(false); // 隐藏右边 的坐标轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 让x轴在下面
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyYValueFormatter());//自定义y数据格式化方式


        // add data


        // get the legend (only possible after setting data)
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
        // modify the legend ...
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);  //左下边显示
        mLegend.setXEntrySpace(2f);
        mLegend.setYEntrySpace(2f);
        mLegend.setWordWrapEnabled(true);
        mLegend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        // mLegend.setForm(LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.BLACK);// 颜色
//      mLegend.setTypeface(mTf);// 字体

        lineChart.animateX(2500); // 立即执行的动画,x轴
        lineChart.invalidate();
    }

    public static void showPieChartDaily(PieChart pieChart , final Map<String,List> dailyAppInfoMap) {
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        ArrayList<Entry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
        ArrayList<Integer> colors = new ArrayList<Integer>();
         AppColors appColors=new AppColors();
        double sumApp=0;
        for (Map.Entry<String,List> entry : dailyAppInfoMap.entrySet()) {
            List value = entry.getValue();
            sumApp+=(value.size()*10);
        }
        double sumAppT=0;
        int num=0;
        for (Map.Entry<String,List> entry : dailyAppInfoMap.entrySet()) {
            List value = entry.getValue();
            double sum=0;
                sum+=(value.size()*10);
            if(sum>=sumApp*0.02) {
                sumAppT+=sum;
                xValues.add(entry.getKey());
                int hashCode =Math.abs(entry.getKey().hashCode()) ;
                colors.add(Color.parseColor(appColors.myAppColors[hashCode%22]));

                yValues.add(new Entry((float)sum/3600, num));
                num++;
            }
        }

        if(sumApp-sumAppT>0) {
            xValues.add("其他");
            colors.add(Color.parseColor("#EE4000")) ;
            yValues.add(new Entry((float) (sumApp - sumAppT)/3600, num));
        }

        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues,""/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        // 饼图颜色

        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(10);
        pieDataSet.setValueTextColor(Color.WHITE);
        //DisplayMetrics metrics =getResources().getDisplayMetrics();
        //  float px = 3 * (metrics.densityDpi / 160f);
        float px = 8;

        pieDataSet.setSelectionShift(px); // 选中态多出的长度
        pieDataSet.setHighlightEnabled(true);
        PieData pieData = new PieData(xValues, pieDataSet);
        //设置数据
        pieChart.setData(pieData);
        String sumtime = String .format("%.2f",sumApp/3600.0);


        pieChart.setCenterText(sumtime+"小时");
        pieChart.invalidate();
    }

    public static HashMap<String,String> showPieChartAna(PieChart pieChart , final Map<String,List> dailyAppInfoMap) {
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        ArrayList<Entry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
        ArrayList<Integer> colors = new ArrayList<Integer>();
        AppColors appColors=new AppColors();
        HashMap<String,String> result=new HashMap<>();
        double sumApp=0;
        for (Map.Entry<String,List> entry : dailyAppInfoMap.entrySet()) {
            List value = entry.getValue();
            sumApp+=(value.size()*10);
        }
        double sumAppT=0;
        int num=0;
        float max=0;
        for (Map.Entry<String,List> entry : dailyAppInfoMap.entrySet()) {
            List value = entry.getValue();
            double sum=0;
            sum+=(value.size()*10);
            if(sum>=sumApp*0.02) {
                sumAppT+=sum;
                xValues.add(entry.getKey());
                int hashCode =Math.abs(entry.getKey().hashCode()) ;
                colors.add(Color.parseColor(appColors.myAppColors[hashCode%22]));

                yValues.add(new Entry((float)sum/3600, num));

                if((float)sum/3600>max)
                {
                    max=(float)sum/3600;
                    DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    String p=decimalFormat.format(max);//format 返回的是字符串
                    result.put("a",p);
                    result.put("b",String.valueOf(entry.getKey()));

                }
                num++;
            }
        }

        if(sumApp-sumAppT>0) {
            xValues.add("其他");
            colors.add(Color.parseColor("#EE4000")) ;
            yValues.add(new Entry((float) (sumApp - sumAppT)/3600, num));
        }

        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues,""/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        // 饼图颜色

        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(10);
        pieDataSet.setValueTextColor(Color.WHITE);
        //DisplayMetrics metrics =getResources().getDisplayMetrics();
        //  float px = 3 * (metrics.densityDpi / 160f);
        float px = 8;

        pieDataSet.setSelectionShift(px); // 选中态多出的长度
        pieDataSet.setHighlightEnabled(true);
        PieData pieData = new PieData(xValues, pieDataSet);
        //设置数据
        pieChart.setData(pieData);
        String sumtime = String .format("%.2f",sumApp/3600.0);
        result.put("c",sumtime);

        pieChart.setCenterText(sumtime);
        pieChart.invalidate();
        return result;
    }

    public static void showPieChartMonth(PieChart pieChart , final Map<String, double[]> monthAppInfoMap) {
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        ArrayList<Entry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
        ArrayList<Integer> colors = new ArrayList<Integer>();
        AppColors appColors=new AppColors();
        int num=0;
        double sumApp=0;
        for (Map.Entry<String,double[]> entry : monthAppInfoMap.entrySet()) {
            double[] value = entry.getValue();
            double sum=0;
            for (double i:value)
            {
                sum+=i;
            }
            sumApp+=sum;
        }
        double sumAppT=0;
        for (Map.Entry<String,double[]> entry : monthAppInfoMap.entrySet()) {

            double[] value = entry.getValue();
            double sum=0;
            for (double i:value)
            {
                sum+=i;
            }
            if(sum>=sumApp*0.02) {
                sumAppT+=sum;
                xValues.add(entry.getKey());
                int hashCode =Math.abs(entry.getKey().hashCode()) ;
                colors.add(Color.parseColor(appColors.myAppColors[hashCode%22]));
                yValues.add(new Entry((float) sum/3600, num));
                num++;
            }


        }
        if(sumApp-sumAppT>0) {
            xValues.add("其他");
            colors.add(Color.parseColor("#EE4000")) ;
            yValues.add(new Entry((float) (sumApp - sumAppT)/3600, num));
        }
        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, ""/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        // 饼图颜色
        pieDataSet.setHighlightEnabled(true);
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(10);
        pieDataSet.setValueTextColor(Color.WHITE);
        //DisplayMetrics metrics =getResources().getDisplayMetrics();
        //  float px = 3 * (metrics.densityDpi / 160f);
        float px = 8;

        pieDataSet.setSelectionShift(px); // 选中态多出的长度

        PieData pieData = new PieData(xValues, pieDataSet);
        //设置数据
        pieChart.setData(pieData);
        String sumtime = String .format("%.2f",sumApp/3600.0);
        pieChart.setCenterText(sumtime+"小时");
        pieChart.invalidate();
    }

    public static void showPieChartYear(PieChart pieChart , final Map<String, double[]> yearAppInfoMap) {
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        ArrayList<Entry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
        ArrayList<Integer> colors = new ArrayList<Integer>();
        AppColors appColors=new AppColors();

        double sumApp=0;
        for (Map.Entry<String,double[]> entry : yearAppInfoMap.entrySet()) {
            double[] value = entry.getValue();
            double sum=0;
            for (double i:value)
            {
                sum+=i;
            }
            sumApp+=sum;
        }
        double sumAppT=0;
        int num=0;
        for (Map.Entry<String,double[]> entry : yearAppInfoMap.entrySet()) {

            double[] value = entry.getValue();
            double sum=0;
            for (double i:value)
            {
                sum+=i;
            }

            if(sum>=sumApp*0.02) {
                sumAppT+=sum;
                xValues.add(entry.getKey());
                int hashCode =Math.abs(entry.getKey().hashCode()) ;
                colors.add(Color.parseColor(appColors.myAppColors[hashCode%22]));
                yValues.add(new Entry((float) sum/60, num));
                num++;
            }

        }
        if(sumApp-sumAppT>0) {
            xValues.add("其他");
            colors.add(Color.parseColor("#EE4000")) ;

            yValues.add(new Entry((float) (sumApp - sumAppT)/3600, num));
        }
        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, ""/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        // 饼图颜色

        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(10);
        pieDataSet.setValueTextColor(Color.WHITE);
        //DisplayMetrics metrics =getResources().getDisplayMetrics();
        //  float px = 3 * (metrics.densityDpi / 160f);
        float px = 8;

        pieDataSet.setSelectionShift(px); // 选中态多出的长度
        pieDataSet.setHighlightEnabled(true);
        PieData pieData = new PieData(xValues, pieDataSet);
        //设置数据
        pieChart.setData(pieData);
        String sumtime = String .format("%.2f",sumApp/3600.0);
        pieChart.setCenterText(sumtime+"小时");
        pieChart.invalidate();
    }



    public static void showBarChartDaily(BarChart barChart , final Map<String,List> dailyAppInfoMap) {
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        ArrayList<BarDataSet> bardataSets = new ArrayList<>();
        AppColors appColors=new AppColors();
        double sumApp=0;
        for (Map.Entry<String,List> entry : dailyAppInfoMap.entrySet()) {
            List value = entry.getValue();
            sumApp+=(value.size()*10);
        }
        double sumAppT=0;
        int num=0;
        for (Map.Entry<String,List> entry : dailyAppInfoMap.entrySet()) {
            List value = entry.getValue();
            double sum=0;
            sum+=(value.size()*10);
            if(sum>=sumApp*0.02) {
                sumAppT+=sum;

                int hashCode =Math.abs(entry.getKey().hashCode()) ;
                ArrayList<BarEntry> yValue = new ArrayList<>();
                yValue.add(new BarEntry((float)sum/3600, 0));
                BarDataSet barDataSet = new BarDataSet(yValue,entry.getKey()/*显示在比例图上*/);
                barDataSet.setColor(Color.parseColor(appColors.myAppColors[hashCode%22]));
                barDataSet.setBarSpacePercent(50f);
                bardataSets.add(barDataSet);
                num++;
            }
        }

        if(sumApp-sumAppT>0) {
            ArrayList<BarEntry> yValue = new ArrayList<>();
            yValue.add(new BarEntry((float) (sumApp - sumAppT)/3600, 0));
            BarDataSet barDataSet = new BarDataSet(yValue,"其他"/*显示在比例图上*/);
            barDataSet.setColor(Color.parseColor("#EE4000"));
            barDataSet.setBarSpacePercent(50f);
            bardataSets.add(barDataSet);

        }

        xValues.add(" ");
        BarData barData = new BarData(xValues, bardataSets);
        //设置数据
        barChart.setData(barData);
        String sumtime = String .format("%.2f",sumApp/3600.0);

        barChart.invalidate();
    }



    public static void showBarChartMonth(BarChart barChart , final Map<String, double[]> monthAppInfoMap) {
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<BarDataSet> bardataSets = new ArrayList<>();
        AppColors appColors=new AppColors();
        int num=0;
        double sumApp=0;
        for (Map.Entry<String,double[]> entry : monthAppInfoMap.entrySet()) {
            double[] value = entry.getValue();
            double sum=0;
            for (double i:value)
            {
                sum+=i;
            }
            sumApp+=sum;
        }
        double sumAppT=0;
        for (Map.Entry<String,double[]> entry : monthAppInfoMap.entrySet()) {

            double[] value = entry.getValue();
            double sum=0;
            for (double i:value)
            {
                sum+=i;
            }
            if(sum>=sumApp*0.02) {
                sumAppT+=sum;
                int hashCode =Math.abs(entry.getKey().hashCode()) ;
                ArrayList<BarEntry> yValue = new ArrayList<>();
                yValue.add(new BarEntry((float) sum/3600, 0));
                BarDataSet barDataSet = new BarDataSet(yValue,entry.getKey()/*显示在比例图上*/);
                barDataSet.setColor(Color.parseColor(appColors.myAppColors[hashCode%22]));
                barDataSet.setBarSpacePercent(50f);
                bardataSets.add(barDataSet);
                num++;
            }


        }
        if(sumApp-sumAppT>0) {


            ArrayList<BarEntry> yValue = new ArrayList<>();
            yValue.add(new BarEntry((float) (sumApp - sumAppT)/3600, 0));
            BarDataSet barDataSet = new BarDataSet(yValue,"其他"/*显示在比例图上*/);
            barDataSet.setColor(Color.parseColor("#EE4000"));
            barDataSet.setBarSpacePercent(50f);
            bardataSets.add(barDataSet);
        }


        xValues.add(" ");
        BarData barData = new BarData(xValues, bardataSets);
        //设置数据
        barChart.setData(barData);
        String sumtime = String .format("%.2f",sumApp/3600.0);

        barChart.invalidate();
    }


    public static void showBarChartYear(BarChart barChart , final Map<String, double[]> yearAppInfoMap) {
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<BarDataSet> bardataSets = new ArrayList<>();
        AppColors appColors=new AppColors();
        int num=0;
        double sumApp=0;
        for (Map.Entry<String,double[]> entry : yearAppInfoMap.entrySet()) {
            double[] value = entry.getValue();
            double sum=0;
            for (double i:value)
            {
                sum+=i;
            }
            sumApp+=sum;
        }
        double sumAppT=0;
        for (Map.Entry<String,double[]> entry : yearAppInfoMap.entrySet()) {

            double[] value = entry.getValue();
            double sum=0;
            for (double i:value)
            {
                sum+=i;
            }
            if(sum>=sumApp*0.02) {
                sumAppT+=sum;
                int hashCode =Math.abs(entry.getKey().hashCode()) ;
                ArrayList<BarEntry> yValue = new ArrayList<>();
                yValue.add(new BarEntry((float) sum/3600, 0));
                BarDataSet barDataSet = new BarDataSet(yValue,entry.getKey()/*显示在比例图上*/);
                barDataSet.setColor(Color.parseColor(appColors.myAppColors[hashCode%22]));
                barDataSet.setBarSpacePercent(20f);
                bardataSets.add(barDataSet);
                num++;
            }


        }
        if(sumApp-sumAppT>0) {


            ArrayList<BarEntry> yValue = new ArrayList<>();
            yValue.add(new BarEntry((float) (sumApp - sumAppT)/3600, 0));
            BarDataSet barDataSet = new BarDataSet(yValue,"其他"/*显示在比例图上*/);
            barDataSet.setColor(Color.parseColor("#EE4000"));
            barDataSet.setBarSpacePercent(20f);
            bardataSets.add(barDataSet);
        }


        xValues.add(" ");
        BarData barData = new BarData(xValues, bardataSets);
        //设置数据
        barChart.setData(barData);
        String sumtime = String .format("%.2f",sumApp/3600.0);

        barChart.invalidate();
    }




    public static void showLineChartDaily(LineChart lineChart, final Map<String,List> dailyAppInfoMap, Set<String> appNameSet) {
        ArrayList<String> xValues = new ArrayList<String>();
        AppColors appColors=new AppColors();
        for (int i = 0; i < 24; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xValues.add(i+":00"  );
        }
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        // y轴的数据
        for (String appName : appNameSet) {
            ArrayList<Entry> yValues = new ArrayList<Entry>();
            List<String> appTime= dailyAppInfoMap.get(appName);
            float[]  time=new float[24];
            for (String apptime: appTime) {
                try {
                   String hhmmss=apptime.substring(11,19);
                    String[] hhmmss1=hhmmss.split(":");
                    int hh=Integer.parseInt(hhmmss1[0]);
                    int mm=Integer.parseInt(hhmmss1[1]);
                    int ss=Integer.parseInt(hhmmss1[2]);
                    time[hh]+=10.0;


                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }

            }
            for(int i =0;i<24;i++)
            {
                yValues.add(new Entry(time[i]/60, i));
            }


            LineDataSet lineDataSet = new LineDataSet(yValues,appName /*显示在比例图上*/);
            lineDataSet.setLineWidth(1.75f); // 线宽
            lineDataSet.setCircleSize(3f);// 显示的圆形大小
            int hashCode=Math.abs(appName.hashCode());
            lineDataSet.setColor(Color.parseColor(appColors.myAppColors[hashCode%22]));// 显示颜色
            lineDataSet.setCircleColor(Color.parseColor(appColors.myAppColors[hashCode%22]));// 圆形的颜色
            lineDataSet.setHighLightColor(Color.parseColor(appColors.myAppColors[hashCode%22])); // 高亮的线的颜色
            lineDataSets.add(lineDataSet); // add the datasets
        }
        // create a data object with the datasets


        LineData lineData = new LineData(xValues, lineDataSets);
        lineChart.setData(lineData); // 设置数据
        lineChart.invalidate();
    }

    public static void showLineChartMonth(LineChart lineChart, final Map<String, double[]> monthAppInfoMap, Set<String> appNameSet) {
        ArrayList<String> xValues = new ArrayList<String>();
        AppColors appColors=new AppColors();
        for (int i = 1; i < 32; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xValues.add("" + i);
        }
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        // y轴的数据
        for (String appName : appNameSet) {

            ArrayList<Entry> yValues = new ArrayList<Entry>();
            double[] value = monthAppInfoMap.get(appName);
            for (int i = 0; i <value.length ; i++) {
                yValues.add(new Entry((float) value[i]/60, i+1));
            }
            LineDataSet lineDataSet = new LineDataSet(yValues,appName /*显示在比例图上*/);
            lineDataSet.setLineWidth(1.75f); // 线宽
            lineDataSet.setCircleSize(3f);// 显示的圆形大小
            int hashCode=Math.abs(appName.hashCode());
            lineDataSet.setColor(Color.parseColor(appColors.myAppColors[hashCode%22]));// 显示颜色
            lineDataSet.setCircleColor(Color.parseColor(appColors.myAppColors[hashCode%22]));// 圆形的颜色
            lineDataSet.setHighLightColor(Color.parseColor(appColors.myAppColors[hashCode%22])); // 高亮的线的颜色


            lineDataSets.add(lineDataSet); // add the datasets
        }
        // create a data object with the datasets


        LineData lineData = new LineData(xValues, lineDataSets);
        lineChart.setData(lineData); // 设置数据
        lineChart.invalidate();
    }

    public static void showLineChartYear(LineChart lineChart, final Map<String, double[]> yearAppInfoMapT, Set<String> appNameSet) {
        ArrayList<String> xValues = new ArrayList<String>();
        AppColors appColors=new AppColors();
        for (int i = 1; i < 13; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xValues.add(i+ "月" );
        }
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        // y轴的数据
        for (String appName : appNameSet) {

            ArrayList<Entry> yValues = new ArrayList<Entry>();
            double[] value = yearAppInfoMapT.get(appName);
            for (int i = 0; i <value.length ; i++) {
                yValues.add(new Entry((float) value[i]/60, i+1));
            }
            LineDataSet lineDataSet = new LineDataSet(yValues,appName /*显示在比例图上*/);
            lineDataSet.setLineWidth(1.75f); // 线宽
            lineDataSet.setCircleSize(3f);// 显示的圆形大小
            int hashCode=Math.abs(appName.hashCode());
            lineDataSet.setColor(Color.parseColor(appColors.myAppColors[hashCode%22]));// 显示颜色
            lineDataSet.setCircleColor(Color.parseColor(appColors.myAppColors[hashCode%22]));// 圆形的颜色
            lineDataSet.setHighLightColor(Color.parseColor(appColors.myAppColors[hashCode%22])); // 高亮的线的颜色


            lineDataSets.add(lineDataSet); // add the datasets
        }
        // create a data object with the datasets


        LineData lineData = new LineData(xValues, lineDataSets);
        lineChart.setData(lineData); // 设置数据
        lineChart.invalidate();
    }


   static  class  MyYValueFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0");
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return  mFormat.format(value)+"min";
        }
    }
    static  class  MyYValueFormatter1 implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYValueFormatter1() {
            mFormat = new DecimalFormat("###,###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return  mFormat.format(value)+"h";
        }
    }

}
