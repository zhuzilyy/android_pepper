package com.hzjz.pepper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.NaviPara;
import com.hzjz.pepper.R;
import com.hzjz.pepper.activity.LessonDetailActivity;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.hzjz.pepper.http.utils.MapUtil;
import com.hzjz.pepper.http.utils.PackageUtil;
import com.hzjz.pepper.plugins.DateUtil;
import com.hzjz.pepper.plugins.PopCheckID;
import com.hzjz.pepper.plugins.PopLocation;
import com.hzjz.pepper.plugins.PopRegis;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
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
        //首先判断是不是管理员
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
                //判断是不是管理员
                if(Hawk.get("isadmin").toString().equals("0")){
                    //不是管理员但是有权限就行编辑
                    if (list.getJSONObject(i).getString("all_delete").equals("1")){
                        viewHolder.chkitem.setVisibility(View.VISIBLE);
                    }else if(list.getJSONObject(i).getString("all_delete").equals("0")){
                        //不是管理员没有权限就行编辑
                        viewHolder.chkitem.setVisibility(View.INVISIBLE);
                    }
                }else{
                    //是管理员
                    viewHolder.chkitem.setVisibility(View.VISIBLE);
                }
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
                String max_registration = list.getJSONObject(i).getString("max_registration");
                String register_num = list.getJSONObject(i).getString("register_num");
                int intMaxRegistration = 0,intRegisterNum=0;
                if (!TextUtils.isEmpty(max_registration) &&!TextUtils.isEmpty(register_num) ){
                    intMaxRegistration =Integer.parseInt(max_registration);
                    intRegisterNum = Integer.parseInt(register_num);
                }
                if (this.list.getJSONObject(i).getString("studentStatus").equals("") && intMaxRegistration>intRegisterNum) {
                    viewHolder.br.setVisibility(View.VISIBLE);
                    viewHolder.btn_addWaitList.setVisibility(View.GONE);
                    viewHolder.btn_removeWaitList.setVisibility(View.GONE);
                    viewHolder.bu.setVisibility(View.GONE);
                    viewHolder.srListener = new RegisListener(i);
                    viewHolder.br.setOnClickListener(viewHolder.srListener);
                    //显示加入到waitlist
                } else if (this.list.getJSONObject(i).getString("studentStatus").equals("Registered")) {
                    viewHolder.br.setVisibility(View.GONE);
                    viewHolder.bu.setVisibility(View.VISIBLE);
                    viewHolder.btn_addWaitList.setVisibility(View.GONE);
                    viewHolder.btn_removeWaitList.setVisibility(View.GONE);
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
                viewHolder.br.setVisibility(View.GONE);
                viewHolder.saListener = new ShowAttendListener(i);
                viewHolder.bt.setOnClickListener(viewHolder.saListener);
            } else {
                viewHolder.bt.setVisibility(View.GONE);
            }
        }

        //判断显示attendance的显示和隐藏
            //进行过注册
            if (list.getJSONObject(i).getString("studentStatus").equals("Registered") &&
                    //可以参加
                    list.getJSONObject(i).getString("allowAttendance").equals("True") &&
                    //时间范围在开始和结束时间之间
                    !DateUtil.compare_date(list.getJSONObject(i).getString("trainingDate") + " " + list.getJSONObject(i).getString("trainingTimeStart")) &&
                    DateUtil.compare_date(list.getJSONObject(i).getString("trainingDate") + " " + list.getJSONObject(i).getString("trainingTimeEnd"))
                    ){
                viewHolder.saListener = new ShowAttendListener(i);
                viewHolder.bt.setOnClickListener(viewHolder.saListener);
                viewHolder.bt.setVisibility(View.VISIBLE);
            }else{
                viewHolder.bt.setVisibility(View.GONE);
            }
            //判断addWaitlist的显示和隐藏
            if (DateUtil.compare_date(list.getJSONObject(i).getString("trainingDate") + " " + list.getJSONObject(i).getString("trainingTimeStart"))
                    && list.getJSONObject(i).getString("allow_waitlist").equals("True")) {
                String allowRegistration = list.getJSONObject(i).getString("allowRegistration");
                String max_registration = list.getJSONObject(i).getString("max_registration");
                String register_num = list.getJSONObject(i).getString("register_num");
                String studentStatus =list.getJSONObject(i).getString("studentStatus");
                int intMaxRegistration = 0,intRegisterNum=0;
                if (!TextUtils.isEmpty(max_registration) &&!TextUtils.isEmpty(register_num) ){
                    intMaxRegistration =Integer.parseInt(max_registration);
                    intRegisterNum = Integer.parseInt(register_num);
                }
                if (studentStatus.equals("Waitlist")){
                    viewHolder.br.setVisibility(View.GONE);
                    viewHolder.bu.setVisibility(View.GONE);
                    viewHolder.btn_addWaitList.setVisibility(View.GONE);
                    viewHolder.btn_removeWaitList.setVisibility(View.VISIBLE);
                    viewHolder.removeWaitListListener = new RemoveWaitListListener(i);
                    viewHolder.btn_removeWaitList.setOnClickListener(viewHolder.removeWaitListListener);
                }else if (studentStatus.equals("") && allowRegistration.equals("True") && !(intMaxRegistration>intRegisterNum)){
                    viewHolder.btn_addWaitList.setVisibility(View.VISIBLE);
                    viewHolder.btn_removeWaitList.setVisibility(View.GONE);
                    viewHolder.br.setVisibility(View.GONE);
                    viewHolder.bu.setVisibility(View.GONE);
                    viewHolder.addWaitListListener = new AddWaitListListener(i);
                    viewHolder.btn_addWaitList.setOnClickListener(viewHolder.addWaitListListener);
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
            intent.putExtra("permission", list.getJSONObject(mPosition).getString("all_edit"));
            mContext.startActivity(intent);
        }
    }
    //添加到等待列表
    private class AddWaitListListener implements View.OnClickListener {
        int mPosition;
        public AddWaitListListener(int inPosition) {
            mPosition = inPosition;
        }
        @Override
        public void onClick(View v) {
            doFunc(mPosition, "addWaitList");
        }
    }
    //移除等待列表
    private class RemoveWaitListListener implements View.OnClickListener {
        int mPosition;
        public RemoveWaitListListener(int inPosition) {
            mPosition = inPosition;
        }
        @Override
        public void onClick(View v) {
            doFunc(mPosition, "removeWaitList");
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
            pl.setLocationListener(new PopLocation.LocationListener() {
                @Override
                public void click() {
                    if (TextUtils.isEmpty(list.getJSONObject(mPosition).getString("geoLocation"))){
                        return;
                    }
                    intoMapApp(mPosition,list.getJSONObject(mPosition).getString("geoLocation"));
                    pl.dismiss();
                }
            });
        }
    }
    //进入地图软件
    private void intoMapApp(int position,String addressName) {
        String geo_props = list.getJSONObject(position).getString("geo_props");
        if (geo_props.length()<20){
            return;
        }
        String substring = geo_props.substring(2, geo_props.length() - 1);
        String[] split = substring.split(",");
        String[] splitLat = split[0].split(":");
        String[] splitLon = split[1].split(":");
        String lat = splitLat[1];
        String lon = splitLon[1];
        String subLat = lat.substring(1,lat.length()-1);
        String subLon = lon.substring(1,lon.length()-1);
        LatLng latLng = new LatLng(Double.parseDouble(subLat),Double.parseDouble(subLon));
        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        naviPara.setTargetPoint(latLng);
        if (PackageUtil.isApplicationAvilible(mContext,"com.autonavi.minimap")){
            try {
                // 调起高德地图导航
                AMapUtils.openAMapNavi(naviPara,mContext);
            } catch (com.amap.api.maps.AMapException e) {
                // 如果没安装会进入异常，调起下载页面
                AMapUtils.getLatestAMapApp(mContext);
            }
            //启动百度地图
        }else if(PackageUtil.isApplicationAvilible(mContext,"com.baidu.BaiduMap")){
            MapUtil.invokeBaiDuMap(mContext,latLng,addressName);
            //启动腾讯地图
        }else if(PackageUtil.isApplicationAvilible(mContext,"com.tencent.map")){
            MapUtil.invoketencentMap(mContext,latLng,addressName);
        }else{
            Toast.makeText(mContext, "请安装地图软件", Toast.LENGTH_SHORT).show();
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
                pc = new PopCheckID(mContext, list.getJSONObject(mPosition).getString("attendancel_id"));
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
                list.getJSONObject(funposition).put("studentStatus", "True");
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
            }else if(msg.what == 4){
                String reason = (String)msg.obj;
                Toast.makeText(mContext,reason,Toast.LENGTH_SHORT).show();
            }else if(msg.what == 5){
                pr = new PopRegis(mContext, "");
                pr.showAtLocation(((Activity) mContext).findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
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
        String url ="";
        //验证chenckid的方法
        if (stustate.equals("Attended")){
            param.put("id", list.getJSONObject(position).getString("id"));
            param.put("user_id", Hawk.get("authid").toString());
            param.put("student_ids", Hawk.get("authid").toString());
            param.put("yn", "true");
            url = ApiConfig.attendancePepregStudent();
        }else if(stustate.equals("addWaitList")){
            param.put("id", list.getJSONObject(position).getString("id"));
            //param.put("studentId", Hawk.get("authid").toString());
            param.put("user_id", Hawk.get("authid").toString());
            param.put("join","true");
            url = ApiConfig.addWaitList();
        }else if(stustate.equals("removeWaitList")){
            param.put("id", list.getJSONObject(position).getString("id"));
            //param.put("studentId", Hawk.get("authid").toString());
            param.put("user_id", Hawk.get("authid").toString());
            param.put("join","false");
            url = ApiConfig.addWaitList();
        }else{
            param.put("id", list.getJSONObject(position).getString("id"));
            //param.put("studentId", Hawk.get("authid").toString());
            param.put("user_id", Hawk.get("authid").toString());
            param.put("join", stustate);
            url = ApiConfig.updatePepregStudent();
        }

        DialogUtil.showDialogLoading(mContext,"loading");
        OkHttpUtils.postJsonAsyn(url, param, new HttpCallback() {
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
                        }else if(stustate.equals("addWaitList")){
                            msgca.what = 5;
                            list.getJSONObject(position).put("studentStatus","Waitlist");
                            notifyDataSetChanged();
                        }else if(stustate.equals("removeWaitList")){
                            list.getJSONObject(position).put("studentStatus","");
                            notifyDataSetChanged();
                        }
                        handler.sendMessage(msgca);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        msgca.what = -1;
                        handler.sendMessage(msgca);
                    }
                } else if (resultDesc.getError_code() == 2){
                    msgca.what = 4;
                    msgca.obj = resultDesc.getReason();
                    handler.sendMessage(msgca);
                }else{
                    msgca.what = -1;
                    handler.sendMessage(msgca);
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
        @BindView(R.id.btn_addWaitList)
        Button btn_addWaitList;
        @BindView(R.id.btn_removeWaitList)
        Button btn_removeWaitList;
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
        AddWaitListListener addWaitListListener;
        RemoveWaitListListener removeWaitListListener;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
