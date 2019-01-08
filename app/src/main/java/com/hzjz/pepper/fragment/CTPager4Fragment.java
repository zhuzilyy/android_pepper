package com.hzjz.pepper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hzjz.pepper.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CTPager4Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String mParam1;
    private static JSONObject mParam2;
    private OnFragmentInteractionListener mListener;
    private JSONObject mjo = new JSONObject();

    @BindView(R.id.btn_prevstop)
    ImageButton btnPrevstop;
    @BindView(R.id.btn_nextstop)
    ImageButton btnNextstop;
    @BindView(R.id.map)
    RelativeLayout map;
    @BindView(R.id.main)
    LinearLayout main;
    @BindView(R.id.location)
    EditText location;
    @BindView(R.id.addr)
    EditText addr;
    Unbinder unbinder;

    public static CTPager4Fragment newInstance(String param1, JSONObject param2) {
        CTPager4Fragment fragment = new CTPager4Fragment();
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
        View view = inflater.inflate(R.layout.fragment_ctpager4, container, false);
        unbinder = ButterKnife.bind(this, view);if (mParam1.equals("edit")){
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

    @OnClick({R.id.btn_prevstop, R.id.btn_nextstop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_prevstop:
                mListener.onFragmentInteraction("prev", null);
                break;
            case R.id.btn_nextstop:
                if (location.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.step4_iptlocation), Toast.LENGTH_SHORT).show();
                } else if (addr.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.step4_seladd), Toast.LENGTH_SHORT).show();
                } else {
                    mjo.put("geoLocation", location.getText().toString());
                    mjo.put("geoDestination", addr.getText().toString());
                    mjo.put("geoProps", "");
                    mListener.onFragmentInteraction("next", mjo);
                }
                break;
        }
    }

    private void initData() {
        location.setText(mParam2.getString("geoLocation"));
        addr.setText(mParam2.getString("geoDestination"));
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String result, JSONObject mjo);
    }
}
