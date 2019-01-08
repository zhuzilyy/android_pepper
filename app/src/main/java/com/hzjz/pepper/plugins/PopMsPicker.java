package com.hzjz.pepper.plugins;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hzjz.pepper.R;

import java.util.ArrayList;
import java.util.List;

public class PopMsPicker extends PopupWindow {
    private View window;
    private TextView poptitle;
    private Button btnok, btncancel;
    private WheelView wvh, wvs;
    private onCheckClickListener onCheckClickListener;
    private Context mContext;
    private String hour, seconds;
    private int sindex = 0;

    public PopMsPicker(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.pop_timepicker, null);
        poptitle = (TextView) window.findViewById(R.id.pop_title);
        hour = String.valueOf(DateUtil.getCurrentHour());;
        seconds = String.valueOf(DateUtil.getCurrentMinutes());;
        if (DateUtil.getCurrentHour() < 10) {
            hour = "0" + hour;
        }
        if (DateUtil.getCurrentMinutes() < 10) {
            seconds = "0" + seconds;
        }
        setTitle();
        sindex = DateUtil.getCurrentMinutes();
        wvh = (WheelView) window.findViewById(R.id.hour);
        wvs = (WheelView) window.findViewById(R.id.seconds);
        wvh.setItems(getHour());
        wvh.setSeletion(DateUtil.getCurrentHour() - 1);
        wvh.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                hour = item;
                setTitle();
                wvs.setSelecting(false, false);
            }
        });
        wvs.setItems(getSecond());
        wvs.setSeletion(sindex - 1);
        wvs.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                seconds = item;
                sindex = selectedIndex;
                setTitle();
                wvh.setSelecting(false, false);
            }
        });
        btnok = (Button) window.findViewById(R.id.pop_yes);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckClickListener.onCheckCallback(hour, seconds);
                dismiss();
            }
        });
        btncancel = (Button) window.findViewById(R.id.pop_no);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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

    public void setOnCheckBackListener(onCheckClickListener callback) {
        this.onCheckClickListener = callback;
    }

    public interface onCheckClickListener {
        void onCheckCallback(String hour, String second);
    }

    private void setTitle() {
        String title = hour + ":" + seconds;
        poptitle.setText(title);
    }

    private List<String> getHour() {
        List<String> args = new ArrayList<>();
        for (int i = 1; i < 25; i++) {
            if (i < 10) {
                args.add("0" + String.valueOf(i));
            } else {
                args.add(String.valueOf(i));
            }
        }
        return args;
    }

    private List<String> getSecond() {
        List<String> args = new ArrayList<>();
        for (int i = 1; i < 61; i++) {
            if (i < 10) {
                args.add("0" + String.valueOf(i));
            } else {
                args.add(String.valueOf(i));
            }
        }
        return args;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}