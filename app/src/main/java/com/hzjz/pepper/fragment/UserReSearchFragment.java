package com.hzjz.pepper.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hzjz.pepper.R;
import com.hzjz.pepper.adapter.SimpleSelectAdapter;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.hzjz.pepper.plugins.PopMsPicker;
import com.hzjz.pepper.plugins.PopYmdPicker;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.drakeet.materialdialog.MaterialDialog;


public class UserReSearchFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.tv_dateord)
    TextView tvDateord;
    @BindView(R.id.item_btn_dateord)
    RelativeLayout itemBtnDateord;
    @BindView(R.id.tv_stimeord)
    TextView tvStimeord;
    @BindView(R.id.item_btn_stimeord)
    RelativeLayout itemBtnStimeord;
    @BindView(R.id.tv_etimeord)
    TextView tvEtimeord;
    @BindView(R.id.item_btn_etimeord)
    RelativeLayout itemBtnEtimeord;
    private String mParam1;
    private Bundle mParam2;
    private OnFragmentInteractionListener mListener;
    private String title = "", type = "2";
    String stateId = "", stateName = "", districtId = "", districtName = "", districtOrd = "AND", subjectid = "", subjectName = "", subjectOrd = "AND",
            date = "", datemethod = "on", dateOrd = "AND", stime = "", stmethod = "on", stimeOrd = "AND", etime = "", etmethod = "on", etimeOrd = "AND";
    private JSONObject jostr = new JSONObject();
    private int cate = 0;
    private JSONArray statja = null;
    private JSONArray distja = null;
    private JSONArray subja = null;
    private JSONArray timeorder = new JSONArray();
    private String[] orderlist = {"on", "before", "after", "on or before", "on or after"};
    PopYmdPicker popYmdPicker;
    PopMsPicker popMsPicker;
    View thisview;

    MaterialDialog alert;
    ListView listView;
    SimpleSelectAdapter cachelist;

    @BindView(R.id.tv_stat)
    TextView tvStat;
    @BindView(R.id.item_btn_stat)
    RelativeLayout itemBtnStat;
    @BindView(R.id.btn_statc)
    Button btnStatc;
    @BindView(R.id.switch_distswt)
    Switch switchDistswt;
    @BindView(R.id.tv_dist)
    TextView tvDist;
    @BindView(R.id.item_btn_dist)
    RelativeLayout itemBtnDist;
    @BindView(R.id.btn_distc)
    Button btnDistc;
    @BindView(R.id.switch_sub)
    Switch switchSub;
    @BindView(R.id.tv_sub)
    TextView tvSub;
    @BindView(R.id.item_btn_sb)
    RelativeLayout itemBtnSb;
    @BindView(R.id.btn_subc)
    Button btnSubc;
    @BindView(R.id.switch_date)
    Switch switchDate;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.item_btn_date)
    RelativeLayout itemBtnDate;
    @BindView(R.id.btn_datec)
    Button btnDatec;
    @BindView(R.id.switch_stime)
    Switch switchStime;
    @BindView(R.id.tv_stime)
    TextView tvStime;
    @BindView(R.id.item_btn_stime)
    RelativeLayout itemBtnStime;
    @BindView(R.id.btn_stimec)
    Button btnStimec;
    @BindView(R.id.switch_etime)
    Switch switchEtime;
    @BindView(R.id.tv_etime)
    TextView tvEtime;
    @BindView(R.id.item_btn_etime)
    RelativeLayout itemBtnEtime;
    @BindView(R.id.btn_etimec)
    Button btnEtimec;
    @BindView(R.id.scrollmain)
    LinearLayout scrollmain;
    @BindView(R.id.seach_scroll)
    ScrollView seachScroll;
    @BindView(R.id.btn_reset)
    Button btnReset;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.btn_panel)
    LinearLayout btnPanel;
    Unbinder unbinder;
    private String selectHour,selectMinute,selectEndHour,selectEndMinute;
    private  PopMsPicker popMsPickerStarTime,popMsPickerEndTime;
    public static UserReSearchFragment newInstance(String param1, Bundle param2) {
        UserReSearchFragment fragment = new UserReSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putBundle(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getBundle(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_re_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        this.thisview = view;
        initDate();
        setmswtListener();
        for (int i = 0; i < orderlist.length; i++) {
            JSONObject jo = new JSONObject();
            jo.put("name", orderlist[i]);
            timeorder.add(jo);
        }
        cachelist = new SimpleSelectAdapter(getActivity(), new JSONArray(), 0);
        listView = new ListView(getActivity());
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (8 * scale + 0.5f);
        listView.setPadding(0, dpAsPixels, 0, dpAsPixels);
        listView.setDividerHeight(0);
        listView.setAdapter(cachelist);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (cate == 0) {
                    jostr.put("stateId", statja.getJSONObject(i).getString("id"));
                    jostr.put("stateName", statja.getJSONObject(i).getString("name"));
                    tvStat.setText(statja.getJSONObject(i).getString("name"));
                } else if (cate == 1) {
                    jostr.put("districtId", distja.getJSONObject(i).getString("id"));
                    jostr.put("districtName", distja.getJSONObject(i).getString("name"));
                    tvDist.setText(distja.getJSONObject(i).getString("name"));
                } else if (cate == 2) {
                    jostr.put("subjectid", subja.getJSONObject(i).getString("value"));
                    jostr.put("subjectName", subja.getJSONObject(i).getString("name"));
                    tvSub.setText(subja.getJSONObject(i).getString("name"));
                } else if (cate == 4) {
                    jostr.put("datemethod", timeorder.getJSONObject(i).getString("name"));
                    tvDateord.setText(timeorder.getJSONObject(i).getString("name"));
                } else if (cate == 5) {
                    jostr.put("stmethod", timeorder.getJSONObject(i).getString("name"));
                    tvStimeord.setText(timeorder.getJSONObject(i).getString("name"));
                } else if (cate == 6) {
                    jostr.put("etmethod", timeorder.getJSONObject(i).getString("name"));
                    tvEtimeord.setText(timeorder.getJSONObject(i).getString("name"));
                }
                alert.dismiss();
            }
        });
        alert = new MaterialDialog(getActivity()).setTitle(title).setContentView(listView).setCanceledOnTouchOutside(true);
        alert.setPositiveButton(getResources().getString(R.string.Close), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        popMsPickerStarTime = new PopMsPicker(getActivity());
        popMsPickerEndTime = new PopMsPicker(getActivity());
        popYmdPicker = new PopYmdPicker(getActivity());
        if (mParam1.equals("2")) {
            setInitData();
        }
        return view;
    }

    private void setInitData() {
        type = mParam2.getString("type");
        jostr = JSON.parseObject(mParam2.getString("data"));

        tvStat.setText(jostr.getString("stateName"));
        tvDist.setText(jostr.getString("districtName"));
        if (jostr.getString("districtOrd").equals("OR")) {
            switchDistswt.setChecked(true);
        } else {
            switchDistswt.setChecked(false);
        }
        tvSub.setText(jostr.getString("subjectName"));
        if (jostr.getString("subjectOrd").equals("OR")) {
            switchSub.setChecked(true);
        } else {
            switchSub.setChecked(false);
        }
        tvDate.setText(jostr.getString("date"));
        String[] dates = jostr.getString("date").split("-");
        if (dates.length!=0){
            popYmdPicker.setCurrentYear(dates[0]);
            popYmdPicker.setSelectMonth(dates[1]);
            popYmdPicker.setSelectDay(dates[2]);
            popYmdPicker.setSelectData();
        }
        tvDateord.setText(jostr.getString("datemethod"));
        if (jostr.getString("dateOrd").equals("OR")) {
            switchDate.setChecked(true);
        } else {
            switchDate.setChecked(false);
        }
        tvStime.setText(jostr.getString("stime"));
        tvStimeord.setText(jostr.getString("stmethod"));
        if (jostr.getString("stimeOrd").equals("OR")) {
            switchStime.setChecked(true);
        } else {
            switchStime.setChecked(false);
        }
        tvEtime.setText(jostr.getString("etime"));
        tvEtimeord.setText(jostr.getString("etmethod"));
        if (jostr.getString("etimeOrd").equals("OR")) {
            switchEtime.setChecked(true);
        } else {
            switchEtime.setChecked(false);
        }
    }

    private void initDate() {
        jostr.put("stateId", "");
        jostr.put("stateName", "");
        jostr.put("districtId", "");
        jostr.put("districtName", "");
        jostr.put("districtOrd", "AND");
        jostr.put("subjectid", "");
        jostr.put("subjectName", "");
        jostr.put("subjectOrd", "AND");
        jostr.put("date", "");
        jostr.put("datemethod", "on");
        jostr.put("dateOrd", "AND");
        jostr.put("stime", "");
        jostr.put("stmethod", "on");
        jostr.put("stimeOrd", "AND");
        jostr.put("etime", "");
        jostr.put("etmethod", "on");
        jostr.put("etimeOrd", "AND");
    }

    private void setmswtListener() {
        switchDistswt.setOnCheckedChangeListener(this);
        switchSub.setOnCheckedChangeListener(this);
        switchDate.setOnCheckedChangeListener(this);
        switchStime.setOnCheckedChangeListener(this);
        switchEtime.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_distswt:
                if (isChecked) {
                    jostr.put("districtOrd", "OR");
                } else {
                    jostr.put("districtOrd", "AND");
                }
                break;
            case R.id.switch_sub:
                if (isChecked) {
                    jostr.put("subjectOrd", "OR");
                } else {
                    jostr.put("subjectOrd", "AND");
                }
                break;
            case R.id.switch_date:
                if (isChecked) {
                    jostr.put("dateOrd", "OR");
                } else {
                    jostr.put("dateOrd", "AND");
                }
                break;
            case R.id.switch_stime:
                if (isChecked) {
                    jostr.put("stimeOrd", "OR");
                } else {
                    jostr.put("stimeOrd", "AND");
                }
                break;
            case R.id.switch_etime:
                if (isChecked) {
                    jostr.put("etimeOrd", "OR");
                } else {
                    jostr.put("etimeOrd", "AND");
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_reset, R.id.btn_search, R.id.item_btn_stat, R.id.btn_statc, R.id.item_btn_dist,
            R.id.btn_distc, R.id.item_btn_sb, R.id.btn_subc, R.id.item_btn_date, R.id.btn_datec, R.id.item_btn_stime,
            R.id.btn_stimec, R.id.item_btn_etime, R.id.btn_etimec, R.id.item_btn_dateord, R.id.item_btn_stimeord, R.id.item_btn_etimeord})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_reset:
                jostr.put("stateId", "");
                jostr.put("stateName", "");
                jostr.put("districtId", "");
                jostr.put("districtName", "");
                jostr.put("districtOrd", "AND");
                jostr.put("subjectid", "");
                jostr.put("subjectName", "");
                jostr.put("subjectOrd", "AND");
                jostr.put("date", "");
                jostr.put("datemethod", "on");
                jostr.put("dateOrd", "AND");
                jostr.put("stime", "");
                jostr.put("stmethod", "on");
                jostr.put("stimeOrd", "AND");
                jostr.put("etime", "");
                jostr.put("etmethod", "on");
                jostr.put("etimeOrd", "AND");

                tvStat.setText("");
                tvDist.setText("");
                switchDistswt.setChecked(false);
                tvSub.setText("");
                switchSub.setChecked(false);
                tvDate.setText("");
                tvDateord.setText("on");
                switchDate.setChecked(false);
                tvStime.setText("");
                tvStimeord.setText("on");
                switchStime.setChecked(false);
                tvEtime.setText("");
                tvEtimeord.setText("on");
                switchEtime.setChecked(false);
                break;
            case R.id.btn_search:
                JSONObject jo = new JSONObject();
                jo.put("type", type);
                jo.put("data", jostr);
                mListener.onFragmentInteraction(2, jo.toString());
                break;
            case R.id.item_btn_stat:
                cate = 0;
                title = getResources().getString(R.string.search_state);
                if (statja != null) {
                    cachelist.setlist(statja, cate);
                    listView.setAdapter(cachelist);
                    alert.setTitle(title);
                    alert.show();
                } else {
                    getData();
                }
                break;
            case R.id.btn_statc:
                jostr.put("stateId", "");
                jostr.put("stateName", "");
                tvStat.setText("");
                break;
            case R.id.item_btn_dist:
                cate = 1;
                title = getResources().getString(R.string.search_district);
                if (distja != null) {
                    cachelist.setlist(distja, cate);
                    listView.setAdapter(cachelist);
                    alert.setTitle(title);
                    alert.show();
                } else {
                    getData();
                }
                break;
            case R.id.btn_distc:
                jostr.put("districtId", "");
                jostr.put("districtName", "");
                jostr.put("districtOrd", "AND");
                switchDistswt.setChecked(false);
                tvDist.setText("");
                break;
            case R.id.item_btn_sb:
                cate = 2;
                title = getResources().getString(R.string.search_subject);
                if (subja != null) {
                    cachelist.setlist(subja, cate);
                    listView.setAdapter(cachelist);
                    alert.setTitle(title);
                    alert.show();
                } else {
                    getData();
                }
                break;
            case R.id.btn_subc:
                jostr.put("subjectid", "");
                jostr.put("subjectName", "");
                jostr.put("subjectOrd", "AND");
                switchSub.setChecked(false);
                tvSub.setText("");
                break;
            case R.id.item_btn_date:
                //设置默认显示的数据
                popYmdPicker.setOnCheckBackListener(new PopYmdPicker.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(String year, String month, String day) {
                        String datet = month + "/" + day + "/" + year;
                        jostr.put("date", year + "-" + month + "-" + day);
                        popYmdPicker.setSelectYear(year);
                        popYmdPicker.setSelectMonth(month);
                        popYmdPicker.setSelectDay(day);
                        tvDate.setText(datet);
                    }
                });
                popYmdPicker.showAtLocation(thisview.findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_datec:
                jostr.put("date", "");
                jostr.put("dateOrd", "AND");
                jostr.put("datemethod", "on");
                tvDate.setText("");
                tvDateord.setText("on");
                switchDate.setChecked(false);
                break;
            case R.id.item_btn_stime:
                popMsPickerStarTime.setOnCheckBackListener(new PopMsPicker.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(String hour, String second) {
                        String datet = hour + ":" + second;
                        if (!etime.equals("")) {
                            if (Integer.parseInt(datet.replace(":", "")) >= Integer.parseInt(etime.replace(":", ""))) {
                                Message msg = new Message();
                                msg.what = 2;
                                handler.sendMessage(msg);
                            } else {
                                stime = datet;
                                jostr.put("stime", stime);
                                tvStime.setText(datet);
                            }
                        } else {
                            selectHour = hour;
                            selectMinute = second;
                            if (!TextUtils.isEmpty(selectHour)){
                                popMsPickerStarTime.setSelectHour(selectHour);
                            }
                            if (!TextUtils.isEmpty(selectMinute)){
                                popMsPickerStarTime.setSelectMinute(selectMinute);
                            }
                            stime = datet;
                            jostr.put("stime", stime);
                            tvStime.setText(datet);
                        }
                    }
                });
                popMsPickerStarTime.showAtLocation(thisview.findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_stimec:
                jostr.put("stime", "");
                jostr.put("stimeOrd", "AND");
                jostr.put("stmethod", "on");
                tvStime.setText("");
                tvStimeord.setText("on");
                switchStime.setChecked(false);
                break;
            case R.id.item_btn_etime:
                popMsPickerEndTime.setOnCheckBackListener(new PopMsPicker.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(String hour, String second) {
                        String datet = hour + ":" + second;
                        if (!stime.equals("")) {
                            if (Integer.parseInt(stime.replace(":", "")) >= Integer.parseInt(datet.replace(":", ""))) {
                                Message msg = new Message();
                                msg.what = 3;
                                handler.sendMessage(msg);
                            } else {
                                etime = datet;
                                jostr.put("etime", etime);
                                tvEtime.setText(datet);
                            }
                        } else {
                            selectEndHour = hour;
                            selectEndMinute = second;
                            if (!TextUtils.isEmpty(selectEndHour)){
                                popMsPickerEndTime.setSelectHour(selectEndHour);
                            }
                            if (!TextUtils.isEmpty(selectEndMinute)){
                                popMsPickerEndTime.setSelectMinute(selectEndMinute);
                            }
                            etime = datet;
                            jostr.put("etime", etime);
                            tvEtime.setText(datet);
                        }
                    }
                });
                popMsPickerEndTime.showAtLocation(thisview.findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_etimec:
                jostr.put("etime", "");
                jostr.put("etimeOrd", "AND");
                jostr.put("etmethod", "on");
                tvEtime.setText("");
                tvEtimeord.setText("on");
                switchEtime.setChecked(false);
                break;
            case R.id.item_btn_dateord:
                cate = 4;
                title = getResources().getString(R.string.search_date);
                cachelist.setlist(timeorder, cate);
                listView.setAdapter(cachelist);
                alert.setTitle(title);
                alert.show();
                break;
            case R.id.item_btn_stimeord:
                cate = 5;
                title = getResources().getString(R.string.search_stime);
                cachelist.setlist(timeorder, cate);
                listView.setAdapter(cachelist);
                alert.setTitle(title);
                alert.show();
                break;
            case R.id.item_btn_etimeord:
                cate = 6;
                title = getResources().getString(R.string.search_etime);
                cachelist.setlist(timeorder, cate);
                listView.setAdapter(cachelist);
                alert.setTitle(title);
                alert.show();
                break;
        }
    }

    private void getData() {
        Map<String, String> param = new HashMap<>();
        String url = "";
        switch (cate) {
            case 0:
                param.put("stateId", "");
                url = ApiConfig.getSearchState();
                break;
            case 1:
                param.put("stateId", "");
                url = ApiConfig.getSearchDist();
                break;
            case 2:
                param.put("user_id", Hawk.get("authid").toString());
                url = ApiConfig.getSearchSubject();
                break;
        }
        OkHttpUtils.postJsonAsyn(url, param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        switch (cate) {
                            case 0:
                                title = getResources().getString(R.string.search_state);
                                statja = JSON.parseArray(resultDesc.getResult());
                                cachelist.setlist(statja, cate);
                                break;
                            case 1:
                                title = getResources().getString(R.string.search_district);
                                distja = JSON.parseArray(resultDesc.getResult());
                                cachelist.setlist(distja, cate);
                                break;
                            case 2:
                                title = getResources().getString(R.string.search_subject);
                                subja = JSON.parseArray(resultDesc.getResult());
                                cachelist.setlist(subja, cate);
                                break;
                        }
                        listView.setAdapter(cachelist);
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        msg.what = 2;
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
                    alert.setTitle(title);
                    alert.show();
                    break;
                case 2:
                    Toast.makeText(getActivity(), getResources().getString(R.string.stimelittlethanetime), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getActivity(), getResources().getString(R.string.stimebigthanetime), Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(getActivity(), getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
}
