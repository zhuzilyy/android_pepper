package com.hzjz.pepper.plugins;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hzjz.pepper.R;
import com.hzjz.pepper.config.ApiConfig;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PopYmdPicker extends PopupWindow {
    private View window;
    private TextView poptitle;
    private Button btnok, btncancel;
    private WheelView wvm, wvd, wvy;
    private onCheckClickListener onCheckClickListener;
    private Context mContext;
    private String year, month, day;
    private int dayindex = 0;

    public PopYmdPicker(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.pop_datepicker, null);
        poptitle = (TextView) window.findViewById(R.id.pop_title);
        year = String.valueOf(DateUtil.getCurrentYear());;
        month = String.valueOf(DateUtil.getCurrentMonth());;
        if (DateUtil.getCurrentMonth() < 10) {
            month = "0" + month;
        }
        day = String.valueOf(DateUtil.getCurrentDay());;
        if (DateUtil.getCurrentDay() < 10) {
            day = "0" + day;
        }
        setTitle();
        dayindex = DateUtil.getCurrentDay();
        wvy = (WheelView) window.findViewById(R.id.year);
        wvm = (WheelView) window.findViewById(R.id.month);
        wvd = (WheelView) window.findViewById(R.id.day);
        wvy.setItems(getYear());
        wvy.setSeletion(10);
        wvy.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                year = item;
                setTitle();
                wvm.setSelecting(false, false);
                wvd.setSelecting(false, false);
            }
        });
        wvm.setItems(getMonth());
        wvm.setSeletion(DateUtil.getCurrentMonth() - 1);
        wvm.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                month = item;
                setTitle();
                wvy.setSelecting(false, false);
                wvd.setSelecting(false, false);
                wvd.setItems(getdays());
                wvd.setDynamicSelection(dayindex);
            }
        });
        wvd.setItems(getdays());
        wvd.setSeletion(dayindex - 1);
        wvd.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                day = item;
                dayindex = selectedIndex;
                setTitle();
                wvy.setSelecting(false, false);
                wvm.setSelecting(false, false);
            }
        });
        btnok = (Button) window.findViewById(R.id.pop_yes);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckClickListener.onCheckCallback(year, month, day);
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
        void onCheckCallback(String year, String month, String day);
    }

    private void setTitle() {
        String title = month + "/" + day + "/" + year;
        poptitle.setText(title);
    }

    private List<String> getYear() {
        List<String> args = new ArrayList<>();
        int nowyear = DateUtil.getCurrentYear();
        for (int i = 10; i > -1; i--) {
            args.add(String.valueOf(nowyear + i));
        }
        for (int i = 1; i < 11; i++) {
            args.add(String.valueOf(nowyear - i));
        }
        return args;
    }

    private List<String> getMonth() {
        List<String> args = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            if (i < 10) {
                args.add("0" + String.valueOf(i));
            } else {
                args.add(String.valueOf(i));
            }
        }
        return args;
    }

    private List<String> getdays() {
        List<String> args = new ArrayList<>();
        if (!year.equals("") && !month.equals("")) {
            int totalday = DateUtil.getMonthLastDay(Integer.parseInt(year), Integer.parseInt(month)) + 1;
            for (int i = 1; i < totalday; i++) {
                if (i < 10) {
                    args.add("0" + String.valueOf(i));
                } else {
                    args.add(String.valueOf(i));
                }
            }
        }
        return args;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
