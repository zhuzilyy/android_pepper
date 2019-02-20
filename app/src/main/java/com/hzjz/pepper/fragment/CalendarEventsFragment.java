package com.hzjz.pepper.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hzjz.pepper.R;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.hzjz.pepper.plugins.DateUtil;
import com.hzjz.pepper.plugins.PopConfirmWindow;
import com.hzjz.pepper.plugins.TimeTransferUtil;
import com.orhanobut.hawk.Hawk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarEventsFragment extends Fragment implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private Context mContext;
    private List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
    boolean calledNetwork = false;
    private String seldate = "";
    private String filtertype = "";
    private JSONArray cachelist = new JSONArray();
    private JSONArray timelist = new JSONArray();
    private Map<String, JSONObject> dataset = new HashMap<>();
    PopConfirmWindow PopConfirm;

    @BindView(R.id.weekView)
    WeekView mWeekView;
    @BindView(R.id.container)
    RelativeLayout container;
    Unbinder unbinder;

    public CalendarEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_events, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getActivity();
        seldate = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.setEmptyViewLongPressListener(this);
        mWeekView.setNumberOfVisibleDays(7);
//        mWeekView.setVerticalFlingEnabled(false);
//        mWeekView.setHorizontalFlingEnabled(false);
//        mWeekView.setShowNowLine(true);
//        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
//        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
//        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        setupDateTimeInterpreter(false);
        return view;
    }

    public void setWeekViewDim(int dim) {
        if (dim == 1) {
            mWeekView.setNumberOfVisibleDays(1);
            mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
            mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        } else {
            mWeekView.setNumberOfVisibleDays(7);
            mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
            mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
            mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        }
    }

    public void toToday() {
        seldate = DateUtil.getCurrentDate("yyyy-MM");
        mWeekView.goToToday();
//        Calendar startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 1);
//        startTime.set(Calendar.MONTH, DateUtil.getCurrentMonth() - 1);
//        startTime.set(Calendar.YEAR, DateUtil.getCurrentYear());
//        mWeekView.goToDate(startTime);
    }
    public void goSelday(int year, int month, int day) {
        String stime = String.valueOf(year) + "-" + String.valueOf(month);
        if (!stime.equals(seldate)) {
            seldate = String.valueOf(year) + "-" + String.valueOf(month);
        }
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        mWeekView.goToDate(date);
    }

    public void searchByCate(String cate) {
        filtertype = cate;
        events.clear();
        getData();
    }
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDay(Calendar date) {
                SimpleDateFormat format = new SimpleDateFormat("dd", Locale.ENGLISH);
                return format.format(date.getTime());
            }

            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" MM", Locale.ENGLISH);

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour == 12 ? "12 PM" : (hour - 12) + " PM") : (hour == 0 ? "0 AM" : hour + " AM");
//                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
//        JSONObject jo = JSON.parseObject(event.getName());
        JSONObject jo = dataset.get(String.valueOf(event.getId()));
        String content = jo.getString("trainingTimeStart") + "-" + jo.getString("trainingTimeEnd") + "\n"
                + jo.getString("stateName");
        PopConfirm = new PopConfirmWindow(getActivity(), jo.getString("name"), content, null);
        PopConfirm.showAtLocation(container, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
//        Toast.makeText(mContext, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
//        Toast.makeText(mContext, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
//        Toast.makeText(mContext, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        String stamp = String.valueOf(newYear) + String.valueOf(newMonth);
        if (!timelist.contains(stamp)) {
            timelist.add(stamp);
            getData();
//            new Handler().postDelayed(new Runnable(){
//                public void run() {
//                    buildEvents();
//                }
//            }, 2000);
        }

        List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent aaevent : events) {
            if (eventMatches(aaevent, newYear, newMonth)) {
                matchedEvents.add(aaevent);
            }
        }
        return matchedEvents;
    }

    private void getData() {
        String weekDay="";
        Map<String, String> param = new HashMap<>();
        try {
            Date date = TimeTransferUtil.stringToDate(seldate, "yyyy-MM-dd HH:mm:ss");
            weekDay = TimeTransferUtil.convertWeekByDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        param.put("week_date", weekDay);
        param.put("studentStatus", filtertype);
        param.put("user_id", Hawk.get("authid").toString());
        DialogUtil.showDialogLoading(getActivity(),"loading");
        OkHttpUtils.postJsonAsyn(ApiConfig.getMainList(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        cachelist.clear();
                        cachelist = JSON.parseArray(resultDesc.getResult());
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                } else {
                    msg.what = -1;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    buildEvents();
                    break;
                case -1:
                    Toast.makeText(getActivity(), getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    private void buildEvents() {
        this.events.clear();
        for (int i = 0; i < cachelist.size(); i++) {
            dataset.put(cachelist.getJSONObject(i).getString("id"), cachelist.getJSONObject(i));
            String cy = cachelist.getJSONObject(i).getString("trainingDate");
            String sh = cachelist.getJSONObject(i).getString("trainingTimeStart");
            String eh = cachelist.getJSONObject(i).getString("trainingTimeEnd");
            String name = cachelist.getJSONObject(i).getString("name");
            int year = Integer.parseInt(cy.substring(0, cy.indexOf("-")));
            int month = Integer.parseInt(cy.substring(cy.indexOf("-") + 1, cy.indexOf("-") + 3));
            int day = Integer.parseInt(cy.substring(cy.indexOf("-") + 4, cy.indexOf("-") + 6));
            int shour = Integer.parseInt(sh.substring(0, sh.indexOf(":")));
            int sminute = Integer.parseInt(sh.substring(sh.indexOf(":") + 1, sh.indexOf(":") + 3));
            int ehour = Integer.parseInt(eh.substring(0, eh.indexOf(":")));
            int eminute = Integer.parseInt(eh.substring(eh.indexOf(":") + 1, eh.indexOf(":") + 3));
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, shour);
            startTime.set(Calendar.MINUTE, sminute);
            startTime.set(Calendar.DAY_OF_MONTH, day);
            startTime.set(Calendar.MONTH, month - 1);
            startTime.set(Calendar.YEAR, year);
            Calendar endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, ehour);
            endTime.set(Calendar.MINUTE, eminute);
            int cid = Integer.parseInt(cachelist.getJSONObject(i).getString("id"));
            WeekViewEvent event = new WeekViewEvent(cid, name, startTime, endTime);
            event.setColor(getResources().getColor(judgeColor(cachelist.getJSONObject(i).getString("studentStatus"))));
            this.events.add(event);
        }

//        Calendar startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.DAY_OF_MONTH, 7);
//        startTime.set(Calendar.MONTH, 5);
//        startTime.set(Calendar.YEAR, 2018);
//        Calendar endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.HOUR_OF_DAY, 23);
//        endTime.set(Calendar.MINUTE, 0);
//        WeekViewEvent event = new WeekViewEvent(1, "aa", startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_01));
//        this.events.add(event);

        mWeekView.notifyDatasetChanged();
    }

    protected int judgeColor(String stat) {
        int color = R.color.event_color_01;
        switch (stat) {
            case "Registered":
                color = R.color.event_color_02;
                break;
            case "Attended":
                color = R.color.event_color_01;
                break;
            case "I can reglster":
                color = R.color.event_color_03;
                break;
            case "Training not yet open":
                color = R.color.event_color_04;
                break;
            case "No More Spots":
                color = R.color.event_color_05;
                break;
        }
        return color;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
