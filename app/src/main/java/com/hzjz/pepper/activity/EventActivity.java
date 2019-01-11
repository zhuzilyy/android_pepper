package com.hzjz.pepper.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hzjz.pepper.R;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.bean.UserEmailBean;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.fragment.CTPager1Fragment;
import com.hzjz.pepper.fragment.CTPager2Fragment;
import com.hzjz.pepper.fragment.CTPager3Fragment;
import com.hzjz.pepper.fragment.CTPager4Fragment;
import com.hzjz.pepper.fragment.CTPager5Fragment;
import com.hzjz.pepper.fragment.CTPager61Fragment;
import com.hzjz.pepper.fragment.CTPager62Fragment;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.plugins.DateUtil;
import com.hzjz.pepper.plugins.ViewPagerSlide;
import com.orhanobut.hawk.Hawk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventActivity extends FragmentActivity implements CTPager1Fragment.OnFragmentInteractionListener, CTPager2Fragment.OnFragmentInteractionListener, CTPager3Fragment.OnFragmentInteractionListener, CTPager4Fragment.OnFragmentInteractionListener, CTPager5Fragment.OnFragmentInteractionListener, CTPager61Fragment.OnFragmentInteractionListener, CTPager62Fragment.OnFragmentInteractionListener {
    Context mContext;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private int pageindex = 0;
    private String ctype = "add";
    private JSONObject jo = new JSONObject(), cachejo = new JSONObject();

    @BindView(R.id.btn_back)
    LinearLayout btnBack;
    @BindView(R.id.maintitle)
    TextView maintitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vp)
    ViewPagerSlide vp;
    @BindView(R.id.container)
    RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);
        mContext = this;
        ctype = getIntent().getStringExtra("type");
        initObject();
        if (ctype.equals("edit")) {
            jo = JSON.parseObject(getIntent().getStringExtra("mjo"));
            cachejo.putAll(jo);
        }
        initFragment();
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        vp.setSlide(false);
        vp.setOffscreenPageLimit(7);
        vp.setCurrentItem(pageindex);
    }

    private void initFragment() {
        mFragments.add(CTPager1Fragment.newInstance(ctype, jo));
        mFragments.add(CTPager2Fragment.newInstance(ctype, jo));
        mFragments.add(CTPager3Fragment.newInstance(ctype, jo));
        mFragments.add(CTPager4Fragment.newInstance(ctype, jo));
        mFragments.add(CTPager5Fragment.newInstance(ctype, jo));
        mFragments.add(CTPager61Fragment.newInstance(ctype, jo));
        mFragments.add(CTPager62Fragment.newInstance(ctype, jo));
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    @Override
    public void onFragmentInteraction(String result, JSONObject rjo) {
        if (result.equals("next")) {
            setParam(rjo);
            pageindex++;
            if (pageindex == 5 && Hawk.get("evtype").equals("pd_training")) {
                pageindex++;
            }
        } else if (result.equals("prev")) {
            pageindex--;
            if (pageindex == 5 && Hawk.get("evtype").equals("pd_training")) {
                pageindex--;
            }
        } else if (result.equals("commit")) {
            setParam(rjo);
            commitData();
        }
        vp.setCurrentItem(pageindex);
    }

    public void setParam(JSONObject mjo) {
        if (mjo != null) {
            for (String key : mjo.keySet()) {
                cachejo.put(key, mjo.getString(key));
            }
            cachejo.putAll(mjo);
        }
    }

    public void commitData() {
        //JSONObject param = new JSONObject();
        //Map<String, String> paramdata = new HashMap<>();
        Map<String, String> param = new HashMap<>();
        //添加的email集合
        String emailString = "";
        List<UserEmailBean> infoList = new ArrayList<>();
        JSONArray instructorList = cachejo.getJSONArray("instructorList");
        if (instructorList.size()>0){
            for (int i = 0; i < instructorList.size(); i++) {
                JSONObject jsonObject = instructorList.getJSONObject(i);
                UserEmailBean emailBean = new UserEmailBean();
                emailBean.setEmail(jsonObject.getString("name"));
                emailBean.setAllEdit(jsonObject.getString("allEdit"));
                emailBean.setAllDelete(jsonObject.getString("allDelete"));
                infoList.add(emailBean);
            }
            //email集合拼接成字符串
            for (int i = 0; i <infoList.size(); i++) {
                emailString += infoList.get(i).toString()+",";
            }
            param.put("instructor_emails", emailString);
        }
        if(!TextUtils.isEmpty(cachejo.getString("maxRegistration"))){
            param.put("max_registration", cachejo.getString("maxRegistration"));
        }
        String authId = Hawk.get("authid");
        param.put("user_id", authId);
        param.put("training_date", cachejo.getString("trainingDate"));
        param.put("training_time_start", DateUtil.getStringByFormatString(cachejo.getString("trainingTimeStart"), "HH:mm", "HH:mm:ss"));
        param.put("training_time_end", DateUtil.getStringByFormatString(cachejo.getString("trainingTimeEnd"), "HH:mm", "HH:mm:ss"));
        param.put("district_id", cachejo.getString("districtId"));
        param.put("type", cachejo.getString("type"));
        param.put("school_id", cachejo.getString("schoolId"));
        param.put("name", cachejo.getString("name"));
        param.put("description", cachejo.getString("description"));
        param.put("pepper_course", cachejo.getString("pepperCourse"));
        if (!TextUtils.isEmpty(cachejo.getString("credits"))){
            param.put("credits", cachejo.getString("credits"));
        }
        param.put("attendancel_id", cachejo.getString("attendancelId"));
        param.put("subject", cachejo.getString("subject"));
        param.put("subjectother", cachejo.getString("subjectother"));
        param.put("id", cachejo.getString("id"));
        param.put("classroom", cachejo.getString("geoLocation"));
        param.put("geo_location", cachejo.getString("geoDestination"));
        param.put("geo_props", cachejo.getString("geoProps"));
        param.put("allow_registration", cachejo.getString("allowRegistration"));
        param.put("allow_waitlist", cachejo.getString("allowWaitlist"));
        param.put("allow_attendance", cachejo.getString("allowAttendance"));
        param.put("allow_student_attendance", cachejo.getString("allowStudentAttendance"));
        param.put("allow_validation", cachejo.getString("allowValidation"));
        //param.put("geoProps2", cachejo.getString("geoProps2"));
//            param.put("trainingDate", DateUtil.getStringByFormatString(cachejo.getString("trainingDate"), "yyyy/MM/dd", "yyyy-MM-dd"));
        param.put("user_create_id", Hawk.get("authid").toString());
        param.put("date_create", DateUtil.getCurrentDate("yyyy-MM-dd"));
        param.put("user_modify_id", Hawk.get("authid").toString());
        param.put("date_modify", DateUtil.getCurrentDate("yyyy-MM-dd"));
        //获取本地时间
        param.put("client_time", DateUtil.getCurrentTime());
        if (ctype.equals("edit")) {
            param.put("id", cachejo.getString("id"));
        }
        //paramdata.put("pepregTraining", param.toString());
        OkHttpUtils.postJsonAsyn(ApiConfig.saveTrainingInfoById(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } else {
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });
    }
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(EventActivity.this, getResources()
                            .getString(R.string.savesuccess), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("com.pepper.localbroadcast");
                    intent.putExtra("type", "commitsuc");
                    LocalBroadcastManager.getInstance(EventActivity.this).sendBroadcast(intent);
                    finish();
                    break;
                case 2:
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        Hawk.delete("evtype");
        Hawk.delete("chour");
        super.onDestroy();
    }

    private void initObject() {
        cachejo.put("id", "");
        cachejo.put("type", "");
        cachejo.put("districtId", "");
        cachejo.put("subject", "");
        cachejo.put("name", "");
        cachejo.put("pepperCourse", "");
        cachejo.put("trainingDate", "");
        cachejo.put("geoLocation", "");
        cachejo.put("classroom", "");
        cachejo.put("credits", "");
        cachejo.put("attendancelId", "");
        cachejo.put("allowRegistration", "");
        cachejo.put("maxRegistration", "");
        cachejo.put("allowAttendance", "");
        cachejo.put("allowValidation", "");
        cachejo.put("userCreateId", "");
        cachejo.put("dateCreate", "");
        cachejo.put("userModifyId", "");
        cachejo.put("dateModify", "");
        cachejo.put("allowStudentAttendance", "");
        cachejo.put("trainingTimeStart", "");
        cachejo.put("trainingTimeEnd", "");
        cachejo.put("geoDestination", "");
        cachejo.put("lastDate", "");
        cachejo.put("schoolId", "");
        cachejo.put("subjectother", "");
        cachejo.put("allowWaitlist", "");
        cachejo.put("description", "");
        cachejo.put("geoProps", "");
        cachejo.put("geoProps2", "");
        cachejo.put("instructorList", "");
    }
}
