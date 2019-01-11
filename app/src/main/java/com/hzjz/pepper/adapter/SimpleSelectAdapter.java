package com.hzjz.pepper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.hzjz.pepper.R;
import com.hzjz.pepper.activity.LessonDetailActivity;
import com.hzjz.pepper.plugins.PopCheckID;
import com.hzjz.pepper.plugins.PopLocation;
import com.hzjz.pepper.plugins.PopRegis;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleSelectAdapter extends BaseAdapter {
    private Context mContext;
    public JSONArray list = new JSONArray();
    private int type = 0;

    public SimpleSelectAdapter(Context context, JSONArray list, int type) {
        this.mContext = context;
        this.list = list;
        this.type = type;
    }

    public void setlist(JSONArray ja, int type) {
        this.list = ja;
        this.type = type;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        try {
            return this.list.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        try {
            return list.get(i);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        try {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item_c, null);
                if (i%2==0){
                    view.setBackgroundColor(Color.parseColor("#ebf2fa"));
                }else{
                    view.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (type == 3) {
                viewHolder.itemTitle.setText(list.getJSONObject(i).getString("display_name"));
            } else {
                viewHolder.itemTitle.setText(list.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    final static class ViewHolder {
        @BindView(R.id.text1)
        TextView itemTitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}