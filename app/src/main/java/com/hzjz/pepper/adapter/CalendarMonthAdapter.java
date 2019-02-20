package com.hzjz.pepper.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;

import com.hzjz.pepper.R;
import com.hzjz.pepper.plugins.DateUtil;

public class CalendarMonthAdapter extends RecyclerView.Adapter<CalendarMonthAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private JSONArray list;

    public CalendarMonthAdapter(Context context, JSONArray list) {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setNewList(JSONArray ja) {
        this.list = ja;
        notifyDataSetChanged();
    }

    @Override
    public CalendarMonthAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.calendar_month_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //判断颜色
        String studentStatus = list.getJSONObject(position).getString("studentStatus");
        String allowRegistration = list.getJSONObject(position).getString("allowRegistration");
        String allowAttendance = list.getJSONObject(position).getString("allowAttendance");
        String trainingTimeStart = list.getJSONObject(position).getString("trainingTimeStart");
        String trainingTimeEnd = list.getJSONObject(position).getString("trainingTimeEnd");
        String trainingDate = list.getJSONObject(position).getString("trainingDate");
        String register_num = list.getJSONObject(position).getString("register_num");
        String max_registration = list.getJSONObject(position).getString("max_registration");
        String allow_waitlist = list.getJSONObject(position).getString("allow_waitlist");
        int int_register_num = Integer.parseInt(register_num);
        int int_max_registration = Integer.parseInt(max_registration);
        holder.headcolor.setBackgroundColor(Color.parseColor("#00000000"));
        if (TextUtils.isEmpty(studentStatus) && allowRegistration.equals("True") && DateUtil.compare_date(trainingDate+" "+trainingTimeStart) && int_max_registration > int_register_num){
            holder.headcolor.setBackgroundColor(context.getResources().getColor(judgeColor("I can register")));
            holder.cv_item.setVisibility(View.VISIBLE);
        }else if(!TextUtils.isEmpty(studentStatus) && studentStatus.equals("Registered")){
            holder.headcolor.setBackgroundColor(context.getResources().getColor(judgeColor("Registered")));
            holder.cv_item.setVisibility(View.VISIBLE);
        }else if(!TextUtils.isEmpty(studentStatus) && studentStatus.equals("Registered") && allowAttendance.equals("True") && DateUtil.compare_date(trainingDate+" "+trainingTimeStart)){
            holder.headcolor.setBackgroundColor(context.getResources().getColor(judgeColor("Training not yet open")));
            holder.cv_item.setVisibility(View.VISIBLE);
        }else if(!TextUtils.isEmpty(studentStatus) && studentStatus.equals("Attended")){
            holder.headcolor.setBackgroundColor(context.getResources().getColor(judgeColor("Attended")));
            holder.cv_item.setVisibility(View.VISIBLE);
        }else if(TextUtils.isEmpty(studentStatus) && allowRegistration.equals("True") && allow_waitlist.equals("True") && DateUtil.compare_date(trainingDate+" "+trainingTimeStart) && !(int_max_registration > int_register_num)){
            holder.headcolor.setBackgroundColor(context.getResources().getColor(judgeColor("waitlist")));
            holder.cv_item.setVisibility(View.VISIBLE);
        }else if(allow_waitlist.equals("False") && !(int_max_registration > int_register_num) && DateUtil.compare_date(trainingDate+" "+trainingTimeEnd)){
            holder.headcolor.setBackgroundColor(context.getResources().getColor(judgeColor("No More Spots")));
            holder.cv_item.setVisibility(View.VISIBLE);
        }
        //holder.headcolor.setBackgroundColor(context.getResources().getColor(judgeColor(list.getJSONObject(position).getString("studentStatus"))));
        holder.timeView.setText(list.getJSONObject(position).getString("trainingDate") + "  " +
                list.getJSONObject(position).getString("trainingTimeStart") + " - " +
                list.getJSONObject(position).getString("trainingTimeEnd"));
        holder.contView.setText(list.getJSONObject(position).getString("name"));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout headcolor,cv_item;
        TextView timeView;
        TextView contView;

        ViewHolder(View view) {
            super(view);
            headcolor = (RelativeLayout) view.findViewById(R.id.head_color);
            timeView = (TextView) view.findViewById(R.id.time_view);
            contView = (TextView) view.findViewById(R.id.cont_view);
            cv_item = (RelativeLayout) view.findViewById(R.id.cv_item);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("ViewHolder", "onClick--> position = " + getPosition());
//                }
//            });
        }
    }



    protected int judgeColor(String stat) {
        int color = 0;
        switch (stat) {
            case "Registered":
                color = R.color.event_color_02;
                break;
            case "Attended":
                color = R.color.event_color_01;
                break;
            case "I can register":
                color = R.color.event_color_03;
                break;
            case "Training not yet open":
                color = R.color.event_color_04;
                break;
            case "No More Spots":
                color = R.color.event_color_05;
                break;
            case "waitlist":
                color = R.color.event_color_07;
                break;
        }
        return color;
    }
}