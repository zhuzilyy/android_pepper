package com.hzjz.pepper.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hzjz.pepper.R;
import com.hzjz.pepper.adapter.MainListAdapter;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.hzjz.pepper.plugins.PopCheckID;
import com.hzjz.pepper.plugins.PopConfirmWindow;
import com.hzjz.pepper.plugins.PopOrder;
import com.hzjz.pepper.plugins.PopYmdPicker;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;


public class MainActivity extends Activity implements PopCheckID.onCheckClickListener {
    MainListAdapter adapter;
    JSONArray cachelist = new JSONArray();
    private boolean isEdit = false;
    private String authid, isadmin = "0", stateid = "", statename = "", districtId = "", districtname = "", subjectid = "", subjectname = "", pepperCourseid = "", pepperCoursename = "", training_date = "", type = "0";
    private String advancedSearchString = "";
    private String ordername = "training_date", ordercate = "desc";
    private JSONObject jareg = new JSONObject();
    private JSONObject mjo = new JSONObject();
    private int page = 1, loadstate = 0;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;
    PopConfirmWindow PopConfirm;
    PopOrder popOrder;
    PopYmdPicker popYmdPicker;
    private long firstTime = 0;
    @BindView(R.id.profile_btn)
    LinearLayout profileBtn;
    @BindView(R.id.maintitle)
    TextView maintitle;
    @BindView(R.id.calender)
    ImageButton calender;
    @BindView(R.id.addevents)
    ImageButton addevents;
    @BindView(R.id.edit_query)
    ImageButton editQuery;
    @BindView(R.id.list_main)
    ListView listMain;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.main_search)
    LinearLayout mainSearch;
    @BindView(R.id.main_search_text)
    TextView mainSearchtext;
    @BindView(R.id.funcpanel)
    LinearLayout funcpanel;
    @BindView(R.id.btn_order)
    ImageButton btnorder;
    @BindView(R.id.btn_edit)
    Button btnedit;
    @BindView(R.id.btn_del)
    Button btndel;
    @BindView(R.id.tv_noData)
    TextView tv_noData;
    private int totalCount;
    private boolean add_permission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestPermissions();
        if (!Hawk.contains("authid")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        rigisBroadcast();
        isadmin = Hawk.get("isadmin");
        authid = Hawk.get("authid");
        add_permission = Hawk.get("add_permission");
        //普通用户显示添加pd的功能
        if (isadmin != null && isadmin.equals("0")) {
            if (add_permission){
                addevents.setVisibility(View.VISIBLE);
            }else{
                addevents.setVisibility(View.GONE);
            }
            calender.setVisibility(View.VISIBLE);
        }
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                loadstate = 0;
                getData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                page++;
                loadstate = 1;
                getData();
            }
        });
        adapter = new MainListAdapter(this, new JSONArray(), isadmin);
        adapter.registerDataSetObserver(datesetObserver);
        listMain.setAdapter(adapter);
        refreshLayout.autoRefresh(500);
    }
    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(MainActivity.this);
        rxPermission
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Log.i("tag", permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.i("tag", permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.i("tag", permission.name + " is denied.");
                        }
                    }
                });
    }

    private void getData() {
        Map<String, String> param = new HashMap<>();
        param.put("page", page+"");
        param.put("user_id", authid);
        param.put("is_superuser", String.valueOf(isadmin));
        param.put("stateId", stateid);
        param.put("districtId", districtId);
        param.put("subject", subjectid);
        param.put("pepperCourse", pepperCourseid);
        param.put("trainingDate", training_date);
        param.put("advancedSearchString", advancedSearchString);
        param.put("order", ordername);
        param.put("sort", ordercate);
        OkHttpUtils.postJsonAsyn(ApiConfig.getMainList(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                Log.i("tag",resultDesc.toString());
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        cachelist.clear();
                        cachelist = JSON.parseObject(resultDesc.getResult()).getJSONArray("list");
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
                    if (loadstate == 0) {
                        totalCount=0;
                        refreshLayout.finishRefresh();
                        refreshLayout.setNoMoreData(false);
                        adapter.list.clear();
//                        JSONArray ja = new JSONArray();
//                        JSONObject jo = new JSONObject();
//                        jo.put("name", "pmtrainingname");
//                        jo.put("trainingDate", "2018-05-02");
//                        jo.put("allowRegistration", "true");
//                        jo.put("allowStudentAttendance", "true");
//                        jo.put("studentStatus", "");
//                        jo.put("id", "1");
//                        ja.add(jo);
//                        adapter.setlist(ja);
                        adapter.setlist(cachelist);
                        //从这判断是不是有数据
                        if (cachelist.size()==0){
                            refreshLayout.setVisibility(View.GONE);
                            tv_noData.setVisibility(View.VISIBLE);
                        }else{
                            refreshLayout.setVisibility(View.VISIBLE);
                            tv_noData.setVisibility(View.GONE);
                            if (cachelist.size()<10){
                                ClassicsFooter.REFRESH_FOOTER_ALLLOADED = cachelist.size()+ " trainings are loaded";
                            }
                        }
                    } else {
                        totalCount+=cachelist.size();
                        if (cachelist.size() < 1) {
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        } else {
                            refreshLayout.finishLoadMore();
                            adapter.setlist(cachelist);
                            ClassicsFooter.REFRESH_FOOTER_ALLLOADED = totalCount+ " trainings are loaded";
                        }
                    }
                    break;
                case 2:
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadMore();
                    break;
                case 3:
                    PopConfirm.dismiss();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.pop_delsuccess), Toast.LENGTH_SHORT).show();
                    controlEdit();
                    refreshLayout.autoRefresh(300);
                    break;
                case 4:
                    Intent intent = new Intent(MainActivity.this, EventActivity.class);
                    intent.putExtra("type", "edit");
                    intent.putExtra("mjo", mjo.toString());
                    intent.putExtra("id", adapter.getChkList().getJSONObject(0).getString("id"));
                    intent.putExtra("title", adapter.getChkList().getJSONObject(0).getString("title"));
                    startActivity(intent);
                    break;
            }
        }
    };

    private DataSetObserver datesetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            if (adapter.getChkList().size() > 0) {
                funcpanel.setVisibility(View.VISIBLE);
                if (adapter.getChkList().size() > 1) {
                    btnedit.setVisibility(View.GONE);
                } else {
                    //btnedit.setVisibility(View.VISIBLE);
                }
            } else {
                funcpanel.setVisibility(View.GONE);
            }
        }
    };

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

    @OnClick({R.id.profile_btn, R.id.calender, R.id.addevents, R.id.edit_query, R.id.main_search, R.id.btn_order, R.id.btn_edit, R.id.btn_del})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.profile_btn:
                intent = new Intent(this, LogoutActivity.class);
                startActivity(intent);
                break;
            case R.id.calender:
                intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                break;
            case R.id.addevents:
                intent = new Intent(this, EventActivity.class);
                intent.putExtra("type", "add");
                startActivity(intent);
                break;
            case R.id.edit_query:
                controlEdit();
                break;
            case R.id.main_search:
                if (isadmin.equals("0")) {
                    intent = new Intent(this, UserSearchActivity.class);
                } else {
                    intent = new Intent(this, SearchActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putString("type", type);
                bundle.putString("stateid", stateid);
                bundle.putString("statename", statename);
                bundle.putString("districtId", districtId);
                bundle.putString("districtname", districtname);
                bundle.putString("subjectid", subjectid);
                bundle.putString("subjectname", subjectname);
                bundle.putString("pepperCourseid", pepperCourseid);
                bundle.putString("pepperCoursename", pepperCoursename);
                bundle.putString("trainingDate", training_date);
                bundle.putString("advancedSearchString", advancedSearchString);
                bundle.putString("data", jareg.toString());
                intent.putExtra("param", bundle);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_order:
                popOrder = new PopOrder(this, ordername, ordercate, orderlistener);
                popOrder.showAtLocation(this.findViewById(R.id.container), Gravity.TOP | Gravity.START, 0, 0);
                break;
            case R.id.btn_edit:
                getDetailData();
                break;
            case R.id.btn_del:
                PopConfirm = new PopConfirmWindow(this, getResources().getString(R.string.pop_deltrain), listener);
                PopConfirm.showAtLocation(this.findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
        }
    }
    private void getDetailData() {
        Map<String, String> param = new HashMap<>();
        param.put("user_id",Hawk.get("authid").toString());
        param.put("ids", adapter.getChkList().getJSONObject(0).getString("id"));
        OkHttpUtils.postJsonAsyn(ApiConfig.getTrainingInfoById(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        //mjo = JSON.parseObject(resultDesc.getResult());
                        msg.what = 4;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                JSONObject jo = JSON.parseObject(data.getStringExtra("param"));
                type = jo.getString("type");
                if (type.equals("0") || type.equals("1")) {
                    stateid = jo.getString("stateid");
                    statename = jo.getString("statename");
                    districtId = jo.getString("districtId");
                    districtname = jo.getString("districtname");
                    subjectid = jo.getString("subjectid");
                    subjectname = jo.getString("subjectname");
                    pepperCourseid = jo.getString("pepperCourseid");
                    pepperCoursename = jo.getString("pepperCoursename");
                    training_date = jo.getString("trainingDate");
                    advancedSearchString = "";
                    String condition = statename + "  " + districtname + "  " + subjectname + "  " + pepperCoursename + "  " + training_date;
                    if (condition.trim().length() > 0) {
                        mainSearchtext.setText("Regular Searching...");
                    } else {
                        mainSearchtext.setText("");
                    }
                } else if (type.equals("2")) {
                    stateid = "";
                    statename = "";
                    districtId = "";
                    districtname = "";
                    subjectid = "";
                    subjectname = "";
                    pepperCourseid = "";
                    pepperCoursename = "";
                    training_date = "";
                    jareg = jo.getJSONObject("data");
                    advancedSearchString = "";
                    advancedSearchString = splictingStrings(jareg, 1);
                    if (advancedSearchString.trim().length() > 0) {
                        mainSearchtext.setText("Advanced Searching...");
                    } else {
                        mainSearchtext.setText("");
                    }
                }
                refreshLayout.autoRefresh();
            } else if (resultCode == 2) {
                JSONObject jo = JSON.parseObject(data.getStringExtra("param"));
                type = jo.getString("type");
                if (type.equals("0") || type.equals("1")) {
                    stateid = jo.getString("stateid");
                    statename = jo.getString("statename");
                    districtId = jo.getString("districtId");
                    districtname = jo.getString("districtname");
                    subjectid = jo.getString("subjectid");
                    subjectname = jo.getString("subjectname");
                    pepperCourseid = jo.getString("pepperCourseid");
                    pepperCoursename = jo.getString("pepperCoursename");
                    training_date = jo.getString("trainingDate");
                    String condition = statename + "  " + districtname + "  " + subjectname + "  " + pepperCoursename + "  " + training_date;
                    if (condition.trim().length() > 0) {
                        mainSearchtext.setText("Regular Searching...");
                    } else {
                        mainSearchtext.setText("");
                    }
                } else if (type.equals("2")) {
                    stateid = "";
                    statename = "";
                    districtId = "";
                    districtname = "";
                    subjectid = "";
                    subjectname = "";
                    pepperCourseid = "";
                    pepperCoursename = "";
                    training_date = "";
                    jareg = jo.getJSONObject("data");
                    advancedSearchString = "";
                    advancedSearchString = splictingStrings(jareg, 2);
                    if (advancedSearchString.trim().length() > 0) {
                        mainSearchtext.setText("Advanced Searching...");
                    } else {
                        mainSearchtext.setText("");
                    }
                }
                refreshLayout.autoRefresh();
            } else if (resultCode == 5) {
                refreshLayout.autoRefresh();
            }
        }
    }

    private void controlEdit() {
        isEdit = !isEdit;
        if (isEdit) {
            editQuery.setBackgroundResource(R.mipmap.s19);
            refreshLayout.setEnableRefresh(false);
            refreshLayout.setEnableLoadMore(false);
        } else {
            editQuery.setBackgroundResource(R.mipmap.s14);
//            editQuery.setBackgroundResource(getResources().getDrawable(R.mipmap.s13));
            refreshLayout.setEnableRefresh(true);
            refreshLayout.setEnableLoadMore(true);
        }
        adapter.setEdit(isEdit);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.popcfm_no:
                    PopConfirm.dismiss();
                    break;
                case R.id.popcfm_yes:
                    delList();
                    break;
            }
        }
    };

    private void delList() {
        String ids= "";
        Map<String, String> param = new HashMap<>();
        JSONArray ja = new JSONArray();
        for (int i = 0; i < adapter.getChkList().size(); i++) {
            ids += adapter.getChkList().getJSONObject(i).getString("id")+",";
        }
        param.put("user_id", Hawk.get("authid").toString());
        param.put("ids", ids);
        DialogUtil.showDialogLoading(this,"loading");
        OkHttpUtils.postJsonAsyn(ApiConfig.delList(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        msg.what = 3;
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

    private View.OnClickListener orderlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.state:
                    if (!ordername.equals("stateName")) {
                        ordername = "stateName";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
                case R.id.dist:
                    if (!ordername.equals("districtId")) {
                        ordername = "districtId";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
                case R.id.subj:
                    if (!ordername.equals("subject")) {
                        ordername = "subject";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
                case R.id.course:
                    if (!ordername.equals("pepperCourse")) {
                        ordername = "pepperCourse";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
                case R.id.trname:
                    if (!ordername.equals("name")) {
                        ordername = "name";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
                case R.id.descr:
                    if (!ordername.equals("description")) {
                        ordername = "description";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
                case R.id.trdate:
                    if (!ordername.equals("training_date")) {
                        ordername = "training_date";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
                case R.id.trstime:
                    if (!ordername.equals("trainingTimeStart")) {
                        ordername = "trainingTimeStart";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
                case R.id.tretime:
                    if (!ordername.equals("trainingTimeEnd")) {
                        ordername = "trainingTimeEnd";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
                case R.id.trlocate:
                    if (!ordername.equals("geoLocation")) {
                        ordername = "geoLocation";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
                case R.id.hours:
                    if (!ordername.equals("credits")) {
                        ordername = "credits";
                        ordercate = "asc";
                    } else {
                        ordercate = "desc";
                    }
                    break;
            }
            popOrder.dismiss();
            refreshLayout.autoRefresh();
        }
    };

    @Override
    public void onCheckCallback(int result, String content) {
        if (result == 0) {
            Toast.makeText(this, getResources().getString(R.string.CheckSuccess), Toast.LENGTH_SHORT).show();
            refreshLayout.autoRefresh();
        } else {
            Toast.makeText(this, getResources().getString(R.string.CheckFailure), Toast.LENGTH_SHORT).show();
        }
    }

    private void rigisBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.pepper.localbroadcast");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("type").equals("finish")) {
                    finish();
                } else if (intent.getStringExtra("type").equals("commitsuc")) {
                    refreshLayout.autoRefresh();
                }
            }
        };
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        adapter.unregisterDataSetObserver(datesetObserver);
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
    private String splictingStrings(JSONObject sjo, int type) {
        String spline = "";
        if (!sjo.getString("stateId").equals("")) {
            spline = spline + "{\"key\":\"stateId\",";
            spline = spline + "\"value\":\"" + sjo.getString("stateId") + "\"}";
        }
        if (!sjo.getString("districtId").equals("")) {
            if (!sjo.getString("stateId").equals("")) {
                spline = spline + "@-" + sjo.getString("districtOrd") + "@+";
            }
            spline = spline + "{\"key\":\"districtId\",";
            spline = spline + "\"value\":\"" + sjo.getString("districtId") + "\"}";
        }
        if (!sjo.getString("subjectid").equals("")) {
            if (!sjo.getString("stateId").equals("") || !sjo.getString("districtId").equals("")) {
                spline = spline + "@-" + sjo.getString("subjectOrd") + "@+";
            }
            spline = spline + "{\"key\":\"subject\",";
            spline = spline + "\"value\":\"" + sjo.getString("subjectid") + "\"}";
        }
        if (!sjo.getString("date").equals("")) {
            if (!sjo.getString("stateId").equals("") || !sjo.getString("districtId").equals("") || !sjo.getString("subjectid").equals("")) {
                spline = spline + "@-" + sjo.getString("dateOrd") + "@+";
            }
            spline = spline + "{\"key\":\"Date\",";
            spline = spline + "\"value\":\"" + sjo.getString("date") + "\",";
            spline = spline + "\"Method\":\"" + sjo.getString("datemethod") + "\"}";
        }
        if (!sjo.getString("stime").equals("")) {
            if (!sjo.getString("stateId").equals("") || !sjo.getString("districtId").equals("") || !sjo.getString("subjectid").equals("") || !sjo.getString("date").equals("")) {
                spline = spline + "@-" + sjo.getString("stimeOrd") + "@+";
            }
            spline = spline + "{\"key\":\"STime\",";
            spline = spline + "\"value\":\"" + sjo.getString("stime") + "\",";
            spline = spline + "\"Method\":\"" + sjo.getString("stmethod") + "\"}";
        }
        if (!sjo.getString("etime").equals("")) {
            if (!sjo.getString("stateId").equals("") || !sjo.getString("districtId").equals("") || !sjo.getString("subjectid").equals("") || !sjo.getString("date").equals("") || !sjo.getString("stime").equals("")) {
                spline = spline + "@-" + sjo.getString("etimeOrd") + "@+";
            }
            spline = spline + "{\"key\":\"ETime\",";
            spline = spline + "\"value\":\"" + sjo.getString("etime") + "\",";
            spline = spline + "\"Method\":\"" + sjo.getString("etmethod") + "\"}";
        }
        if (spline.startsWith("{") && spline.endsWith("}")){
            spline = "@-AND@+" +spline;
        }
        return spline;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(MainActivity.this, "Press Again To Quit", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;
                    return true;
                } else {
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
