package com.hzjz.pepper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.hzjz.pepper.R;
import com.hzjz.pepper.activity.LessonDetailActivity;
import com.hzjz.pepper.activity.StudentDetailActiivty;
import com.hzjz.pepper.plugins.DateUtil;
import com.hzjz.pepper.plugins.PopCheckID;
import com.hzjz.pepper.plugins.PopLocation;
import com.hzjz.pepper.plugins.PopRegis;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentDetailAdapter extends BaseAdapter {
    private Context mContext;
    public boolean isall = false;
    public JSONArray list = new JSONArray();
    private JSONArray chklist = new JSONArray();
    private String type = "1";

    public StudentDetailAdapter(Context context, JSONArray list, String atype) {
        this.mContext = context;
        this.list = list;
        this.type = atype;
    }

    public void setlist(JSONArray ja) {
        if (ja.size() > 0) {
            for (int i = 0; i < ja.size(); i++) {
                this.list.add(ja.getJSONObject(i));
            }
            notifyDataSetChanged();
        }
    }

    public void setCheckAll(boolean isAll) {
        this.isall = isAll;
        if (!isAll) {
            chklist.clear();
        } else {
            chklist.clear();
            for (int i = 0; i < list.size(); i++) {
                chklist.add(list.getJSONObject(i).getString("id"));
            }
        }
        notifyDataSetChanged();
    }

    public JSONArray getChkList() {
        return this.chklist;
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
                view = LayoutInflater.from(mContext).inflate(R.layout.student_detail_item, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.itemTitle.setText(list.getJSONObject(i).getString("name"));
            viewHolder.itemTime.setText(list.getJSONObject(i).getString("trainingDate"));
            viewHolder.sdListener = new ShowDetailListener(i);
            viewHolder.itemBtnDetail.setOnClickListener(viewHolder.sdListener);
            viewHolder.chkEditListener = new ChkEditListener(i);
            viewHolder.chkItem.setOnCheckedChangeListener(viewHolder.chkEditListener);

            if (chklist.contains(list.getJSONObject(i).getString("id"))) {
                viewHolder.chkItem.setChecked(true);
            } else {
                viewHolder.chkItem.setChecked(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private class ShowDetailListener implements View.OnClickListener {
        int mPosition;

        public ShowDetailListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(mContext, StudentDetailActiivty.class);
            intent.putExtra("id", list.getJSONObject(mPosition).getString("id"));
            intent.putExtra("title", list.getJSONObject(mPosition).getString("name"));
            intent.putExtra("type", type);
            mContext.startActivity(intent);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

            }
        }
    };

    private class ChkEditListener implements CheckBox.OnCheckedChangeListener {
        int mPosition;

        public ChkEditListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                chklist.add(list.getJSONObject(mPosition).getString("id"));
            } else {
                chklist.remove(list.getJSONObject(mPosition).getString("id"));
            }
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        @BindView(R.id.chk_item)
        CheckBox chkItem;
        @BindView(R.id.item_title)
        TextView itemTitle;
        @BindView(R.id.item_time)
        TextView itemTime;
        @BindView(R.id.main_chkdetail)
        LinearLayout mainChkdetail;
        @BindView(R.id.item_btn_detail)
        LinearLayout itemBtnDetail;
        ShowDetailListener sdListener;
        ChkEditListener chkEditListener;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}