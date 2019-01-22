package com.hzjz.pepper.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.hzjz.pepper.R;
import com.hzjz.pepper.activity.EventActivity;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CTPager1Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String mParam1;
    private static JSONObject mParam2;
    private OnFragmentInteractionListener mListener;

    private JSONObject mjo = new JSONObject();

    @BindView(R.id.btn_nextstop)
    ImageButton btnNextstop;
    @BindView(R.id.chk_cate1)
    ImageView chkCate1;
    @BindView(R.id.btn_cate1)
    LinearLayout btnCate1;
    @BindView(R.id.chk_cate2)
    ImageView chkCate2;
    @BindView(R.id.btn_cate2)
    LinearLayout btnCate2;
    Unbinder unbinder;

    public static CTPager1Fragment newInstance(String param1, JSONObject param2) {
        CTPager1Fragment fragment = new CTPager1Fragment();
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
        View view = inflater.inflate(R.layout.fragment_ctpager1, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (mParam1.equals("add")) {
            mjo.put("type", "pepper_course");
            Hawk.put("evtype", "pepper_course");
        } else if (mParam1.equals("edit")){
            if (mParam2.getString("type").equals("pepper_course")) {
                mjo.put("type", "pepper_course");
                Hawk.put("evtype", "pepper_course");
                chkCate1.setImageResource(R.mipmap.s27);
                chkCate2.setImageResource(R.mipmap.s26);
            } else {
                mjo.put("type", "pd_training");
                Hawk.put("evtype", "pd_training");
                chkCate1.setImageResource(R.mipmap.s26);
                chkCate2.setImageResource(R.mipmap.s27);
            }
        }
        Hawk.put("chour", "0");

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

    @OnClick({R.id.btn_nextstop, R.id.btn_cate1, R.id.btn_cate2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_nextstop:
                mListener.onFragmentInteraction("next", mjo);
                break;
            case R.id.btn_cate1:
                mjo.put("type", "pepper_course");
                Hawk.put("evtype", "pepper_course");
                chkCate1.setImageResource(R.mipmap.s27);
                chkCate2.setImageResource(R.mipmap.s26);
                break;
            case R.id.btn_cate2:
                mjo.put("type", "pd_training");
                Hawk.put("evtype", "pd_training");
                chkCate1.setImageResource(R.mipmap.s26);
                chkCate2.setImageResource(R.mipmap.s27);
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String result, JSONObject mjo);
    }
}
