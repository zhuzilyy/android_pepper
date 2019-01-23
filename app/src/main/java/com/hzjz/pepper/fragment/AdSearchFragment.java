package com.hzjz.pepper.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.hzjz.pepper.plugins.PopYmdPicker;
import com.hzjz.pepper.view.MyListView;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdSearchFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private Bundle mParam2;
    private String stateid = "", statename = "", districtId = "", districtname = "", subjectid = "", subjectname = "", pepperCourseid = "", pepperCoursename = "", trainingDate = "", type = "0";
    String title = "";
    private JSONArray[] jsonArrays = new JSONArray[4];
    private OnFragmentInteractionListener mListener;
    private int cate = 0;
    MaterialDialog alert;
    //ListView listView;
    SimpleSelectAdapter simpleSelectAdapter;
    PopYmdPicker popYmdPicker;
    View thisview;
    JSONObject jo;

    @butterknife.BindView(R.id.t_state)
    TextView tState;
    @butterknife.BindView(R.id.s_state)
    RelativeLayout sState;
    @butterknife.BindView(R.id.t_dist)
    TextView tDist;
    @butterknife.BindView(R.id.s_dist)
    RelativeLayout sDist;
    @butterknife.BindView(R.id.t_subj)
    TextView tSubj;
    @butterknife.BindView(R.id.s_subj)
    RelativeLayout sSubj;
    @butterknife.BindView(R.id.t_pepc)
    TextView tPepc;
    @butterknife.BindView(R.id.s_pepc)
    RelativeLayout sPepc;
    @butterknife.BindView(R.id.t_date)
    TextView tDate;
    @butterknife.BindView(R.id.s_date)
    RelativeLayout sDate;
    @butterknife.BindView(R.id.btn_reset)
    Button btnReset;
    @butterknife.BindView(R.id.btn_search)
    Button btnSearch;
    butterknife.Unbinder unbinder;
    private View view_alertListView;
    private MyListView listView;
    private TextView tv_noData;
    public static AdSearchFragment newInstance(String param1, Bundle param2) {
        AdSearchFragment fragment = new AdSearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_ad_search, container, false);
        this.thisview = view;
        unbinder = butterknife.ButterKnife.bind(this, view);
        popYmdPicker = new PopYmdPicker(getActivity());
        if (mParam1.equals("1")) {
            setInitData();
        }

        simpleSelectAdapter = new SimpleSelectAdapter(getActivity(), new JSONArray(), 0);
      /*  listView = new ListView(getActivity());
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (8 * scale + 0.5f);
        listView.setPadding(0, dpAsPixels, 0, dpAsPixels);
        listView.setDividerHeight(0);*/
        view_alertListView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_alert_listview,null);
        listView = view_alertListView.findViewById(R.id.listview);
        tv_noData = view_alertListView.findViewById(R.id.tv_noData);
        listView.setAdapter(simpleSelectAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                type = "1";
                switch (cate) {
                    case 0:
                        //点击切换state的时候 地区和学校数据要发生变化
                        String newStateName = jsonArrays[0].getJSONObject(i).getString("name");
                        if (!TextUtils.isEmpty(newStateName) && !TextUtils.isEmpty(statename)){
                            if (!newStateName.equals(statename)){
                                districtId = "";
                                districtname ="";
                                tDist.setText(districtname);
                            }
                        }
                        stateid = jsonArrays[0].getJSONObject(i).getString("id");
                        statename = jsonArrays[0].getJSONObject(i).getString("name");
                        tState.setText(jsonArrays[0].getJSONObject(i).getString("name"));
                        break;
                    case 1:
                        districtId = jsonArrays[1].getJSONObject(i).getString("id");
                        districtname = jsonArrays[1].getJSONObject(i).getString("name");
                        tDist.setText(jsonArrays[1].getJSONObject(i).getString("name"));
                        break;
                    case 2:
                        subjectid = jsonArrays[2].getJSONObject(i).getString("value");
                        subjectname = jsonArrays[2].getJSONObject(i).getString("name");
                        tSubj.setText(jsonArrays[2].getJSONObject(i).getString("name"));
                        break;
                    case 3:
                        pepperCourseid = jsonArrays[3].getJSONObject(i).getString("course_id");
                        pepperCoursename = jsonArrays[3].getJSONObject(i).getString("display_name");
                        tPepc.setText(jsonArrays[3].getJSONObject(i).getString("display_name"));
                        break;
                }
                alert.dismiss();
            }
        });
        alert = new MaterialDialog(getActivity()).setTitle(title).setContentView(view_alertListView).setCanceledOnTouchOutside(true);
        alert.setPositiveButton(getResources().getString(R.string.Close), new View.OnClickListener() {
            @Override public void onClick(View v) {
                alert.dismiss();
            }
        });
        return view;
    }

    private void setInitData() {
        type = mParam2.getString("type");
        stateid = mParam2.getString("stateid");
        statename = mParam2.getString("statename");
        districtId = mParam2.getString("districtId");
        districtname = mParam2.getString("districtname");
        subjectid = mParam2.getString("subjectid");
        subjectname = mParam2.getString("subjectname");
        pepperCourseid = mParam2.getString("pepperCourseid");
        pepperCoursename = mParam2.getString("pepperCoursename");
        trainingDate = mParam2.getString("trainingDate");
        //设置时间的默认选项
        String[] dates = trainingDate.split("-");
        if (dates.length == 3){
            popYmdPicker.setCurrentYear(dates[0]);
            popYmdPicker.setSelectMonth(dates[1]);
            popYmdPicker.setSelectDay(dates[2]);
            popYmdPicker.setSelectData();
        }
        tState.setText(statename);
        tDist.setText(districtname);
        tSubj.setText(subjectname);
        tPepc.setText(pepperCoursename);
        tDate.setText(trainingDate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @butterknife.OnClick({R.id.s_state, R.id.s_dist, R.id.s_subj, R.id.s_pepc, R.id.s_date, R.id.btn_reset, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.s_state:
                cate = 0;
                title = getResources().getString(R.string.search_state);
                if (jsonArrays[cate] != null) {
                    simpleSelectAdapter.setlist(jsonArrays[cate], cate);
                    listView.setAdapter(simpleSelectAdapter);
                    alert.setTitle(title);
                    alert.show();
                } else {
                    getData();
                }
                break;
            case R.id.s_dist:
                //先选择州
                if (TextUtils.isEmpty(stateid)){
                    Toast.makeText(getActivity(), R.string.selectStateFirst,Toast.LENGTH_LONG).show();
                    return;
                }
                cate = 1;
                title = getResources().getString(R.string.search_district);
                getData();
                break;
            case R.id.s_subj:
                cate = 2;
                title = getResources().getString(R.string.search_subject);
                if (jsonArrays[cate] != null) {
                    simpleSelectAdapter.setlist(jsonArrays[cate], cate);
                    listView.setAdapter(simpleSelectAdapter);
                    alert.setTitle(title);
                    alert.show();
                } else {
                    getData();
                }
                break;
            case R.id.s_pepc:
                cate = 3;
                title = getResources().getString(R.string.search_peppercourses);
                if (jsonArrays[cate] != null) {
                    simpleSelectAdapter.setlist(jsonArrays[cate], cate);
                    listView.setAdapter(simpleSelectAdapter);
                    alert.setTitle(title);
                    alert.show();
                } else {
                    getData();
                }
                break;
            case R.id.s_date:
                popYmdPicker.setOnCheckBackListener(new PopYmdPicker.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(String year, String month, String day) {
                        type = "1";
//                        Toast.makeText(getActivity(), year + month + day, Toast.LENGTH_SHORT).show();
                        String datet = month + "/" + day + "/" + year;
                        trainingDate = year + "-" + month + "-" + day;
                        popYmdPicker.setSelectYear(year);
                        popYmdPicker.setSelectMonth(month);
                        popYmdPicker.setSelectDay(day);
                        tDate.setText(datet);
                    }
                });
                popYmdPicker.showAtLocation(thisview.findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_reset:
                resetall();
                break;
            case R.id.btn_search:
                JSONObject jo = new JSONObject();
                jo.put("type", type);
                jo.put("stateid", stateid);
                jo.put("statename", statename);
                jo.put("districtId", districtId);
                jo.put("districtname", districtname);
                jo.put("subjectid", subjectid);
                jo.put("subjectname", subjectname);
                jo.put("pepperCourseid", pepperCourseid);
                jo.put("pepperCoursename", pepperCoursename);
                jo.put("trainingDate", trainingDate);
                mListener.onFragmentInteraction(1, jo.toString());
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
                param.put("stateId", stateid);
                url = ApiConfig.getSearchDist();
                break;
            case 2:
                param.put("user_id", Hawk.get("authid").toString());
                url = ApiConfig.getSearchSubject();
                break;
            case 3:
                param.put("user_id", Hawk.get("authid").toString());
                url = ApiConfig.getSearchCourses();
                break;
        }
        DialogUtil.showDialogLoading(getActivity(),"loading");
        OkHttpUtils.postJsonAsyn(url, param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        jsonArrays[cate] = JSON.parseArray(resultDesc.getResult());
                        simpleSelectAdapter.setlist(jsonArrays[cate], cate);
                        listView.setAdapter(simpleSelectAdapter);
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } else {
                    msg.what = 2;
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
            if (msg.what == 1) {
                if (jsonArrays[cate].size()==0){
                    listView.setVisibility(View.GONE);
                    tv_noData.setVisibility(View.VISIBLE);
                }else{
                    listView.setVisibility(View.VISIBLE);
                    tv_noData.setVisibility(View.GONE);
                }
                alert.setTitle(title);
                alert.show();
            } else if (msg.what == 2) {
                Toast.makeText(getActivity(), getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
            } else if (msg.what == -1){
                Toast.makeText(getActivity(), getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void resetall() {
        tState.setText("");
        tDist.setText("");
        tSubj.setText("");
        tPepc.setText("");
        tDate.setText("");

        type = "0";
        stateid = "";
        statename = "";
        districtId = "";
        districtname = "";
        subjectid = "";
        subjectname = "";
        pepperCourseid = "";
        pepperCoursename = "";
        trainingDate = "";
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
}
