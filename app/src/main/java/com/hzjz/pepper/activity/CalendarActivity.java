package com.hzjz.pepper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzjz.pepper.R;
import com.hzjz.pepper.fragment.CalendarEventsFragment;
import com.hzjz.pepper.fragment.CalendarMonthFragment;
import com.hzjz.pepper.plugins.CheckCalendarPop;
import com.hzjz.pepper.plugins.DateUtil;
import com.hzjz.pepper.plugins.PopCate;
import com.hzjz.pepper.plugins.PopCheckID;
import com.hzjz.pepper.plugins.PopYmdPicker;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalendarActivity extends FragmentActivity implements CalendarMonthFragment.OnFragmentInteractionListener {

    CalendarMonthFragment calendarMonthFragment = new CalendarMonthFragment();
    CalendarEventsFragment calendarEventsFragment = new CalendarEventsFragment();
    private String calendartype = "0", filtertype = "";
    CheckCalendarPop checkCalendarPop;
    PopCate popCate;
    private boolean initiated = false;
    PopYmdPicker popYmdPicker;

    @BindView(R.id.btn_back)
    LinearLayout btnBack;
    @BindView(R.id.btn_today)
    LinearLayout btnToday;
    @BindView(R.id.maintitle)
    TextView maintitle;
    @BindView(R.id.filter)
    ImageButton filter;
    @BindView(R.id.timepick)
    ImageButton timepick;
    @BindView(R.id.viewcate)
    LinearLayout viewcate;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.content_main)
    FrameLayout contentMain;
    @BindView(R.id.container)
    RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        maintitle.setText(DateUtil.getCurrentMonth() + "/" + DateUtil.getCurrentDay() + "/" + DateUtil.getCurrentYear());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_main, calendarMonthFragment)
                .add(R.id.content_main, calendarEventsFragment)
                .show(calendarMonthFragment)
                .hide(calendarEventsFragment)
                .commit();
        //测试提交
    }

    @OnClick({R.id.btn_back, R.id.btn_today, R.id.filter,R.id.timepick, R.id.viewcate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_today:
                if (calendartype.equals("0")) {
                    calendarMonthFragment.refreshMonthPager();
                } else {
                    calendarEventsFragment.toToday();
                }
                break;
            case R.id.filter:
                popCate = new PopCate(this, filtertype);
                popCate.setOnCheckBackListener(new PopCate.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(int result, String content) {
                        if (result == 1) {
                            filtertype = content;
                            Message msg = new Message();
                            msg.what = 2;
                            handler.sendMessage(msg);
                        }
                    }
                });
                popCate.showAtLocation(findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.timepick:
                popYmdPicker = new PopYmdPicker(this);
                popYmdPicker.setOnCheckBackListener(new PopYmdPicker.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(String year, String month, String day) {
                        String datet = month + "/" + day + "/" + year;
                        if (calendartype.equals("0")) {
                            calendarMonthFragment.goSelday(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                        } else {
                            calendarEventsFragment.goSelday(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                        }
                    }
                });
                popYmdPicker.showAtLocation(findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.viewcate:
                checkCalendarPop = new CheckCalendarPop(this, calendartype);
                checkCalendarPop.setOnCheckBackListener(new CheckCalendarPop.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(int result, String content) {
                        if (result == 1) {
                            calendartype = content;
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }
                    }
                });
                checkCalendarPop.showAtLocation(findViewById(R.id.container), Gravity.TOP | Gravity.START, 0, 0);
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    checkCalendarPop.dismiss();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    if (calendartype.equals("0")) {
                        maintitle.setVisibility(View.VISIBLE);
                        transaction
                                .show(calendarMonthFragment)
                                .hide(calendarEventsFragment)
                                .commit();
                    } else {
                        maintitle.setVisibility(View.GONE);
                        transaction
                                .hide(calendarMonthFragment)
                                .show(calendarEventsFragment)
                                .commit();
                        if (calendartype.equals("1")) {
                            calendarEventsFragment.setWeekViewDim(7);
                        } else if (calendartype.equals("2")) {
                            calendarEventsFragment.setWeekViewDim(1);
                        }
                    }
                    break;
                case 2:
                    popCate.dismiss();
                    if (calendartype.equals("0")) {
                        calendarMonthFragment.searchByCate(filtertype);
                    } else {
                        calendarEventsFragment.searchByCate(filtertype);
                    }
                    break;
            }
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && !initiated) {
//            calendarMonthFragment.refreshMonthPager();
//            initiated = true;
//        }
    }

    @Override
    public void onFragmentInteraction(int code, String result) {
        if (code == 1) {
            maintitle.setText(result);
        }
    }
}
