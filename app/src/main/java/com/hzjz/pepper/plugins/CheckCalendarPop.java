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

import com.hzjz.pepper.R;

import butterknife.BindView;
import butterknife.OnClick;

public class CheckCalendarPop extends PopupWindow {

    private onCheckClickListener onCheckClickListener;
    private Context mContext;
    private View window;
    private String param = "0",type="";
    LinearLayout layoutweek, layoutmonth, layoutday;
    ImageView imgweek, imgmonth, imgday;

    public CheckCalendarPop(Context context, String param) {
        super(context);
        this.mContext = context;
        this.param = param;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.pop_check_calendar, null);
        layoutmonth = (LinearLayout) window.findViewById(R.id.c_month);
        layoutweek = (LinearLayout) window.findViewById(R.id.c_week);
        layoutday = (LinearLayout) window.findViewById(R.id.c_day);
        imgmonth = (ImageView) window.findViewById(R.id.c_month_i);
        imgweek = (ImageView) window.findViewById(R.id.c_week_i);
        imgday = (ImageView) window.findViewById(R.id.c_day_i);
        layoutmonth.setOnClickListener(onClickListener);
        layoutweek.setOnClickListener(onClickListener);
        layoutday.setOnClickListener(onClickListener);
        CheckParam();
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

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.c_month:
                    param = "0";
                    type ="CALENDAR MONTH";
                    break;
                case R.id.c_week:
                    param = "1";
                    type ="CALENDAR WEEK";
                    break;
                case R.id.c_day:
                    param = "2";
                    type ="CALENDAR DAY";
                    break;
            }
            CheckParam();
            onCheckClickListener.onCheckCallback(1, param,type);
            dismiss();
        }
    };

    private void CheckParam() {
        if (param.equals("0")) {
            imgmonth.setBackgroundResource(R.mipmap.s27);
            imgweek.setBackgroundResource(R.mipmap.s26);
            imgday.setBackgroundResource(R.mipmap.s26);
        } else if (param.equals("1")) {
            imgmonth.setBackgroundResource(R.mipmap.s26);
            imgweek.setBackgroundResource(R.mipmap.s27);
            imgday.setBackgroundResource(R.mipmap.s26);
        } else if (param.equals("2")) {
            imgmonth.setBackgroundResource(R.mipmap.s26);
            imgweek.setBackgroundResource(R.mipmap.s26);
            imgday.setBackgroundResource(R.mipmap.s27);
        }
    }


    public void setOnCheckBackListener(onCheckClickListener callback) {
        this.onCheckClickListener = callback;
    }

    public interface onCheckClickListener {
        void onCheckCallback(int result, String content,String type);
    }
}
