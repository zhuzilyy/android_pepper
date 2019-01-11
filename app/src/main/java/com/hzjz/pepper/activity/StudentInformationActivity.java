package com.hzjz.pepper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.google.gson.JsonObject;
import com.hzjz.pepper.R;
import com.hzjz.pepper.adapter.StudentInfoAdapter;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudentInformationActivity extends Activity {

    StudentInfoAdapter adapter;
    JSONArray ja;
    private String id = "",traningName;
    Timer timer = new Timer();

    @BindView(R.id.btn_back)
    LinearLayout btnBack;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.validpanel)
    RelativeLayout validpanel;
    @BindView(R.id.st_name)
    TextView stName;
    @BindView(R.id.attend_switch)
    Switch attendSwitch;
    @BindView(R.id.valid_switch)
    Switch validSwitch;
    @BindView(R.id.switchpanel)
    LinearLayout switchpanel;
    @BindView(R.id.si_list)
    ListView siList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_information);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");
        traningName = getIntent().getStringExtra("name");
        stName.setText(traningName);
        ja = new JSONArray();
        adapter = new StudentInfoAdapter(StudentInformationActivity.this, ja, id);
        adapter.registerDataSetObserver(dso);
        siList.setAdapter(adapter);
        attendSwitch.setOnCheckedChangeListener(onAttCheckedChangeListener);
        validSwitch.setOnCheckedChangeListener(onVadCheckedChangeListener);
        getDate();
    }
    private void getDate() {
        Map<String, String> param = new HashMap<>();
        param.put("user_id",Hawk.get("authid").toString());
        param.put("id",id);
        DialogUtil.showDialogLoading(this,"loading");
        OkHttpUtils.postJsonAsyn(ApiConfig.getStudentList(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        ja = new JSONArray(JSON.parseArray(resultDesc.getResult()));
                        msg.what = 1;
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
            }
        });
    }

    private void doFunc(final String stustate) {
        String url="";
        String ids="";
        for (int i = 0; i <ja.size() ; i++) {
            ids += ja.getJSONObject(i).getString("student_id")+",";
        }
        Map<String, String> param = new HashMap<>();
        if (stustate.equals("Attended") || stustate.equals("Registered")){
            param.put("id",id);
            param.put("user_id", Hawk.get("authid").toString());
            if (stustate.equals("Attended")){
                param.put("yn", "true");
            }else if (stustate.equals("Registered")){
                param.put("yn", "false");
            }
            param.put("student_ids", ids);
            url = ApiConfig.attendancePepregStudent();
        }else if(stustate.equals("Validated")){
            param.put("id",id);
            param.put("user_id", Hawk.get("authid").toString());
            param.put("yn", "true");
            param.put("student_ids", ids);
            url = ApiConfig.validatePepregStudent();
        }
        DialogUtil.showDialogLoading(this,"loading");
        OkHttpUtils.postJsonAsyn(url, param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        if (stustate.equals("Registered")) {
                            msg.what = 2;
                        } else if (stustate.equals("Attended")) {
                            msg.what = 3;
                        } else if (stustate.equals("Validated")) {
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
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }

    private DataSetObserver dso = new DataSetObserver() {
        @Override
        public void onChanged() {
            judgeAll();
        }
    };

    public void judgeAll() {
        attendSwitch.setOnCheckedChangeListener(null);
        validSwitch.setOnCheckedChangeListener(null);
        if (adapter.attja.size() > 0 && adapter.attja.size() == ja.size()) {
            attendSwitch.setChecked(true);
        } else {
            attendSwitch.setChecked(false);
        }
        if (adapter.valja.size() > 0 && adapter.valja.size() == ja.size()) {
            attendSwitch.setChecked(true);
            validSwitch.setChecked(true);
        } else {
            validSwitch.setChecked(false);
        }
        attendSwitch.setOnCheckedChangeListener(onAttCheckedChangeListener);
        validSwitch.setOnCheckedChangeListener(onVadCheckedChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener onAttCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                doFunc("Attended");
            } else {
                doFunc("Registered");
            }
        }
    };
    private CompoundButton.OnCheckedChangeListener onVadCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                doFunc("Validated");
            } else {
                doFunc("Attended");
            }
        }
    };
    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.setNewList(ja);
                    cancelTimer();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 1001;
                            handler.sendMessage(message);
                        }
                    }, 1000, 1000);
                    break;
                case 2:
                    validpanel.setVisibility(View.GONE);
                    getDate();
                    break;
                case 3:
                    validpanel.setVisibility(View.VISIBLE);
                    getDate();
                    break;
                case 4:
                    getDate();
                    break;
                case -1:
                    Toast.makeText(StudentInformationActivity.this, getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
                    break;
                case 1001:
                    judgeAll();
                    cancelTimer();
                    break;
            }
        }
    };

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        adapter.unregisterDataSetObserver(dso);
        super.onDestroy();
    }
}
