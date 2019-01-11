package com.hzjz.pepper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.hzjz.pepper.R;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.hzjz.pepper.plugins.DensityUtil;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentInfoAdapter extends BaseAdapter {
    private Context mContext;
    public JSONArray list = new JSONArray();
    private String id ;
//    public JSONArray attja = new JSONArray();
//    public JSONArray valja = new JSONArray();
    public SparseArray<String> attja = new SparseArray<>();
    public SparseArray<String> valja = new SparseArray<>();

    public StudentInfoAdapter(Context context, JSONArray list, String id) {
        this.mContext = context;
        this.list = list;
        this.id = id;
    }

    public void setNewList(JSONArray nlist) {
        attja.clear();
        valja.clear();
        this.list.clear();
        this.list = nlist;
        for (int x = 0; x < this.list.size(); x++) {
            if (list.getJSONObject(x).getString("status").equals("Attended")) {
                attja.put(x, list.getJSONObject(x).getString("email"));
            }
            if (list.getJSONObject(x).getString("status").equals("Validated")) {
                attja.put(x, list.getJSONObject(x).getString("email"));
                valja.put(x, list.getJSONObject(x).getString("email"));
            }
        }
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
                view = LayoutInflater.from(mContext).inflate(R.layout.si_list_item, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.attendSwitch.setOnCheckedChangeListener(null);
            viewHolder.validSwitch.setOnCheckedChangeListener(null);
            viewHolder.itemName.setText(list.getJSONObject(i).getString("email"));
            viewHolder.itemCredit.setText(list.getJSONObject(i).getString("student_credit"));
            viewHolder.chkChangedListener = new ChkChangedListener(i);
            viewHolder.chkArrow.setOnCheckedChangeListener(viewHolder.chkChangedListener);
            viewHolder.delListener = new delListener(i);
            viewHolder.itemState.setText(list.getJSONObject(i).getString("status"));
            viewHolder.row4.setVisibility(View.GONE);
            viewHolder.attendSwitch.setChecked(false);
            viewHolder.validSwitch.setChecked(false);
            if (list.getJSONObject(i).getString("status").equals("Registered")) {
                viewHolder.row4.setVisibility(View.GONE);
                viewHolder.attendSwitch.setChecked(false);
                viewHolder.validSwitch.setChecked(false);
            }
            if (list.getJSONObject(i).getString("status").equals("Attended")) {
//                attja.add(i, list.getJSONObject(i).getString("userEmail"));
                viewHolder.row4.setVisibility(View.VISIBLE);
                viewHolder.attendSwitch.setChecked(true);
                viewHolder.validSwitch.setChecked(false);
            }
            if (list.getJSONObject(i).getString("status").equals("Validated")) {
//                attja.add(i, list.getJSONObject(i).getString("userEmail"));
//                valja.add(i, list.getJSONObject(i).getString("userEmail"));
                viewHolder.row4.setVisibility(View.VISIBLE);
                viewHolder.attendSwitch.setChecked(true);
                viewHolder.validSwitch.setChecked(true);
            }
            viewHolder.attSwitchListener = new AttSwitchListener(i);
            viewHolder.attendSwitch.setOnCheckedChangeListener(viewHolder.attSwitchListener);
            viewHolder.valSwitchListener = new ValSwitchListener(i);
            viewHolder.validSwitch.setOnCheckedChangeListener(viewHolder.valSwitchListener);
            viewHolder.btnDel.setOnClickListener(viewHolder.delListener);
            if (viewHolder.chkArrow.isChecked()) {
                viewHolder.rowgroup.setVisibility(View.GONE);
                viewHolder.line.setVisibility(View.GONE);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.itemState.getLayoutParams();
                lp.setMarginEnd(DensityUtil.dip2px(mContext, 90));
                viewHolder.itemState.setLayoutParams(lp);
                RelativeLayout.LayoutParams lpc = (RelativeLayout.LayoutParams) viewHolder.itemCredit.getLayoutParams();
                lpc.setMarginEnd(DensityUtil.dip2px(mContext, 90));
                viewHolder.itemCredit.setLayoutParams(lpc);
                RelativeLayout.LayoutParams lpb = (RelativeLayout.LayoutParams) viewHolder.btnDel.getLayoutParams();
                lpb.setMargins(0, 0, 0, 0);
                lpb.addRule(RelativeLayout.BELOW, R.id.row2);
            } else {
                viewHolder.rowgroup.setVisibility(View.VISIBLE);
                viewHolder.line.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.itemState.getLayoutParams();
                lp.setMarginEnd(0);
                viewHolder.itemState.setLayoutParams(lp);
                RelativeLayout.LayoutParams lpc = (RelativeLayout.LayoutParams) viewHolder.itemCredit.getLayoutParams();
                lpc.setMarginEnd(0);
                viewHolder.itemCredit.setLayoutParams(lpc);
                RelativeLayout.LayoutParams lpb = (RelativeLayout.LayoutParams) viewHolder.btnDel.getLayoutParams();
                lpb.setMargins(0, DensityUtil.dip2px(mContext, 10), 0, 0);
                lpb.addRule(RelativeLayout.BELOW, R.id.line);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private class ChkChangedListener implements CheckBox.OnCheckedChangeListener {
        int mPosition;

        public ChkChangedListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            notifyDataSetChanged();
        }
    }

    private class AttSwitchListener implements Switch.OnCheckedChangeListener{
        int mPosition;

        public AttSwitchListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                doFunc(mPosition, "Attended");
            } else {
                doFunc(mPosition, "Registered");
            }
        }
    }



    private class ValSwitchListener implements Switch.OnCheckedChangeListener{
        int mPosition;

        public ValSwitchListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                doFunc(mPosition, "Validated");
            } else {
                doFunc(mPosition, "Attended");
            }
        }
    }

    private class delListener implements View.OnClickListener{
        int mPosition;
        public delListener(int inPosition) {
            mPosition = inPosition;
        }
        @Override
        public void onClick(View v) {
            doFunc(mPosition, "DEL");
        }
    }

    private int funposition = 0;
    private void doFunc(int position, final String stustate) {
        funposition = position;
        Map<String, String> param = new HashMap<>();
        String url="";
        if (stustate.equals("DEL")){
            param.put("id", list.getJSONObject(position).getString("id"));
            param.put("user_id", Hawk.get("authid").toString());
            url = ApiConfig.deletePepregStudent();
        }else if(stustate.equals("Attended") || stustate.equals("Registered")){
            //trainingId
            param.put("id",id);
            param.put("user_id", Hawk.get("authid").toString());
            if (stustate.equals("Attended")){
                param.put("yn", "true");
            }else if (stustate.equals("Registered")){
                param.put("yn", "false");
            }
            param.put("student_ids", list.getJSONObject(position).getString("student_id"));
            url = ApiConfig.attendancePepregStudent();
        }else if(stustate.equals("Validated")){
            param.put("id",id);
            param.put("user_id", Hawk.get("authid").toString());
            param.put("student_ids", list.getJSONObject(position).getString("student_id"));
            param.put("yn", "true");
            url = ApiConfig.validatePepregStudent();
        }
        DialogUtil.showDialogLoading(mContext,"loading");
        OkHttpUtils.postJsonAsyn(url, param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        if (stustate.equals("Registered")) {
                            msg.what = 1;
                        } else if (stustate.equals("Attended")) {
                            msg.what = 2;
                        } else if (stustate.equals("Validated")) {
                            msg.what = 3;
                        } else if (stustate.equals("DEL")) {
                            msg.what = 4;
                        }
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                } else {
                    msg.what = -1;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    list.getJSONObject(funposition).put("status", "Registered");
                    attja.delete(funposition);
                    valja.delete(funposition);
                    notifyDataSetChanged();
                    break;
                case 2:
                    list.getJSONObject(funposition).put("status", "Attended");
                    attja.put(funposition, list.getJSONObject(funposition).getString("userEmail"));
                    valja.delete(funposition);
                    notifyDataSetChanged();
                    break;
                case 3:
                    list.getJSONObject(funposition).put("status", "Validated");
                    valja.put(funposition, list.getJSONObject(funposition).getString("userEmail"));
                    notifyDataSetChanged();
                    break;
                case 4:
                    list.remove(funposition);
                    notifyDataSetChanged();
                    break;
                case -1:
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    static class ViewHolder {
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.chk_arrow)
        CheckBox chkArrow;
        @BindView(R.id.row1)
        RelativeLayout row1;
        @BindView(R.id.item_state)
        TextView itemState;
        @BindView(R.id.rowgroup)
        RelativeLayout rowgroup;
        @BindView(R.id.row2)
        RelativeLayout row2;
        @BindView(R.id.attend_switch)
        Switch attendSwitch;
        @BindView(R.id.row3)
        RelativeLayout row3;
        @BindView(R.id.valid_switch)
        Switch validSwitch;
        @BindView(R.id.row4)
        RelativeLayout row4;
        @BindView(R.id.item_credit)
        TextView itemCredit;
        @BindView(R.id.row5)
        RelativeLayout row5;
        @BindView(R.id.line)
        LinearLayout line;
        @BindView(R.id.btn_del)
        Button btnDel;
        ChkChangedListener chkChangedListener;
        AttSwitchListener attSwitchListener;
        ValSwitchListener valSwitchListener;
        delListener delListener;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
