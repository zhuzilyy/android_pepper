package com.hzjz.pepper.plugins;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.hzjz.pepper.R;

public class PopRegis extends PopupWindow {

//    private Button Outlookc, Googlec;
    private View window;

    public PopRegis(Context context, String param) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.pop_registed, null);
//        Outlookc = (Button) window.findViewById(R.id.btn_OC);
//        Googlec = (Button) window.findViewById(R.id.btn_GC);
//        Outlookc.setOnClickListener(listener);
//        Googlec.setOnClickListener(listener);
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

}