package com.hzjz.pepper.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hzjz.pepper.R;
import com.hzjz.pepper.plugins.DateUtil;
import com.hzjz.pepper.plugins.PopMsPicker;
import com.hzjz.pepper.plugins.PopYmdPicker;
import com.orhanobut.hawk.Hawk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CTPager3Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String mParam1;
    private static JSONObject mParam2;
    private OnFragmentInteractionListener mListener;

    private JSONObject mjo = new JSONObject();
    private String trainingDate = "", stime = "", etime = "";
    View thisview;
    PopYmdPicker popYmdPicker;
    PopMsPicker popMsPicker;
    int sh = 0, eh = 0;

    @BindView(R.id.btn_prevstop)
    ImageButton btnPrevstop;
    @BindView(R.id.btn_nextstop)
    ImageButton btnNextstop;
    @BindView(R.id.sel_tdate_t)
    TextView selTdateT;
    @BindView(R.id.sel_tdate)
    RelativeLayout selTdate;
    @BindView(R.id.sel_stime_t)
    TextView selStimeT;
    @BindView(R.id.sel_stime)
    RelativeLayout selStime;
    @BindView(R.id.sel_etime_t)
    TextView selEtimeT;
    @BindView(R.id.sel_etime)
    RelativeLayout selEtime;
    @BindView(R.id.main)
    LinearLayout main;
    Unbinder unbinder;

    public static CTPager3Fragment newInstance(String param1, JSONObject param2) {
        CTPager3Fragment fragment = new CTPager3Fragment();
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
        View view = inflater.inflate(R.layout.fragment_ctpager3, container, false);
        this.thisview = view;
        unbinder = ButterKnife.bind(this, view);
        if (mParam1.equals("edit")){
            initData();
        }
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_prevstop, R.id.btn_nextstop, R.id.sel_tdate, R.id.sel_stime, R.id.sel_etime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_prevstop:
                mListener.onFragmentInteraction("prev", null);
                break;
            case R.id.btn_nextstop:
                if (trainingDate.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.search_date), Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(stime.replace(":", "")) >= Integer.parseInt(etime.replace(":", ""))) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.stimebigthanetime), Toast.LENGTH_SHORT).show();
                } else if (etime.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.search_etime), Toast.LENGTH_SHORT).show();
                } else if (etime.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.search_etime), Toast.LENGTH_SHORT).show();
                } else {
                    mjo.put("trainingDate", trainingDate);
                    mjo.put("trainingTimeStart", stime);
                    mjo.put("trainingTimeEnd", etime);
                    Hawk.put("chour", String.valueOf(Integer.parseInt(etime.substring(0, etime.indexOf(":"))) - Integer.parseInt(stime.substring(0, stime.indexOf(":")))));
//                    Hawk.put("chour", String.valueOf(eh - sh));
                    mListener.onFragmentInteraction("next", mjo);
                }
                break;
            case R.id.sel_tdate:
                popYmdPicker = new PopYmdPicker(getActivity());
                popYmdPicker.setOnCheckBackListener(new PopYmdPicker.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(String year, String month, String day) {
                        String datet = month + "/" + day + "/" + year;
                        trainingDate = year + "-" + month + "-" + day;
                        selTdateT.setText(datet);
                    }
                });
                popYmdPicker.showAtLocation(main, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.sel_stime:
                popMsPicker = new PopMsPicker(getActivity());
                popMsPicker.setOnCheckBackListener(new PopMsPicker.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(String hour, String second) {
                        String datet = hour + ":" + second;
                        sh = Integer.parseInt(hour);
                        stime = datet;
                        selStimeT.setText(datet);
                    }
                });
                popMsPicker.showAtLocation(main, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.sel_etime:
                popMsPicker = new PopMsPicker(getActivity());
                popMsPicker.setOnCheckBackListener(new PopMsPicker.onCheckClickListener() {
                    @Override
                    public void onCheckCallback(String hour, String second) {
                        String datet = hour + ":" + second;
                        eh = Integer.parseInt(hour);
                        etime = datet;
                        selEtimeT.setText(datet);
                    }
                });
                popMsPicker.showAtLocation(main, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
        }
    }

    private void initData() {
        trainingDate = mParam2.getString("trainingDate");
        stime = mParam2.getString("trainingTimeStart");
        etime = mParam2.getString("trainingTimeEnd");
        selTdateT.setText(mParam2.getString("trainingDate"));
        selStimeT.setText(mParam2.getString("trainingTimeStart"));
        selEtimeT.setText(mParam2.getString("trainingTimeEnd"));
        if (mParam1.equals("edit")){
            selTdate.setClickable(false);
            selTdate.setFocusable(false);
            selTdate.setFocusableInTouchMode(false);
            selStime.setClickable(false);
            selStime.setFocusable(false);
            selStime.setFocusableInTouchMode(false);
            selEtime.setClickable(false);
            selEtime.setFocusable(false);
            selEtime.setFocusableInTouchMode(false);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String result, JSONObject mjo);
    }
}
