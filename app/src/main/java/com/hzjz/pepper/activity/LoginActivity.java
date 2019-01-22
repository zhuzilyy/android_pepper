package com.hzjz.pepper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.hzjz.pepper.R;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.hzjz.pepper.http.widget.loading.ShapeLoadingDialog;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends Activity {
    private String resmessage = "";
    @BindView(R.id.login_username)
    EditText loginUsername;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.login_fp)
    TextView loginFp;
    @BindView(R.id.login_jp)
    TextView loginJp;
    @BindView(R.id.chk_remember)
    CheckBox chkremember;
    @BindView(R.id.iv_visible)
    ImageView iv_visible;
    private String userName,pwd;
    private boolean isRemember,pwdIsVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userName = Hawk.get("userName", "");
        pwd = Hawk.get("pwd", "");
        isRemember = Hawk.get("chkremember",false);
        if (!TextUtils.isEmpty(userName)){
            loginUsername.setText(userName);
        }
        if (!TextUtils.isEmpty(pwd)){
            loginPassword.setText(pwd);
        }
        chkremember.setChecked(isRemember);
    }
    @OnClick({R.id.login_btn, R.id.login_fp, R.id.login_jp, R.id.chk_remember,R.id.iv_visible})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                if (loginUsername.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.warning_username), Toast.LENGTH_SHORT).show();
                } else if (loginPassword.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.warning_password), Toast.LENGTH_SHORT).show();
                } else {
                    login();
                }
                break;
            case R.id.login_fp:
                break;
            case R.id.login_jp:
                break;
            case R.id.iv_visible:
                if (!pwdIsVisible){
                    //设置EditText的密码为可见的
                    pwdIsVisible = true;
                    iv_visible.setImageResource(R.mipmap.visible_pwd);
                    loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    //loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    loginPassword.setSelection(loginPassword.getText().length());
                }else{
                    //设置密码为隐藏的
                    pwdIsVisible = false;
                    iv_visible.setImageResource(R.mipmap.invisible_pwd);
                    loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    //loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    loginPassword.setSelection(loginPassword.getText().length());
                }
                break;
        }
    }
    //登录
    private void login() {
        final Message msg = new Message();
        final Bundle bd = new Bundle();
        Map<String, String> param = new HashMap<>();
        param.put("email", loginUsername.getText().toString());
        param.put("password", loginPassword.getText().toString());
        DialogUtil.showDialogLoading(LoginActivity.this,"loading");
        OkHttpUtils.postJsonAsyn(ApiConfig.getLogin(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                if (resultDesc.getError_code() == 0) {
                    try {
                        bd.putString("rs", resultDesc.getResult());
                        msg.what = 1;
                        msg.setData(bd);
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } else {
//                                ToastUtil.showText(resultDesc.getReason());
                    resmessage = resultDesc.getReason();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                DialogUtil.hideDialogLoading();
//                            ToastUtil.showText(getResources().getString(R.string.cnfailed));
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bd = msg.getData();
            if (msg.what == 1) {
                JSONObject rs = JSON.parseObject(bd.getString("rs"));
                Hawk.put("authid", rs.getJSONObject("authUser").getString("id"));
                Hawk.put("username", rs.getJSONObject("authUser").getString("username"));
                Hawk.put("add_permission", rs.getJSONObject("authUser").getBoolean("add_permission"));
                //Hawk.put("is_superuser", rs.getJSONObject("authUser").getString("is_superuser"));
                Hawk.put("imgurl", rs.getString("headImg"));
                if (chkremember.isChecked()) {
                    Hawk.put("userName", loginUsername.getText().toString());
                    Hawk.put("pwd", loginPassword.getText().toString());
                }else{
                    Hawk.put("userName","");
                    Hawk.put("pwd", "");
                }
                Hawk.put("chkremember", chkremember.isChecked());
                if (rs.getJSONObject("authUser").getInteger("is_superuser") == 1) {
                    Hawk.put("isadmin", "1");
                } else {
                    Hawk.put("isadmin", "0");
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (msg.what == 2) {
                Toast.makeText(LoginActivity.this, resmessage, Toast.LENGTH_SHORT).show();
            } else if (msg.what == -1) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
