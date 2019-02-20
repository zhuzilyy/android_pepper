package com.hzjz.pepper.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hzjz.pepper.R;
import com.hzjz.pepper.activity.InstSearchActivity;
import com.hzjz.pepper.bean.ResultDesc;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.http.HttpCallback;
import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.http.utils.DialogUtil;
import com.hzjz.pepper.plugins.DateUtil;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CTPager5Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String mParam1;
    private static JSONObject mParam2 = new JSONObject();
    private OnFragmentInteractionListener mListener;

    private JSONObject mjo = new JSONObject();
    HashMap<Integer, LinearLayout> conditions = new HashMap<>();
    int itemId = 0;
    private int position = 0;
    private JSONArray mja = new JSONArray();
    private Map<Integer, JSONObject> mapa = new HashMap<>();
    private JSONObject instja = new JSONObject();

    @BindView(R.id.btn_prevstop)
    ImageButton btnPrevstop;
    @BindView(R.id.btn_nextstop)
    ImageButton btnNextstop;
    @BindView(R.id.viewcontainer)
    LinearLayout viewcontainer;
    @BindView(R.id.main)
    LinearLayout main;
    @BindView(R.id.btn_add)
    ImageButton btnAdd;
    @BindView(R.id.scrollview)
    ScrollView scrollview;
    Unbinder unbinder;

    public static CTPager5Fragment newInstance(String param1, JSONObject param2) {
        CTPager5Fragment fragment = new CTPager5Fragment();
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
        View view = inflater.inflate(R.layout.fragment_ctpager5, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (mParam1.equals("edit")) {
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

    @OnClick({R.id.btn_prevstop, R.id.btn_nextstop, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_prevstop:
                mListener.onFragmentInteraction("prev", null);
                break;
            case R.id.btn_nextstop:
//                mjo.put("instructorList", mja.toString());
                mja.clear();
                for (Integer in : mapa.keySet()) {
                    mja.add(mapa.get(in));
                }
                mjo.put("instructorList", mja.toString());
                mListener.onFragmentInteraction("next", mjo);
                break;
            case R.id.btn_add:
                Intent intent = new Intent(getActivity(), InstSearchActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    private void initData() {
        getInstData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                JSONObject jo = new JSONObject();
                jo.put("name", data.getStringExtra("name"));
                jo.put("instructorId", data.getStringExtra("id"));
                jo.put("trainingId", "");
                jo.put("allEdit", "false");
                jo.put("allDelete", "false");
                jo.put("userCreateId", Hawk.get("authid").toString());
                jo.put("dateCreate", data.getStringExtra("dateCreate"));
//                mja.add(itemId, jo);
                mapa.put(itemId, jo);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout nview = (LinearLayout) View.inflate(getActivity(), R.layout.item_addpd_list, null);
                nview.setLayoutParams(lp);
                TextView tvtitle = nview.findViewById(R.id.item_name);
                tvtitle.setText(data.getStringExtra("name"));
                tvtitle.setTag(itemId);
                Switch r1 = nview.findViewById(R.id.ed_switch);
                r1.setTag(itemId);
                Switch r2 = nview.findViewById(R.id.del_switch);
                r2.setTag(itemId);

                r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int viewindex = (int) buttonView.getTag();
//                        mja.getJSONObject(viewindex).put("allEdit", String.valueOf(isChecked));
                        mapa.get(viewindex).put("allEdit", String.valueOf(isChecked));
                    }
                });
                r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int viewindex = (int) buttonView.getTag();
//                        mja.getJSONObject(viewindex).put("allDelete", String.valueOf(isChecked));
                        mapa.get(viewindex).put("allDelete", String.valueOf(isChecked));
                    }
                });

                Button btn_del = nview.findViewById(R.id.btn_del);
                btn_del.setTag(itemId);
                btn_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int viewindex = (int) view.getTag();
                        viewcontainer.removeView(conditions.get(viewindex));
                        conditions.remove(viewindex);
//                        mja.remove(viewindex);
                        mapa.remove(viewindex);
                        position--;
                    }
                });
                conditions.put(itemId, nview);
                viewcontainer.addView(nview, position);
                position++;
                itemId++;

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        }
    }

    private void getInstData() {
        Map<String, String> param = new HashMap<>();
        param.put("id", mParam2.getString("id"));
        param.put("user_id",Hawk.get("authid").toString());
        param.put("client_time", DateUtil.getCurrentTime());
        OkHttpUtils.postJsonAsyn(ApiConfig.getInstructor(), param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                DialogUtil.hideDialogLoading();
                Message msg = new Message();
                if (resultDesc.getError_code() == 0) {
                    try {
                        instja = JSON.parseObject(resultDesc.getResult());
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
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
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                for (int i = 0; i < instja.getJSONArray("instructor_permission_emails").size(); i++) {
                    itemId = i;
                    position = i;
                    createView();
                }
            } else if (msg.what == 3) {
                Toast.makeText(getActivity(), getResources().getString(R.string.cnfailed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void createView() {
        JSONArray emails = instja.getJSONArray("instructor_permission_emails");
        String emailContent = emails.get(itemId).toString();
        String[] array = emailContent.split("::");
        JSONObject jo = new JSONObject();
        jo.put("trainingId", mParam2.getString("id"));
        jo.put("allEdit", array[1].equals("1")?"true":"false");
        jo.put("allDelete", array[2].equals("1")?"true":"false");
        jo.put("name", array[0]);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout nview = (LinearLayout) View.inflate(getActivity(), R.layout.item_addpd_list, null);
        nview.setLayoutParams(lp);
        TextView tvtitle = nview.findViewById(R.id.item_name);
        tvtitle.setText(array[0]);
        mapa.put(itemId, jo);
        tvtitle.setTag(itemId);
        Switch r1 = nview.findViewById(R.id.ed_switch);
        r1.setTag(itemId);
        if (array[1].equals("1")) {
            r1.setChecked(true);
        }
        Switch r2 = nview.findViewById(R.id.del_switch);
        r2.setTag(itemId);
        if ( array[2].equals("1")) {
            r2.setChecked(true);
        }

        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int viewindex = (int) buttonView.getTag();
//                mja.getJSONObject(viewindex).put("allEdit", String.valueOf(isChecked));
                mapa.get(viewindex).put("allEdit", String.valueOf(isChecked));
            }
        });
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int viewindex = (int) buttonView.getTag();
//                mja.getJSONObject(viewindex).put("allDelete", String.valueOf(isChecked));
                mapa.get(viewindex).put("allDelete", String.valueOf(isChecked));
            }
        });

        Button btn_del = nview.findViewById(R.id.btn_del);
        btn_del.setTag(itemId);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewindex = (int) view.getTag();
                viewcontainer.removeView(conditions.get(viewindex));
                conditions.remove(viewindex);
//                mja.remove(viewindex);
                mapa.remove(viewindex);
                position--;
            }
        });
        conditions.put(itemId, nview);
        viewcontainer.addView(nview, position);
        position++;
        itemId++;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String result, JSONObject mjo);
    }
}
