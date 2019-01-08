package com.hzjz.pepper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;

import com.hzjz.pepper.R;

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
        holder.headcolor.setBackgroundColor(context.getResources().getColor(judgeColor(list.getJSONObject(position).getString("studentStatus"))));
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
        RelativeLayout headcolor;
        TextView timeView;
        TextView contView;

        ViewHolder(View view) {
            super(view);
            headcolor = (RelativeLayout) view.findViewById(R.id.head_color);
            timeView = (TextView) view.findViewById(R.id.time_view);
            contView = (TextView) view.findViewById(R.id.cont_view);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("ViewHolder", "onClick--> position = " + getPosition());
//                }
//            });
        }
    }



    protected int judgeColor(String stat) {
        int color = R.color.event_color_01;
        switch (stat) {
            case "Registered":
                color = R.color.event_color_02;
                break;
            case "Attended":
                color = R.color.event_color_01;
                break;
            case "I can reglster":
                color = R.color.event_color_03;
                break;
            case "Training not yet open":
                color = R.color.event_color_04;
                break;
            case "No More Spots":
                color = R.color.event_color_05;
                break;
        }
        return color;
    }
}