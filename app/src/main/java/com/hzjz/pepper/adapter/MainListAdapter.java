package com.hzjz.pepper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.hzjz.pepper.plugins.DateUtil;
import com.hzjz.pepper.plugins.PopCheckID;
import com.hzjz.pepper.plugins.PopLocation;
import com.hzjz.pepper.plugins.PopRegis;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainListAdapter extends BaseAdapter {
    private Context mContext;
    public boolean isEdit = false, isEditnoti = false;
    public JSONArray list = new JSONArray();
    private JSONArray chklist = new JSONArray();
    private String isadmin = "0";
    PopLocation pl;
    PopRegis pr;
    PopCheckID pc;
    private int left = 0;
    public MainListAdapter(Context context, JSONArray list, String isadmin) {
        this.mContext = context;
        this.list = list;
        this.isadmin = isadmin;
    }

    public void setlist(JSONArray ja) {
        this.isEditnoti = false;
        if (ja.size() > 0) {
            for (int i = 0; i < ja.size(); i++) {
                this.list.add(ja.getJSONObject(i));
            }
            notifyDataSetChanged();
        }
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        this.isEditnoti = true;
        if (!isEdit) {
            this.chklist.clear();
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
                view = LayoutInflater.from(mContext).inflate(R.layout.list_main_item, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            renderView(viewHolder, i);
            if (isEdit) {
                viewHolder.mainChkdetail.setVisibility(View.GONE);
//                    if (!((viewHolder.funcpanel.getLeft() + viewHolder.funcpanel.getWidth()) > viewHolder.funparent.getWidth())) {
//                        moveRight(viewHolder.funcpanel);
//                    }
                viewHolder.itemBtnDetail.setClickable(false);
                viewHolder.chkitem.setVisibility(View.VISIBLE);
                viewHolder.chkEditListener = new ChkEditListener(i);
                viewHolder.chkitem.setOnCheckedChangeListener(viewHolder.chkEditListener);
//                    if ((viewHolder.funcpanel.getLeft() + viewHolder.funcpanel.getWidth()) < (viewHolder.funparent.getWidth() + 100)) {
//                        moveRight(viewHolder.funcpanel);
//                    }
            } else {
                viewHolder.mainChkdetail.setVisibility(View.VISIBLE);
//                    if (isEditnoti) {
//                        moveLeft(viewHolder.funcpanel);
//                    }
                viewHolder.itemBtnDetail.setClickable(true);
                viewHolder.chkitem.setVisibility(View.GONE);
                viewHolder.chkitem.setChecked(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void renderView(ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(list.getJSONObject(i).getString("name"));
        viewHolder.itemTime.setText(list.getJSONObject(i).getString("trainingDate"));
        viewHolder.sdListener = new ShowDetailListener(i);
        viewHolder.itemBtnDetail.setOnClickListener(viewHolder.sdListener);
        viewHolder.slListener = new ShowLocationListener(i);
        viewHolder.btn_location.setOnClickListener(viewHolder.slListener);
        //before training date, show register, else show attend
        if (DateUtil.compare_date(list.getJSONObject(i).getString("trainingDate") + " " + list.getJSONObject(i).getString("trainingTimeStart"))) {
            if (list.getJSONObject(i).getString("allowRegistration").equals("True")) {
                if (this.list.getJSONObject(i).getString("studentStatus").equals("")) {
                    viewHolder.br.setVisibility(View.VISIBLE);
                    viewHolder.bu.setVisibility(View.GONE);
                    viewHolder.srListener = new RegisListener(i);
                    viewHolder.br.setOnClickListener(viewHolder.srListener);
                } else if (this.list.getJSONObject(i).getString("studentStatus").equals("Registered")) {
                    viewHolder.br.setVisibility(View.GONE);
                    viewHolder.bu.setVisibility(View.VISIBLE);
                    viewHolder.delRegisListener = new delRegisListener(i);
                    viewHolder.bu.setOnClickListener(viewHolder.delRegisListener);
                }
            } else {
                viewHolder.br.setVisibility(View.GONE);
                viewHolder.bu.setVisibility(View.GONE);
            }
            viewHolder.bt.setVisibility(View.GONE);
        } else {
            viewHolder.br.setVisibility(View.GONE);
            viewHolder.bu.setVisibility(View.GONE);
            viewHolder.bt.setVisibility(View.VISIBLE);
            if (this.list.getJSONObject(i).getString("allowAttendance").equals("True")
                    && !this.list.getJSONObject(i).getString("studentStatus").equals("Attended")) {
                viewHolder.saListener = new ShowAttendListener(i);
                viewHolder.bt.setOnClickListener(viewHolder.saListener);
            } else {
                viewHolder.bt.setVisibility(View.GONE);
            }
        }
    }

    private void moveRight(View view) {
        int left = view.getLeft() + 100;
        int top = view.getTop();
        int width = view.getWidth();
        int height = view.getHeight();
        view.layout(left, top, left + width, top + height);
    }

    private void moveLeft(View view) {
        int left = view.getLeft() - 100;
        int top = view.getTop();
        int width = view.getWidth();
        int height = view.getHeight();
        view.layout(left, top, left + width, top + height);
    }

    private class ShowDetailListener implements View.OnClickListener {
        int mPosition;
        public ShowDetailListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(mContext.getApplicationContext(), LessonDetailActivity.class);
            intent.putExtra("id", list.getJSONObject(mPosition).getString("id"));
            intent.putExtra("title", list.getJSONObject(mPosition).getString("name"));
            mContext.startActivity(intent);
        }
    }

    private class ShowLocationListener implements View.OnClickListener {
        int mPosition;
        public ShowLocationListener(int inPosition) {
            mPosition = inPosition;
        }
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            pl = new PopLocation(mContext, list.getJSONObject(mPosition).getString("classroom"), list.getJSONObject(mPosition).getString("geoLocation"));
            pl.showAtLocation(((Activity) mContext).findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }
    private class RegisListener implements View.OnClickListener {
        int mPosition;

        public RegisListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            doFunc(mPosition, "true");
        }
    }

    private class delRegisListener implements View.OnClickListener {
        int mPosition;

        public delRegisListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            doFunc(mPosition, "DEL");
        }
    }

    private class ShowAttendListener implements View.OnClickListener {
        int mPosition;

        public ShowAttendListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (list.getJSONObject(mPosition).getString("type").equals("pepper_course")) {
                doFunc(mPosition, "Attended");
            }else {
                pc = new PopCheckID(mContext, list.getJSONObject(mPosition).getString("attendancelId"));
                pc.setOnCheckBackListener(new PopCheckID.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(int result, String content) {
                        if (result == 1) {
                            doFunc(mPosition, "Attended");
                        } else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.CheckFailure), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                pc.showAtLocation(((Activity) mContext).findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (pc != null) {
                    pc.dismiss();
                }
                Bundle bundle = msg.getData();
                Toast.makeText(mContext, mContext.getResources().getString(R.string.CheckSuccess), Toast.LENGTH_SHORT).show();
                list.getJSONObject(funposition).put("allowStudentAttendance", "false");
                notifyDataSetChanged();
            } else if (msg.what == 2) {
                pr = new PopRegis(mContext, "");
                pr.showAtLocation(((Activity) mContext).findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                list.getJSONObject(funposition).put("studentStatus", "true");
                notifyDataSetChanged();
            } else if (msg.what == 3) {
                //list.remove(funposition);
                list.getJSONObject(funposition).put("studentStatus","");
                notifyDataSetChanged();
            } else if (msg.what == -1) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
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
                chklist.add(list.getJSONObject(mPosition));
            } else {
                chklist.remove(list.getJSONObject(mPosition));
            }
            notifyDataSetChanged();
        }
    }

    private static int funposition = 0;
    private void doFunc(final int position, final String stustate) {
        funposition = position;
        Map<String, String> param = new HashMap<>();
        param.put("id", list.getJSONObject(position).getString("id"));
        //param.put("studentId", Hawk.get("authid").toString());
        param.put("user_id", Hawk.get("authid").toString());
        param.put("join", stustate);
        DialogUtil.showDialogLoading(mContext,"loading");
        OkHttpUtils.postJsonAsyn(ApiConfig.updatePepregStudent(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                DialogUtil.hideDialogLoading();
                super.onSuccess(resultDesc);
                Message msgca = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        if (stustate.equals("true")) {
                            msgca.what = 2;
                            list.getJSONObject(position).put("studentStatus","Registered");
                            notifyDataSetChanged();
                        } else if (stustate.equals("Attended")) {
                            msgca.what = 1;
                        } else if (stustate.equals("DEL")) {
                            msgca.what = 3;
                        }
                        handler.sendMessage(msgca);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        msgca.what = -1;
                        handler.sendMessage(msgca);
                    }
                } else {
                    msgca.what = -1;
                    handler.sendMessage(msgca);
                }
            }
            @Override
            public void onFailure(int code, String message) {
                DialogUtil.hideDialogLoading();
                super.onFailure(code, message);
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }


    final static class ViewHolder {
        @BindView(R.id.item_title)
        TextView itemTitle;
        @BindView(R.id.item_time)
        TextView itemTime;
        @BindView(R.id.main_chkdetail)
        LinearLayout mainChkdetail;
        @BindView(R.id.funparent)
        LinearLayout funparent;
        @BindView(R.id.funcpanel)
        LinearLayout funcpanel;
        @BindView(R.id.btn_location)
        Button btn_location;
        @BindView(R.id.btn_regis)
        Button br;
        @BindView(R.id.btn_unregis)
        Button bu;
        @BindView(R.id.btn_att)
        Button bt;
        @BindView(R.id.item_btn_detail)
        LinearLayout itemBtnDetail;
        @BindView(R.id.chk_item)
        CheckBox chkitem;
        ShowDetailListener sdListener;
        ShowLocationListener slListener;
        RegisListener srListener;
        delRegisListener delRegisListener;
        ShowAttendListener saListener;
        ChkEditListener chkEditListener;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
