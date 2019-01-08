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
import android.widget.TextView;

import com.hzjz.pepper.R;

public class PopCate extends PopupWindow {
    private onCheckClickListener onCheckClickListener;
    private Context mContext;
    private View window;
    private String param = "";
    private TextView allcate, attt, registed, canregis, notopen, nomore;

    public PopCate(Context context, String param) {
        super(context);
        this.mContext = context;
        this.param = param;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.pop_event_cate, null);

        allcate = (TextView) window.findViewById(R.id.allcate);
        attt = (TextView) window.findViewById(R.id.attt);
        registed = (TextView) window.findViewById(R.id.registed);
        canregis = (TextView) window.findViewById(R.id.canregis);
        notopen = (TextView) window.findViewById(R.id.notopen);
        nomore = (TextView) window.findViewById(R.id.nomore);

        allcate.setOnClickListener(onClickListener);
        attt.setOnClickListener(onClickListener);
        registed.setOnClickListener(onClickListener);
        canregis.setOnClickListener(onClickListener);
        notopen.setOnClickListener(onClickListener);
        nomore.setOnClickListener(onClickListener);
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
                case R.id.allcate:
                    param = "";
                    break;
                case R.id.attt:
                    param = "Attended";
                    break;
                case R.id.registed:
                    param = "Registered";
                    break;
                case R.id.canregis:
                    param = "I can reglster";
                    break;
                case R.id.notopen:
                    param = "Training not yet open";
                    break;
                case R.id.nomore:
                    param = "No More Spots";
                    break;
            }
            onCheckClickListener.onCheckCallback(1, param);
            dismiss();
        }
    };

    public void setOnCheckBackListener(onCheckClickListener callback) {
        this.onCheckClickListener = callback;
    }

    public interface onCheckClickListener {
        void onCheckCallback(int result, String content);
    }
}
