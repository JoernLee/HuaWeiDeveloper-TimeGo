package com.example.timego;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/4/6.
 */

public class HourAdapter extends BaseAdapter {
    private List<String[]> mListAppInfo = null;

    LayoutInflater inflater = null;

    int topWhiteBlock = 8;
    int endWhiteBlock = 8;

    public HourAdapter(Context context, List<String[]> apps){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListAppInfo = apps;
    }

    public  int getCount(){
        return (24*5+topWhiteBlock+endWhiteBlock);
    }

    public Object getItem(int position){
        return mListAppInfo.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertview, ViewGroup arg2){
        View view = null;
        ViewHolder holder = null;
        if (convertview == null || convertview.getTag() == null) {
            view = inflater.inflate(R.layout.item_hour, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else{
            view = convertview ;
            holder = (ViewHolder) convertview.getTag() ;
        }
        /*holder.minuteBlock.setBackgroundColor(Integer.parseInt(mListAppInfo.get(position)[1]));*/
        holder.hourBlock10m.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[1]));
        holder.hourBlock20m.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[4]));
        holder.hourBlock30m.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[7]));
        holder.hourBlock40m.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[10]));
        holder.hourBlock50m.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[13]));
        holder.hourBlock60m.setBackgroundColor(Color.parseColor(mListAppInfo.get(position)[16]));
        if(position < topWhiteBlock || position > mListAppInfo.size() - endWhiteBlock - 1){
            if(position < topWhiteBlock){
                holder.hourNow.setText(String.valueOf(position + 16 )+":00");
            }else {
                holder.hourNow.setText(String.valueOf((position - topWhiteBlock ) % 24) +":00");
            }

        }else {
            if ((position - topWhiteBlock) % 24 == 0){
                holder.hourNow.setText(String.valueOf((position - topWhiteBlock) % 24) + ":00");
                holder.hourNow.setTextColor(Color.parseColor("#12B7F5"));
                holder.hourNow.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }else{
                holder.hourNow.setText(String.valueOf((position - topWhiteBlock) % 24) + ":00");
                holder.hourNow.setTextColor(Color.parseColor("#000000"));
            }
        }
        System.out.println(position);
        return view;
    }



    class ViewHolder {
        View hourBlock10m;
        View hourBlock20m;
        View hourBlock30m;
        View hourBlock40m;
        View hourBlock50m;
        View hourBlock60m;
        TextView hourNow;
        public ViewHolder(View view) {
           this.hourBlock10m = view.findViewById(R.id.hour_block10m);
           this.hourBlock20m = view.findViewById(R.id.hour_block20m);
           this.hourBlock30m = view.findViewById(R.id.hour_block30m);
           this.hourBlock40m = view.findViewById(R.id.hour_block40m);
           this.hourBlock50m = view.findViewById(R.id.hour_block50m);
           this.hourBlock60m = view.findViewById(R.id.hour_block60m);
           this.hourNow = (TextView) view.findViewById(R.id.hour_number);
        }
    }


}