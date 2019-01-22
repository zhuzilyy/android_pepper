package com.hzjz.pepper.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.hzjz.pepper.R;
import com.hzjz.pepper.adapter.CalendarMonthAdapter;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.hzjz.pepper.plugins.CustomDayView;
import com.hzjz.pepper.plugins.DateUtil;
import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarMonthFragment extends Fragment {

    private ArrayList<Calendar> currentCalendars = new ArrayList<>();
    private CalendarViewAdapter calendarAdapter;
    private OnSelectDateListener onSelectDateListener;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    private Context context;
    private boolean initiated = false;
    private OnFragmentInteractionListener mListener;
    private String cdate = "";
    private String seldate = "";
    private String filtertype = "";
    private JSONArray cachelist = new JSONArray();
    private JSONArray ymcachelist = new JSONArray();
    private JSONArray daycachelist = new JSONArray();
    HashMap<String, String> markData = new HashMap<>();
    CalendarMonthAdapter calendarMonthAdapter;
    SearchTask mSearchTesk = new SearchTask();
    Handler mHandler = new Handler();
    int cafirst = 0;
    Timer timer = new Timer();

    @BindView(R.id.calendar_view)
    com.ldf.calendar.view.MonthPager monthPager;
    @BindView(R.id.list)
    RecyclerView rvToDoList;
    @BindView(R.id.content)
    CoordinatorLayout content;
    Unbinder unbinder;

    public CalendarMonthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_month, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();
        monthPager.setViewHeight(Utils.dpi2px(getActivity(), 270));
        calendarMonthAdapter = new CalendarMonthAdapter(context, daycachelist);
        rvToDoList.setHasFixedSize(true);
        rvToDoList.setLayoutManager(new LinearLayoutManager(context));
        rvToDoList.setAdapter(calendarMonthAdapter);
        cdate = DateUtil.getCurrentDate("yyyy-MM");
        seldate = DateUtil.getCurrentDate("yyyy-MM-dd");
        initCalendarView();
        return view;
    }

    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1001;
            handler.sendMessage(message);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Utils.scrollTo(content, rvToDoList, monthPager.getCellHeight(), 200);
        calendarAdapter.switchToWeek(monthPager.getRowIndex());
        if (timer!=null){
            timer.schedule(task, 1000, 1000);
        }
    }

    private void initCalendarView() {
        initListener();
        CustomDayView customDayView = new CustomDayView(context, R.layout.custom_day);
        calendarAdapter = new CalendarViewAdapter(
                context,
                onSelectDateListener,
                CalendarAttr.WeekArrayType.Monday,
                customDayView);
        calendarAdapter.setOnCalendarTypeChangedListener(new CalendarViewAdapter.OnCalendarTypeChanged() {
            @Override
            public void onCalendarTypeChanged(CalendarAttr.CalendarType type) {
                rvToDoList.scrollToPosition(0);
            }
        });
        initMarkData();
        initMonthPager();
        getData();
        getDayData();
    }

    /**
     * 初始化标记数据，HashMap的形式，可自定义
     * 如果存在异步的话，在使用setMarkData之后调用 calendarAdapter.notifyDataChanged();
     */
    private void initMarkData() {
        calendarAdapter.setMarkData(markData);
    }

    private void initListener() {
        onSelectDateListener = new OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate date) {
                refreshClickDate(date);
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                //偏移量 -1表示刷新成上一个月数据 ， 1表示刷新成下一个月数据
                monthPager.selectOtherMonth(offset);
            }
        };
    }
    private void refreshClickDate(CalendarDate date) {
        String seldatec = date.getMonth() + "/" + date.getYear();
        seldate = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
        mListener.onFragmentInteraction(1, seldatec);
        getDayData();
    }
    private void initMonthPager() {
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        monthPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                currentCalendars = calendarAdapter.getPagers();
                if (currentCalendars.get(position % currentCalendars.size()) != null) {
                    CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                    String ctime = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
                    String seldatec = date.getMonth() + "/" + date.getYear();
                    mListener.onFragmentInteraction(1, seldatec);
//                    cdate = DateUtil.getStringByFormatString(ctime, "yyyy-M-d", "yyyy-MM");
//                    mHandler.removeCallbacks(mSearchTesk);
//                    mHandler.postDelayed(mSearchTesk, 1000);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        refreshMonthPager();
    }

    class SearchTask implements Runnable {
        @Override
        public void run() {
            Log.e("---SearchTask---", "Start Search");
            getData();
        }
    }

    public void refreshMonthPager() {
        CalendarDate today = new CalendarDate();
        calendarAdapter.notifyDataChanged(today);
        String seldatec = today.getMonth() + "/" + today.getYear();
        seldate = today.getYear() + "-" + today.getMonth() + "-" + today.getDay();
        mListener.onFragmentInteraction(1, seldatec);
    }

    public void goSelday(int year, int month, int day) {
        CalendarDate sday = new CalendarDate(year, month, day);
        calendarAdapter.notifyDataChanged(sday);
        String seldatec = String.valueOf(month) + "/" + String.valueOf(year);
        seldate = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
        mListener.onFragmentInteraction(1, seldatec);
        getDayData();
    }

    public void searchByCate(String cate) {
        filtertype = cate;
        getDayData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int code, String result);
    }
    private void getData() {
       /* Map<String, String> param = new HashMap<>();
        param.put("month_date", cdate);
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
        });*/
    }

    private void getDayData() {
        Map<String, String> param = new HashMap<>();
        param.put("day_date", seldate);
        param.put("studentStatus", filtertype);
        param.put("user_id", Hawk.get("authid").toString());
        OkHttpUtils.postJsonAsyn(ApiConfig.getMainList(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        daycachelist.clear();
                        daycachelist = JSON.parseArray(resultDesc.getResult());
                        msg.what = 2;
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
                    HashMap<String, String> cachemap = new HashMap<>();
                    for (int i = 0; i < cachelist.size(); i++) {
                        if (!cachelist.getJSONObject(i).getString("nums").equals("0")) {
                            String fmtday = DateUtil.getStringByFormatString(cachelist.getJSONObject(i).getString("day"), "yyyy-MM-dd", "yyyy-M-d");
                            cachemap.put(fmtday, "0");
                        }
                    }
                    if (cachemap.size() > 0) {
                        markData.clear();
                        markData = cachemap;
                       // markData.put("2019-1-18", "1");
                        calendarAdapter.setMarkData(markData);
                        calendarAdapter.notifyDataChanged();
                    }
                    break;
                case 2:
                    calendarMonthAdapter.setNewList(daycachelist);
                    break;
                case -1:
                    Toast.makeText(getActivity(), getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
                    break;
                case 1001:
                    timer.cancel();
                    timer = null;
                    Utils.scrollTo(content, rvToDoList, Utils.dpi2px(getActivity(), 270), 200);
                    calendarAdapter.switchToWeek(monthPager.getRowIndex());
                    break;
            }
        }
    };
}
