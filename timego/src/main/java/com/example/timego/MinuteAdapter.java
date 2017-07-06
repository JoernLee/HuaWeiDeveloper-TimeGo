package com.example.timego;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/4/6.
 */

public class MinuteAdapter extends BaseAdapter {
    private List<String[]> mListAppInfo = null;

    LayoutInflater inflater = null;

    public MinuteAdapter(Context context, List<String[]> apps){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListAppInfo = apps;
    }

    public  int getCount(){
        return 5*24*60;
    }

    public Object getItem(int position){
        return mListAppInfo.get(position);
    }

    public long getItemId(int position){
        return 0;
    }

    public View getView(int position, View convertview, ViewGroup arg2){
        View view = null;
        ViewHolder holder = null;
        if (convertview == null || convertview.getTag() == null) {
            view = inflater.inflate(R.layout.item_minute, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else{
            view = convertview ;
            holder = (ViewHolder) convertview.getTag() ;
        }
        /*holder.minuteBlock.setBackgroundColor(Integer.parseInt(mListAppInfo.get(position)[1]));*/
        holder.minuteBlock10s.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[1]));
        holder.minuteBlock20s.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[4]));
        holder.minuteBlock30s.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[7]));
        holder.minuteBlock40s.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[10]));
        holder.minuteBlock50s.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[13]));
        holder.minuteBlock60s.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[16]));
        holder.minuteNow.setText(String.valueOf(position % 60));
        holder.minuteNow.setTextColor(Color.parseColor("#000000"));
        return view;
    }



    class ViewHolder {
        View minuteBlock10s;
        View minuteBlock20s;
        View minuteBlock30s;
        View minuteBlock40s;
        View minuteBlock50s;
        View minuteBlock60s;
        TextView minuteNow;
        public ViewHolder(View view) {
            this.minuteBlock10s = view.findViewById(R.id.minute_block10s);
            this.minuteBlock20s = view.findViewById(R.id.minute_block20s);
            this.minuteBlock30s = view.findViewById(R.id.minute_block30s);
            this.minuteBlock40s = view.findViewById(R.id.minute_block40s);
            this.minuteBlock50s = view.findViewById(R.id.minute_block50s);
            this.minuteBlock60s = view.findViewById(R.id.minute_block60s);
            this.minuteNow = (TextView) view.findViewById(R.id.minute_number);
        }
    }


}