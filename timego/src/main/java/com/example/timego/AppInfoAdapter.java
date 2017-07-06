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

public class AppInfoAdapter extends BaseAdapter {
    private List<AppInfo>  mListAppInfo = null;

    LayoutInflater inflater = null;

    public AppInfoAdapter(Context context, List<AppInfo> apps){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListAppInfo = apps;
    }

    public  int getCount(){
       // return 24*60;
        return mListAppInfo.size();
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
        AppInfo appInfo = (AppInfo) getItem(position);
        holder.minuteBlock20s.setBackgroundColor(Color.parseColor(appInfo.getAppColor()));
        holder.minuteBlock40s.setBackgroundColor(Color.parseColor(appInfo.getAppColor()));
        holder.minuteBlock60s.setBackgroundColor(Color.parseColor(appInfo.getAppColor()));
        holder.minuteNow.setText(String.valueOf(position % 60));
        return view;
    }



    class ViewHolder {
        View minuteBlock20s;
        View minuteBlock40s;
        View minuteBlock60s;
        TextView minuteNow;
        public ViewHolder(View view) {
           this.minuteBlock20s = view.findViewById(R.id.minute_block20s);
           this.minuteBlock40s = view.findViewById(R.id.minute_block40s);
           this.minuteBlock60s = view.findViewById(R.id.minute_block60s);
           this.minuteNow = (TextView) view.findViewById(R.id.minute_number);
        }
    }


}