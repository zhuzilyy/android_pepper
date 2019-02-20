package com.hzjz.pepper.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleTextView;
import com.cpiz.android.bubbleview.RelativePos;
import com.hzjz.pepper.R;
import com.orhanobut.hawk.Hawk;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.cpiz.android.bubbleview.Utils.dp2px;

public class CTPager61Fragment extends Fragment {
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
    @BindView(R.id.regis_switch)
    Switch regisSwitch;
    @BindView(R.id.maxrc)
    EditText maxrc;
    @BindView(R.id.heip1)
    ImageButton heip1;
    @BindView(R.id.waitlist_switch)
    Switch waitlistSwitch;
    @BindView(R.id.heip2)
    ImageButton heip2;
    @BindView(R.id.att_switch)
    Switch attSwitch;
    @BindView(R.id.heip3)
    ImageButton heip3;
    @BindView(R.id.attrec_switch)
    Switch attrecSwitch;
    @BindView(R.id.heip4)
    ImageButton heip4;
    @BindView(R.id.val_switch)
    Switch valSwitch;
    @BindView(R.id.heip5)
    ImageButton heip5;
    @BindView(R.id.main)
    LinearLayout main;
    Unbinder unbinder;
    private BubblePopupWindow mBubblePopupWindow;
    public static CTPager61Fragment newInstance(String param1, JSONObject param2) {
        CTPager61Fragment fragment = new CTPager61Fragment();
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
        View view = inflater.inflate(R.layout.fragment_ctpager61, container, false);
        unbinder = ButterKnife.bind(this, view);
        maxrc.setText(Hawk.get("chour").toString());
        if (mParam1.equals("edit")) {
            initData();
            maxrc.setFocusable(true);
            maxrc.setFocusableInTouchMode(true);
        }
        regisSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    maxrc.setFocusable(true);
                    maxrc.setFocusableInTouchMode(true);
                } else {
                    maxrc.setFocusable(false);
                    maxrc.setFocusableInTouchMode(false);
                }
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.btn_prevstop, R.id.btn_nextstop,R.id.heip1,R.id.heip2,R.id.heip3,R.id.heip5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_prevstop:
                mListener.onFragmentInteraction("prev", null);
                break;
            case R.id.btn_nextstop:
                if (regisSwitch.isChecked() && maxrc.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.iptmrc), Toast.LENGTH_SHORT).show();
                } else {
                    mjo.put("allowRegistration", String.valueOf(regisSwitch.isChecked()));
                    mjo.put("maxRegistration", maxrc.getText().toString().equals("") ? 0 : maxrc.getText().toString());
                    mjo.put("allowWaitlist", String.valueOf(waitlistSwitch.isChecked()));
                    mjo.put("allowStudentAttendance", String.valueOf(attSwitch.isChecked()));
                    mjo.put("allowAttendance", String.valueOf(attrecSwitch.isChecked()));
                    mjo.put("allowValidation", String.valueOf(valSwitch.isChecked()));
                    mListener.onFragmentInteraction("commit", mjo);
                }
                break;
            case R.id.heip1:
                heip1.setImageResource(R.mipmap.help_selected);
                showPop("Vaild only before the training date",heip1);
                break;
            case R.id.heip2:
                heip2.setImageResource(R.mipmap.help_selected);
                showPop("This will allow students to be added to waitlist",heip2);
                break;
            case R.id.heip3:
                heip3.setImageResource(R.mipmap.help_selected);
                showPop("This should only be used for trainings or PD events that do not have an instructor",heip3);
                break;
            case R.id.heip5:
                heip5.setImageResource(R.mipmap.help_selected);
                showPop("Optional Field. This allows an instructor to validate that a user attended a training. PD time credit is not awarded until validation occurs if this field is checked",heip5);
                break;
        }
    }

    private void initData() {
        if (mParam2.getString("allow_registration").equals("True")) {
            regisSwitch.setChecked(true);
            maxrc.setText(mParam2.getString("max_registration"));
        }
        if (mParam2.getString("allow_waitlist").equals("True")) {
            waitlistSwitch.setChecked(true);
        }
        if (mParam2.getString("allow_student_attendance").equals("True")) {
            attSwitch.setChecked(true);
        }
        if (mParam2.getString("allow_attendance").equals("True")) {
            attrecSwitch.setChecked(true);
        }
        if (mParam2.getString("allow_validation").equals("True")) {
            valSwitch.setChecked(true);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String result, JSONObject mjo);
    }
    //显示提示框
    private void showPop(String tvStr, ImageView iv) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.simple_text_bubble, null);
        BubbleTextView mBubbleTextView = rootView.findViewById(R.id.popup_bubble);
        mBubblePopupWindow = new BubblePopupWindow(rootView, mBubbleTextView);
        mBubblePopupWindow.setPadding(dp2px(50));
        mBubbleTextView.setText(tvStr);
        RelativePos mRelativePos = new RelativePos(RelativePos.CENTER_HORIZONTAL, RelativePos.CENTER_VERTICAL);
        mRelativePos.setHorizontalRelate(RelativePos.TO_LEFT_OF);
        showPopupBubble(mBubblePopupWindow, mRelativePos, 0, 0, iv);
        mBubblePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                heip1.setImageResource(R.mipmap.s47);
                heip2.setImageResource(R.mipmap.s47);
                heip3.setImageResource(R.mipmap.s47);
                heip5.setImageResource(R.mipmap.s47);
            }
        });
    }
    private void showPopupBubble(BubblePopupWindow mBubblePopupWindow, RelativePos mRelativePos, int mMarginH, int mMarginV, ImageView iv) {
        if (getActivity().hasWindowFocus()) {
            mBubblePopupWindow.showArrowTo(iv, mRelativePos, mMarginH, mMarginV);
        }
    }
}
