package com.hzjz.pepper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hzjz.pepper.R;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.hzjz.pepper.plugins.PopCheckID;
import com.hzjz.pepper.plugins.PopLocation;
import com.hzjz.pepper.plugins.PopRegis;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LessonDetailActivity extends Activity {

    String title, id, ctype = "";
    PopLocation pl;
    PopRegis pr;
    PopCheckID popCheckID;
    JSONObject jo = new JSONObject();
    JSONArray instjo = new JSONArray();
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.btn_back)
    LinearLayout btnBack;
    @BindView(R.id.btn_edit)
    TextView btnEdit;
    @BindView(R.id.maintitle)
    TextView maintitle;
    @BindView(R.id.btn_location)
    Button btnLocation;
    @BindView(R.id.btn_student)
    Button btnStudent;
    @BindView(R.id.btn_regis)
    Button btnRegis;
    @BindView(R.id.btn_unregis)
    Button btnUnregis;
    @BindView(R.id.btn_att)
    Button btnAtt;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.td_state)
    TextView tdState;
    @BindView(R.id.td_dist)
    TextView tdDist;
    @BindView(R.id.td_subject)
    TextView tdSubject;
    @BindView(R.id.td_course)
    TextView tdCourse;
    @BindView(R.id.td_school)
    TextView tdSchool;
    @BindView(R.id.td_desc)
    TextView tdDesc;
    @BindView(R.id.td_tddate)
    TextView tdTddate;
    @BindView(R.id.td_stime)
    TextView tdStime;
    @BindView(R.id.td_etime)
    TextView tdEtime;
    @BindView(R.id.location_t)
    TextView locationt;
    @BindView(R.id.td_location)
    TextView tdLocation;
    @BindView(R.id.td_hours)
    TextView tdHours;
    @BindView(R.id.td_inst)
    TextView tdInst;
    @BindView(R.id.td_creator)
    TextView tdCreator;
    @BindView(R.id.td_RRA)
    TextView tdRRA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);
        ButterKnife.bind(this);
        title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        maintitle.setText(title);
        if (Hawk.get("isadmin").equals("1")) {
            btnRegis.setVisibility(View.GONE);
            btnAtt.setVisibility(View.GONE);
        }
        rigisBroadcast();
        getData();
    }
    private void getData() {
        Map<String, String> param = new HashMap<>();
        param.put("trainingId", id);
        OkHttpUtils.postJsonAsyn(ApiConfig.getTrainingInfoById(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        //jo = JSON.parseObject(resultDesc.getResult());
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    msg.what = 2;
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

    private void getInstData() {
        Map<String, String> param = new HashMap<>();
        param.put("trainingId", id);
        OkHttpUtils.postJsonAsyn(ApiConfig.getInstructor(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        //instjo = JSON.parseArray(resultDesc.getResult());
                        msg.what = 3;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    msg.what = 2;
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
            if (msg.what == 1) {
                getInstData();
                initdata();
            } else if (msg.what == 2) {
                Toast.makeText(LessonDetailActivity.this, getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 3) {
                String inststr = "";
                if (instjo.size() > 0) {
                    for (int i = 0; i < instjo.size(); i++) {
                        inststr = inststr + instjo.getJSONObject(i).getString("userEmail") + ", ";
                    }
                    inststr = inststr.substring(0, inststr.length() - 1);
                }
                tdInst.setText(inststr);
            } else if (msg.what == -1){
                Toast.makeText(LessonDetailActivity.this, getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 4) {
                if (popCheckID != null) {
                    popCheckID.dismiss();
                }
                Toast.makeText(LessonDetailActivity.this, getResources().getString(R.string.CheckSuccess), Toast.LENGTH_SHORT).show();
                btnAtt.setVisibility(View.GONE);
                Intent intent = new Intent("com.pepper.localbroadcast");
                intent.putExtra("type", "commitsuc");
                LocalBroadcastManager.getInstance(LessonDetailActivity.this).sendBroadcast(intent);
            } else if (msg.what == 5) {
                pr = new PopRegis(LessonDetailActivity.this, "");
                pr.showAtLocation(findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                btnRegis.setVisibility(View.GONE);
                btnUnregis.setVisibility(View.VISIBLE);
                Intent intent = new Intent("com.pepper.localbroadcast");
                intent.putExtra("type", "commitsuc");
                LocalBroadcastManager.getInstance(LessonDetailActivity.this).sendBroadcast(intent);
            } else if (msg.what == 6) {
                Toast.makeText(LessonDetailActivity.this, getResources().getString(R.string.pop_delsuccess), Toast.LENGTH_SHORT).show();
                setResult(5);
                finish();
            }
        }
    };

    private void initdata() {
        ctype = jo.getString("type");
        tdState.setText(jo.getString("stateName"));
        tdDist.setText(jo.getString("districtName"));
        tdSubject.setText(jo.getString("subjectName"));
        tdCourse.setText(jo.getString("courseName"));
        tdSchool.setText(jo.getString("schoolName"));
        tdDesc.setText(jo.getString("description"));
        tdTddate.setText(jo.getString("trainingDate"));
        tdStime.setText(jo.getString("trainingTimeStart"));
        tdEtime.setText(jo.getString("trainingTimeEnd"));
//        locationt.setText(jo.getString("classroom"));
        tdLocation.setText(jo.getString("classroom") + "\n" + jo.getString("geoLocation"));
        tdHours.setText(jo.getString("credits"));
        tdCreator.setText(jo.getString("creator"));
        tdRRA.setText("I would like to register for this training");
        btnRegis.setVisibility(View.GONE);
        btnUnregis.setVisibility(View.GONE);
        btnAtt.setVisibility(View.GONE);
//        if (DateUtil.compare_date(jo.getString("trainingDate") + " " + jo.getString("trainingTimeStart"))) {
//            btnAtt.setVisibility(View.GONE);
//            if (jo.getString("allowRegistration").equals("true")) {
//                if (jo.getString("studentStatus").equals("")) {
//                    btnRegis.setVisibility(View.VISIBLE);
//                } else if (jo.getString("studentStatus").equals("Registered")) {
//                    btnUnregis.setVisibility(View.VISIBLE);
//                }
//            }
//        } else {
//            btnRegis.setVisibility(View.GONE);
//            btnUnregis.setVisibility(View.GONE);
//            btnAtt.setVisibility(View.VISIBLE);
//            if (jo.getString("allowAttendance").equals("true") &&
//                    !jo.getString("studentStatus").equals("Attended")) {
//                btnAtt.setVisibility(View.VISIBLE);
//            } else {
//                btnAtt.setVisibility(View.GONE);
//            }
//        }
    }

    @OnClick({R.id.btn_back, R.id.btn_edit, R.id.btn_location, R.id.btn_student, R.id.btn_regis, R.id.btn_unregis, R.id.btn_att})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_edit:
                intent = new Intent(this, EventActivity.class);
                intent.putExtra("type", "edit");
                intent.putExtra("mjo", jo.toString());
                intent.putExtra("id", jo.getString("id"));
                intent.putExtra("title", jo.getString("title"));
                startActivity(intent);
                break;
            case R.id.btn_location:
                pl = new PopLocation(this, jo.getString("classroom"), jo.getString("geoLocation"));
                pl.showAtLocation(container, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_student:
                intent = new Intent(this, StudentInformationActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", title);
                startActivity(intent);
                break;
            case R.id.btn_regis:
                doFunc("Registered");
                break;
            case R.id.btn_unregis:
                doFunc("DEL");
                break;
            case R.id.btn_att:
                if (ctype.equals("pepper_course")) {
                    doFunc("Attended");
                } else {
                    popCheckID = new PopCheckID(this, id);
                    popCheckID.setOnCheckBackListener(new PopCheckID.onCheckClickListener() {
                        @Override
                        public void onCheckCallback(int result, String content) {
                            if (result == 1) {
                                doFunc("Attended");
                            } else {
                                Toast.makeText(LessonDetailActivity.this, getResources().getString(R.string.CheckFailure), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    popCheckID.showAtLocation(container, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
        }
    }

    private void doFunc(final String stustate) {
        Map<String, String> param = new HashMap<>();
        param.put("trainingId", id);
        param.put("studentId", Hawk.get("authid").toString());
        param.put("authId", Hawk.get("authid").toString());
        param.put("studentStatus", stustate);
        OkHttpUtils.postJsonAsyn(ApiConfig.updatePepregStudent(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        if (stustate.equals("Registered")) {
                            msg.what = 5;
                        } else if (stustate.equals("Attended")) {
                            msg.what = 4;
                            handler.sendMessage(msg);
                        } else if (stustate.equals("DEL")) {
                            msg.what = 6;
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

    private void rigisBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.pepper.localbroadcast");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("type").equals("commitsuc")) {
                    finish();
                }
            }
        };
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
