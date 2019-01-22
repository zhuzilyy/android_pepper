package com.hzjz.pepper.plugins;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.hzjz.pepper.R;

public class PopOrder extends PopupWindow {
    private View window;
    private Context mContext;
    private RelativeLayout state, dist, subj, course, trname, descr, trdate, trstime, tretime, trlocate, hours;
    private ImageView stateimg, distimg, subjimg, courseimg, trnameimg, descrimg, trdateimg, trstimeimg, tretimeimg, trlocateimg, hoursimg;
    private String ordername = "trainingDate", ordercate = "asc";
    private LinearLayout popbg;
    public PopOrder(Context context, String ordern, String orderc, View.OnClickListener listener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.pop_order, null);
        mContext = context;
        ordername = ordern;
        ordercate = orderc;
        popbg = (LinearLayout) window.findViewById(R.id.pop_bg);
        state = (RelativeLayout) window.findViewById(R.id.state);
        dist = (RelativeLayout) window.findViewById(R.id.dist);
        subj = (RelativeLayout) window.findViewById(R.id.subj);
        course = (RelativeLayout) window.findViewById(R.id.course);
        trname = (RelativeLayout) window.findViewById(R.id.trname);
        descr = (RelativeLayout) window.findViewById(R.id.descr);
        trdate = (RelativeLayout) window.findViewById(R.id.trdate);
        trstime = (RelativeLayout) window.findViewById(R.id.trstime);
        tretime = (RelativeLayout) window.findViewById(R.id.tretime);
        trlocate = (RelativeLayout) window.findViewById(R.id.trlocate);
        hours = (RelativeLayout) window.findViewById(R.id.hours);

        stateimg = (ImageView) window.findViewById(R.id.state_img);
        distimg = (ImageView) window.findViewById(R.id.dist_img);
        subjimg = (ImageView) window.findViewById(R.id.subj_img);
        courseimg = (ImageView) window.findViewById(R.id.course_img);
        trnameimg = (ImageView) window.findViewById(R.id.trname_img);
        descrimg = (ImageView) window.findViewById(R.id.descr_img);
        trdateimg = (ImageView) window.findViewById(R.id.trdate_img);
        trstimeimg = (ImageView) window.findViewById(R.id.trstime_img);
        tretimeimg = (ImageView) window.findViewById(R.id.tretime_img);
        trlocateimg = (ImageView) window.findViewById(R.id.trlocate_img);
        hoursimg = (ImageView) window.findViewById(R.id.hours_img);

        defaultOrder();

        state.setOnClickListener(listener);
        dist.setOnClickListener(listener);
        subj.setOnClickListener(listener);
        course.setOnClickListener(listener);
        trname.setOnClickListener(listener);
        descr.setOnClickListener(listener);
        trdate.setOnClickListener(listener);
        trstime.setOnClickListener(listener);
        tretime.setOnClickListener(listener);
        trlocate.setOnClickListener(listener);
        hours.setOnClickListener(listener);

        //设置SelectPicPopupWindow的View
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
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popbg.setBackground(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        window.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                View viewp = window.findViewById(R.id.pop_bg);
                View view = window.findViewById(R.id.popmain);
                int height = viewp.getTop();
                int ch = view.getHeight();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y > (height + ch)) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    private void defaultOrder() {
        switch (ordername) {
            case "stateName":
                stateimg.setImageResource(getOrderIma());
                break;
            case "districtId":
                distimg.setImageResource(getOrderIma());
                break;
            case "subject":
                subjimg.setImageResource(getOrderIma());
                break;
            case "pepperCourse":
                courseimg.setImageResource(getOrderIma());
                break;
            case "name":
                trnameimg.setImageResource(getOrderIma());
                break;
            case "description":
                descrimg.setImageResource(getOrderIma());
                break;
            case "training_date":
                trdateimg.setImageResource(getOrderIma());
                break;
            case "trainingTimeStart":
                trstimeimg.setImageResource(getOrderIma());
                break;
            case "trainingTimeEnd":
                tretimeimg.setImageResource(getOrderIma());
                break;
            case "geoLocation":
                trlocateimg.setImageResource(getOrderIma());
                break;
            case "credits":
                hoursimg.setImageResource(getOrderIma());
                break;
        }
    }

    private int getOrderIma() {
        int id = R.mipmap.s57;
        if (ordercate.equals("asc")) {
            id = R.mipmap.s55;
        } else if (ordercate.equals("desc")){
            id = R.mipmap.s56;
        }
        return id;
    }
}
