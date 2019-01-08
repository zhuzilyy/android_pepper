package com.hzjz.pepper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.hzjz.pepper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstSearchAdapter extends BaseAdapter {
    private Context mContext;
    public JSONArray list = new JSONArray();

    public InstSearchAdapter(Context mContext, JSONArray list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void setlist(JSONArray ja) {
        this.list = ja;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        try {
            return list.get(position);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        try {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.inst_search_item, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.isText.setText(list.getJSONObject(position).getString("userEmail"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.is_text)
        TextView isText;
        @BindView(R.id.is_btn_add)
        Button isBtnAdd;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
