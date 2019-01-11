package com.hzjz.pepper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.hzjz.pepper.R;
import com.hzjz.pepper.adapter.InstSearchAdapter;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstSearchActivity extends Activity implements TextWatcher, ListView.OnItemClickListener {
    SearchTask mSearchTesk = new SearchTask();
    Handler mHandler = new Handler();
    InstSearchAdapter instSearchAdapter;
    JSONArray cachelist = new JSONArray();

    @BindView(R.id.btn_back)
    LinearLayout btnBack;
    @BindView(R.id.main_search_text)
    EditText mainSearchText;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.list_main)
    ListView listMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inst_search);
        ButterKnife.bind(this);
        mainSearchText.addTextChangedListener(this);
        instSearchAdapter = new InstSearchAdapter(this, cachelist);
        listMain.setAdapter(instSearchAdapter);
        listMain.setOnItemClickListener(this);
    }

    @OnClick({R.id.btn_back, R.id.btn_search})
    public void onViewClicked(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainSearchText.getWindowToken(), 0);
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_search:
                getData();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.e(getPackageName(), "---beforeTextChanged---");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.e(getPackageName(), "---onTextChanged---" + s.length());
        mHandler.removeCallbacks(mSearchTesk);
        mHandler.postDelayed(mSearchTesk, 500);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    class SearchTask implements Runnable {
        @Override
        public void run() {
            Log.e("---SearchTask---", "Start Search");
            getData();
        }
    }

    private void getData() {
        String authid = Hawk.get("authid");
        Map<String, String> param = new HashMap<>();
        param.put("q",mainSearchText.getText().toString());
        param.put("user_id", authid);
        DialogUtil.showDialogLoading(this,"loading");
        OkHttpUtils.postJsonAsyn(ApiConfig.searchEmail(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        cachelist.clear();
                        cachelist = JSON.parseArray(resultDesc.getResult());
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
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                instSearchAdapter.setlist(cachelist);
            } else if (msg.what == -1) {
                Toast.makeText(InstSearchActivity.this, getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainSearchText.getWindowToken(), 0);
        Intent intent = new Intent();
        intent.putExtra("id", cachelist.getJSONObject(position).getString("userId"));
        intent.putExtra("name", cachelist.getJSONObject(position).getString("userEmail"));
        Log.i("xjz",cachelist.getJSONObject(position).getString("userEmail"));
        intent.putExtra("userCreateId", cachelist.getJSONObject(position).getString("userCreateId"));
        intent.putExtra("dateCreate", cachelist.getJSONObject(position).getString("dateCreate"));
        setResult(1, intent);
        finish();
    }
}
