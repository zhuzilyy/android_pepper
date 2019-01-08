package com.hzjz.pepper.plugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONException;
import com.hzjz.pepper.R;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;

import java.util.HashMap;
import java.util.Map;

public class PopCheckID extends PopupWindow {
    private View window;
    private Button btncheck;
    private EditText tid;
    private onCheckClickListener onCheckClickListener;
    private Context mContext;
    private String id, lessonid;

    public PopCheckID(Context context, String param) {
        super(context);
        this.mContext = context;
        this.lessonid = param;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.pop_check, null);
        tid = (EditText) window.findViewById(R.id.ipt_trid);
        btncheck = (Button) window.findViewById(R.id.btn_check);
        btncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = tid.getText().toString();
//                getData();
                if (lessonid.equals(id)) {
                    Message msgpc = new Message();
                    msgpc.what = 101;
                    handlerpc.sendMessage(msgpc);
//                    Looper.prepare();
//
//                    Looper.loop();
                } else {
                    Message msg = new Message();
                    msg.what = 102;
                    handlerpc.sendMessage(msg);
                }
            }
        });
        this.setContentView(window);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x44000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        window.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                View view = window.findViewById(R.id.popmain);
                int height = view.getTop();
                int ch = view.getHeight();
                int width = view.getLeft();
                int cw = view.getWidth();
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y > (height + ch) || x < width || x > (width + cw)) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    private void getData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        OkHttpUtils.postJsonAsyn(ApiConfig.getMainList(), params, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                Message msgpc = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        msgpc.what = 101;
                        handlerpc.sendMessage(msgpc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        msgpc.what = 102;
                        handlerpc.sendMessage(msgpc);
                    }
                } else {
                    msgpc.what = 102;
                    handlerpc.sendMessage(msgpc);
                }
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                DialogUtil.hideDialogLoading();
                Message msgpc = new Message();
                msgpc.what = 102;
                handlerpc.sendMessage(msgpc);
            }
        });
    }

    public void setOnCheckBackListener(PopCheckID.onCheckClickListener callback) {
        this.onCheckClickListener = callback;
    }

    public interface onCheckClickListener {
        void onCheckCallback(int result, String content);
    }

    @SuppressLint("HandlerLeak")
    private Handler handlerpc = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 101) {
                onCheckClickListener.onCheckCallback(1, "OK");
            } else if (msg.what == 102) {
                onCheckClickListener.onCheckCallback(0, "Failed");
            }
        }
    };
}
