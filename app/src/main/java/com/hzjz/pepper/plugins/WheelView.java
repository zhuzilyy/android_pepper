package com.hzjz.pepper.plugins;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hzjz.pepper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 7/1/14.
 */
public class WheelView extends ScrollView {
    private boolean isInitial = true;
    private boolean isSelecting = false;
    private float labelheight = 0.0f;
    private int labelheightint = 0;
    private float layoutmarginend = 0.0f;
    public static final String TAG = WheelView.class.getSimpleName();
    public static final int OFF_SET_DEFAULT = 1;
    int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）
    int displayItemCount; // 每页显示的数量
    int selectedIndex = 1;
    int initialY;
    int totalheight = 0;
    Runnable scrollerTask;
    int newCheck = 50;

    public static class OnWheelViewListener {
        public void onSelected(int selectedIndex, String item) {
        }
    }


    private Context context;
//    private ScrollView scrollView;

    private LinearLayout views;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray stylelist = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        labelheight = stylelist.getDimension(R.styleable.WheelView_labelheight, 0.0f);
        labelheightint = (int) labelheight;
        layoutmarginend = stylelist.getDimension(R.styleable.WheelView_layoutmarginend, 0.0f);
        stylelist.recycle();
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    //    String[] items;
    List<String> items;

    private List<String> getItems() {
        return items;
    }

    public void setItems(List<String> list) {
        if (null == items) {
            items = new ArrayList<String>();
        }
        items.clear();
        items.addAll(list);

        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        totalheight = labelheightint * items.size();

        initData();

    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }


    private void init(Context context) {
        this.context = context;

//        scrollView = ((ScrollView)this.getParent());
//        Log.d(TAG, "scrollview: " + scrollView);
        Log.d(TAG, "parent: " + this.getParent());
//        this.setOrientation(VERTICAL);
        this.setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        views.setGravity(Gravity.CENTER);
        this.addView(views);

        scrollerTask = new Runnable() {
            public void run() {
                isInitial = false;
                int newY = getScrollY();
                if (initialY - newY == 0) { // stopped
                    final int remainder = initialY % labelheightint;
                    final int divided = initialY / labelheightint;
                    if (remainder == 0) {
                        selectedIndex = divided + offset;
//                        selectedIndex = divided;
                        int childSize = views.getChildCount();
                        for (int i = 0; i < childSize; i++) {
                            TextView itemView = (TextView) views.getChildAt(i);
                            if (null == itemView) {
                                return;
                            }
                            if (selectedIndex == i && !isSelecting) {
                                itemView.setBackgroundResource(R.drawable.zdpick);
                                itemView.setTextColor(getResources().getColor(R.color.black));
                            }
                        }
                        onSeletedCallBack();
                    } else {
                        if (remainder > labelheightint / 2) {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder + labelheightint);
                                    selectedIndex = divided + offset + 1;
//                                    selectedIndex = divided + 1;
                                    onSeletedCallBack();
                                }
                            });
                        } else {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder);
                                    selectedIndex = divided + offset;
//                                    selectedIndex = divided;
                                    onSeletedCallBack();
                                }
                            });
                        }
                    }
                } else {
                    initialY = getScrollY();
                    WheelView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    public void startScrollerTask() {
        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
        views.removeAllViews();
        views.layout(0, 0, lp.width, totalheight);
        for (String item : items) {
            views.addView(createView(item));
        }
        views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, labelheightint * displayItemCount));
        this.layout(0, 0, lp.width, labelheightint * displayItemCount);
        refreshItemView(0);
    }


    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams((int) labelheight, (int) labelheight));
        tv.setSingleLine(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getResources().getColor(R.color.gray));
//        int padding = dip2px(15);
//        tv.setPadding(padding, padding, padding, padding);
        return tv;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

//        Log.d(TAG, "l: " + l + ", t: " + t + ", oldl: " + oldl + ", oldt: " + oldt);

//        try {
//            Field field = ScrollView.class.getDeclaredField("mScroller");
//            field.setAccessible(true);
//            OverScroller mScroller = (OverScroller) field.get(this);
//
//
//            if(mScroller.isFinished()){
//                Log.d(TAG, "isFinished...");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        isSelecting = true;
        refreshItemView(t);

        if (t > oldt) {
//            Log.d(TAG, "向下滚动");
            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
//            Log.d(TAG, "向上滚动");
            scrollDirection = SCROLL_DIRECTION_UP;

        }


    }

    private void refreshItemView(int y) {
        int position = y / labelheightint + offset;
        int remainder = y % labelheightint;
        int divided = y / labelheightint;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > labelheightint / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                if (isSelecting) {
                    if (isInitial) {
                        itemView.setBackgroundResource(R.drawable.zdcmn);
                    } else {
                        itemView.setBackgroundResource(R.drawable.zdpick);
                    }
                } else {
                    itemView.setBackgroundResource(R.drawable.zdcmn);
                }
                itemView.setTextColor(getResources().getColor(R.color.black));
            } else {
                if (itemView.getText().equals("")) {
                    itemView.setBackgroundResource(R.drawable.zdnull);
                } else {
                    itemView.setBackground(getResources().getDrawable(R.drawable.zdnull));
                }
                itemView.setTextColor(getResources().getColor(R.color.gray));
            }
        }
    }

    public void setSelecting(boolean selecting, boolean isInitial) {
        this.isInitial = isInitial;
        this.isSelecting = selecting;
        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (itemView.getBackground().getCurrent().getConstantState() == getResources().getDrawable(R.drawable.zdpick).getConstantState() && !isSelecting) {
                itemView.setBackgroundResource(R.drawable.zdcmn);
                itemView.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }

    /**
     * 获取选中区域的边界
     */
    int[] selectedAreaBorder;

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = labelheightint * offset;
            selectedAreaBorder[1] = labelheightint * (offset + 1);
        }
        return selectedAreaBorder;
    }


    private int scrollDirection = -1;
    private static final int SCROLL_DIRECTION_UP = 0;
    private static final int SCROLL_DIRECTION_DOWN = 1;

    Paint paint;
    int viewWidth;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: " + oldh);
        viewWidth = w;
//        setBackgroundDrawable(null);
    }

    /**
     * 选中回调
     */
    private void onSeletedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }

    }

    public void setSeletion(int position) {
        final int p = position;
        selectedIndex = p;
        this.post(new Runnable() {
            @Override
            public void run() {
                WheelView.this.smoothScrollTo(0, p * labelheightint);
            }
        });

    }

    public void setDynamicSelection(int position) {
        final int p = position;
        selectedIndex = p;
        this.post(new Runnable() {
            @Override
            public void run() {
                TextView itemView = (TextView) views.getChildAt(selectedIndex);
                if (isSelecting && !isInitial) {
                    itemView.setBackgroundResource(R.drawable.zdpick);
                } else {
                    itemView.setBackgroundResource(R.drawable.zdcmn);
                }
                itemView.setTextColor(getResources().getColor(R.color.black));
            }
        });
    }

    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    public int getSeletedIndex() {
        return selectedIndex - offset;
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    private OnWheelViewListener onWheelViewListener;

    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return (int) labelheight;
    }

}
