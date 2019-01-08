package com.hzjz.pepper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.hzjz.pepper.R;
import com.hzjz.pepper.adapter.StudentDetailAdapter;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UnRegStuActivity extends Activity {
    StudentDetailAdapter studentDetailAdapter;
    private int page = 1, loadstate = 0;
    JSONArray cachelist = new JSONArray();

    @BindView(R.id.btn_back)
    LinearLayout btnBack;
    @BindView(R.id.maintitle)
    TextView maintitle;
    @BindView(R.id.btn_regis)
    LinearLayout btnRegis;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_search_text)
    TextView mainSearchText;
    @BindView(R.id.main_search)
    LinearLayout mainSearch;
    @BindView(R.id.layout_search)
    LinearLayout layoutSearch;
    @BindView(R.id.list_main)
    ListView listMain;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.chk_all)
    CheckBox chkAll;
    @BindView(R.id.btn_unregsel)
    Button btnUnregsel;
    @BindView(R.id.funcpanel)
    RelativeLayout funcpanel;
    @BindView(R.id.container)
    RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_reg_stu);
        ButterKnife.bind(this);

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
        studentDetailAdapter = new StudentDetailAdapter(this, new JSONArray(), "1");
        studentDetailAdapter.registerDataSetObserver(datesetObserver);
        listMain.setAdapter(studentDetailAdapter);
        refreshLayout.autoRefresh(500);
    }

    private void getData() {
        Map<String, String> param = new HashMap<>();
        param.put("page", String.valueOf(page));
        OkHttpUtils.postJsonAsyn(ApiConfig.getMainList(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        cachelist.clear();
                        //cachelist = JSON.parseObject(resultDesc.getResult()).getJSONArray("list");
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
                        refreshLayout.finishRefresh();
                        refreshLayout.setNoMoreData(false);
                        studentDetailAdapter.list.clear();
                        studentDetailAdapter.setlist(cachelist);
                    } else {
                        if (cachelist.size() < 1) {
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        } else {
                            refreshLayout.finishLoadMore();
                            studentDetailAdapter.setlist(cachelist);
                        }
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back, R.id.btn_regis, R.id.main_search, R.id.btn_unregsel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_regis:
                break;
            case R.id.main_search:
                Intent intent = new Intent(this, RegStuSearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("code", 1);
                intent.putExtra("param", bundle);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_unregsel:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == 1) {
                refreshLayout.autoRefresh();
            }
        }
    }

    private DataSetObserver datesetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            if (studentDetailAdapter.getChkList().size() > 0) {

            } else {

            }
        }
    };
}
