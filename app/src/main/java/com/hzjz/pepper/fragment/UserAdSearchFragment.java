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
import android.widget.ListView;
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

public class UserAdSearchFragment extends Fragment {
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

    @butterknife.BindView(R.id.t_subj)
    android.widget.TextView tSubj;
    @butterknife.BindView(R.id.s_subj)
    android.widget.RelativeLayout sSubj;
    @butterknife.BindView(R.id.t_pepc)
    android.widget.TextView tPepc;
    @butterknife.BindView(R.id.s_pepc)
    android.widget.RelativeLayout sPepc;
    @butterknife.BindView(R.id.t_date)
    android.widget.TextView tDate;
    @butterknife.BindView(R.id.s_date)
    android.widget.RelativeLayout sDate;
    @butterknife.BindView(R.id.btn_reset)
    android.widget.Button btnReset;
    @butterknife.BindView(R.id.btn_search)
    android.widget.Button btnSearch;
    butterknife.Unbinder unbinder;
    private View view_alertListView;
    private MyListView listView;
    private TextView tv_noData;

    public static UserAdSearchFragment newInstance(String param1, Bundle param2) {
        UserAdSearchFragment fragment = new UserAdSearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_ad_search, container, false);
        this.thisview = view;
        unbinder = butterknife.ButterKnife.bind(this, view);
        if (mParam1.equals("0")) {
            setInitData();
        }
        simpleSelectAdapter = new SimpleSelectAdapter(getActivity(), new JSONArray(), 0);
        view_alertListView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_alert_listview,null);
        listView = view_alertListView.findViewById(R.id.listview);
        tv_noData = view_alertListView.findViewById(R.id.tv_noData);
       /* listView = new ListView(getActivity());
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (8 * scale + 0.5f);
        listView.setPadding(0, dpAsPixels, 0, dpAsPixels);
        listView.setDividerHeight(0);*/
        listView.setAdapter(simpleSelectAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (cate) {
                    case 0:
                        break;
                    case 1:
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
        popYmdPicker = new PopYmdPicker(getActivity());
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
        if (TextUtils.isEmpty(subjectname)){
            tSubj.setText("Select subject");
        }else{
            tSubj.setText(subjectname);
        }
        if (TextUtils.isEmpty(pepperCoursename)){
            tPepc.setText("Select Pepper Courses");
        }else{
            tPepc.setText(pepperCoursename);
        }
        if (TextUtils.isEmpty(trainingDate)){
            tDate.setText("Select date");
        }else{
            tDate.setText(trainingDate);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @butterknife.OnClick({R.id.s_subj, R.id.s_pepc, R.id.s_date, R.id.btn_reset, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
                mListener.onFragmentInteraction(2, jo.toString());
                break;
        }
    }

    private void getData() {
        String authid = Hawk.get("authid");
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
                param.put("user_id",authid);
                url = ApiConfig.getSearchSubject();
                break;
            case 3:
                param.put("user_id",authid);
                url = ApiConfig.getSearchCourses();
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
                Toast.makeText(getActivity(), getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == -1){
                Toast.makeText(getActivity(), getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void resetall() {
        tSubj.setText("Select subject");
        tPepc.setText("Select Pepper Courses");
        tDate.setText("Select date");
        //type = "";
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
