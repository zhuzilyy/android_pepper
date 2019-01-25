package com.hzjz.pepper.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.hzjz.pepper.http.utils.ToastUtil;
import com.hzjz.pepper.view.MyListView;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.drakeet.materialdialog.MaterialDialog;

public class CTPager2Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String mParam1;
    private static JSONObject mParam2;
    private OnFragmentInteractionListener mListener;

    private JSONObject mjo = new JSONObject();
    private String traincate = "pepper_course";
    private String title = "";
    private int cate = 0;
    MaterialDialog alert;
    ListView listView;
    SimpleSelectAdapter simpleSelectAdapter;
    private JSONArray[] jsonArrays = new JSONArray[5];
    private String stateId = "", statename = "", districtId = "", districtname = "", subjectid = "", subjectname = "", pepperCourseid = "", pepperCoursename = "", schoolid = "", schoolname = "";

    @BindView(R.id.btn_prevstop)
    ImageButton btnPrevstop;
    @BindView(R.id.btn_nextstop)
    ImageButton btnNextstop;
    @BindView(R.id.sel_state_t)
    TextView selStateT;
    @BindView(R.id.sel_state)
    RelativeLayout selState;
    @BindView(R.id.sel_dist_t)
    TextView selDistT;
    @BindView(R.id.sel_dist)
    RelativeLayout selDist;
    @BindView(R.id.txt_peppercourse)
    TextView txtpeppercourse;
    @BindView(R.id.sel_peppercourse)
    RelativeLayout selpeppercourse;
    @BindView(R.id.sel_school_t)
    TextView selSchoolT;
    @BindView(R.id.sel_school)
    RelativeLayout selSchool;
    @BindView(R.id.ipt_trname)
    EditText iptTrname;
    @BindView(R.id.ipt_desc)
    EditText iptDesc;
    @BindView(R.id.sel_peppercourse_t)
    TextView selPeppercourseT;
    @BindView(R.id.sel_subjectarea_t)
    TextView selSubjectareaT;
    @BindView(R.id.sel_subjectarea)
    RelativeLayout selSubjectarea;
    @BindView(R.id.main)
    LinearLayout main;
    Unbinder unbinder;
    private String  authid,newStateName;
    private View view_alertListView;
    private MyListView alertListView;
    private TextView tv_noData;
    private String isadmin;
    public static CTPager2Fragment newInstance(String param1, JSONObject param2) {
        CTPager2Fragment fragment = new CTPager2Fragment();
        mParam1 = param1;
        mParam2 = param2;
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ctpager2, container, false);
        unbinder = ButterKnife.bind(this, view);
        traincate = Hawk.get("evtype");
        authid = Hawk.get("authid");
        isadmin = Hawk.get("isadmin");
        if (traincate.equals("pd_training")) {
            txtpeppercourse.setVisibility(View.GONE);
            selpeppercourse.setVisibility(View.GONE);
        }
        if (mParam1.equals("edit")) {
            initData();
        }
        //有权限选择洲和学区
        if (!isadmin.equals("1")){
            String state_name = Hawk.get("state_name").toString();
            String district_name = Hawk.get("district_name").toString();
            selState.setClickable(false);
            selDist.setClickable(false);
            selStateT.setText(state_name);
            selDistT.setText(district_name);
            statename = state_name;
            districtname = district_name;
            districtId = Hawk.get("district_id").toString();
            stateId = Hawk.get("state_id").toString();
        }
        simpleSelectAdapter = new SimpleSelectAdapter(getActivity(), new JSONArray(), 0);
        view_alertListView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_alert_listview,null);
        alertListView = view_alertListView.findViewById(R.id.listview);
        tv_noData = view_alertListView.findViewById(R.id.tv_noData);
        alertListView.setAdapter(simpleSelectAdapter);
        alertListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (cate) {
                    case 0:
                        schoolid = jsonArrays[0].getJSONObject(i).getString("id");
                        schoolname = jsonArrays[0].getJSONObject(i).getString("name");
                        selSchoolT.setText(jsonArrays[0].getJSONObject(i).getString("name"));
                        break;
                    case 1:
                        districtId = jsonArrays[1].getJSONObject(i).getString("id");
                        districtname = jsonArrays[1].getJSONObject(i).getString("name");
                        selDistT.setText(jsonArrays[1].getJSONObject(i).getString("name"));
                        break;
                    case 2:
                        subjectid = jsonArrays[2].getJSONObject(i).getString("value");
                        subjectname = jsonArrays[2].getJSONObject(i).getString("name");
                        selSubjectareaT.setText(jsonArrays[2].getJSONObject(i).getString("name"));
                        break;
                    case 3:
                        pepperCourseid = jsonArrays[3].getJSONObject(i).getString("course_id");
                        pepperCoursename = jsonArrays[3].getJSONObject(i).getString("display_name");
                        selPeppercourseT.setText(jsonArrays[3].getJSONObject(i).getString("display_name"));
                        break;
                    case 4:
                        //点击切换state的时候 地区和学校数据要发生变化
                        newStateName = jsonArrays[4].getJSONObject(i).getString("name");
                        if (!TextUtils.isEmpty(newStateName) && !TextUtils.isEmpty(statename)){
                            if (!newStateName.equals(statename)){
                                districtId = "";
                                selDistT.setText("");
                            }
                        }
                        stateId = jsonArrays[4].getJSONObject(i).getString("id");
                        statename = jsonArrays[4].getJSONObject(i).getString("name");
                        selStateT.setText(jsonArrays[4].getJSONObject(i).getString("name"));
                        break;
                }
                alert.dismiss();
            }
        });
        alert = new MaterialDialog(getActivity()).setTitle(title).setContentView(view_alertListView).setCanceledOnTouchOutside(true);
        alert.setPositiveButton(getResources().getString(R.string.Close), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        return view;
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            traincate = Hawk.get("evtype");
            if (traincate.equals("pd_training")) {
                txtpeppercourse.setVisibility(View.GONE);
                selpeppercourse.setVisibility(View.GONE);
            } else {
                txtpeppercourse.setVisibility(View.VISIBLE);
                selpeppercourse.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_prevstop, R.id.btn_nextstop, R.id.sel_state, R.id.sel_dist, R.id.sel_peppercourse, R.id.sel_school, R.id.sel_subjectarea})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_prevstop:
                mListener.onFragmentInteraction("prev", null);
                break;
            case R.id.btn_nextstop:
                if (statename.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.search_state), Toast.LENGTH_SHORT).show();
                } else if (districtId.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.search_district), Toast.LENGTH_SHORT).show();
                } else if (schoolid.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.search_school), Toast.LENGTH_SHORT).show();
                } else if (iptTrname.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.inputtn), Toast.LENGTH_SHORT).show();
                } else if (!traincate.equals("pd_training") && subjectid.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.search_subject), Toast.LENGTH_SHORT).show();
                } else if (pepperCoursename.equals("") && !traincate.equals("pd_training")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.search_peppercourses), Toast.LENGTH_SHORT).show();
                } else {
                    mjo.put("districtId", districtId);
                    mjo.put("subject", subjectid);
                    mjo.put("name", iptTrname.getText().toString());
                    mjo.put("pepperCourse", pepperCourseid);
                    mjo.put("schoolId", schoolid);
                    mjo.put("description", iptDesc.getText().toString());
                    mListener.onFragmentInteraction("next", mjo);
                }
                break;
            case R.id.sel_state:
                cate = 4;
                title = getResources().getString(R.string.search_state);
                getData();
                break;
            case R.id.sel_dist:
                //先选择州
                if (TextUtils.isEmpty(stateId)){
                    Toast.makeText(getActivity(), R.string.selectStateFirst,Toast.LENGTH_LONG).show();
                    return;
                }
                cate = 1;
                title = getResources().getString(R.string.search_district);
                getData();
                break;
            case R.id.sel_school:
                //先选择地区
                if (TextUtils.isEmpty(districtId)){
                    Toast.makeText(getActivity(), R.string.selectDistrictFirst,Toast.LENGTH_LONG).show();
                    return;
                }
                cate = 0;
                title = getResources().getString(R.string.search_school);
                getData();
                break;
            case R.id.sel_peppercourse:
                cate = 3;
                title = getResources().getString(R.string.search_peppercourses);
                getData();
                break;
            case R.id.sel_subjectarea:
                cate = 2;
                title = getResources().getString(R.string.search_subject);
                getData();
                break;
        }
    }
    private void initData() {
        stateId = mParam2.getString("state_id");
        districtId = mParam2.getString("district_id");
        subjectid = mParam2.getString("subject");
        pepperCoursename = mParam2.getString("course");
        schoolid = mParam2.getString("school_id");
        statename = mParam2.getString("name");
        pepperCourseid = mParam2.getString("pepper_course");

        selStateT.setText(mParam2.getString("state"));
        selDistT.setText(mParam2.getString("district"));
        selSchoolT.setText(mParam2.getString("school"));
        iptTrname.setText(mParam2.getString("name"));
        iptDesc.setText(mParam2.getString("description"));
        selPeppercourseT.setText(mParam2.getString("course"));
        selSubjectareaT.setText(mParam2.getString("subject_name"));
    }

    private void getData() {
        Map<String, String> param = new HashMap<>();
        String url = "";
        switch (cate) {
            case 4:
                param.put("stateId", "");
                url = ApiConfig.getSearchState();
                break;
            case 0:
                param.put("districtId", districtId);
                url = ApiConfig.getSchoolList();
                break;
            case 1:
                param.put("stateId", stateId);
                url = ApiConfig.getSearchDist();
                break;
            case 2:
                param.put("user_id", authid);
                url = ApiConfig.getSearchSubject();
                break;
            case 3:
                param.put("user_id", authid);
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
                        alertListView.setAdapter(simpleSelectAdapter);
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
                //暂无数据的显示
                if (jsonArrays[cate].size()==0){
                    alertListView.setVisibility(View.GONE);
                    tv_noData.setVisibility(View.VISIBLE);
                }else{
                    alertListView.setVisibility(View.VISIBLE);
                    tv_noData.setVisibility(View.GONE);
                }
                alert.setTitle(title);
                alert.show();
            } else if (msg.what == -1) {
                Toast.makeText(getActivity(), getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String result, JSONObject mjo);
    }
}
