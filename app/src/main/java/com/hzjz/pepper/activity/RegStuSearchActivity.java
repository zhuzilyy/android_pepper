package com.hzjz.pepper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzjz.pepper.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegStuSearchActivity extends Activity {
    private int code = 1;

    @BindView(R.id.btn_back)
    LinearLayout btnBack;
    @BindView(R.id.maintitle)
    TextView maintitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.t_state)
    TextView tState;
    @BindView(R.id.s_state)
    RelativeLayout sState;
    @BindView(R.id.t_dist)
    TextView tDist;
    @BindView(R.id.s_dist)
    RelativeLayout sDist;
    @BindView(R.id.t_school)
    TextView tSchool;
    @BindView(R.id.s_school)
    RelativeLayout sSchool;
    @BindView(R.id.t_email)
    EditText tEmail;
    @BindView(R.id.t_username)
    EditText tUsername;
    @BindView(R.id.t_fname)
    EditText tFname;
    @BindView(R.id.t_lname)
    EditText tLname;
    @BindView(R.id.t_cohort)
    TextView tCohort;
    @BindView(R.id.s_cohort)
    RelativeLayout sCohort;
    @BindView(R.id.btn_reset)
    Button btnReset;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.container)
    RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_stu_search);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_back, R.id.s_state, R.id.s_dist, R.id.s_school, R.id.s_cohort, R.id.btn_reset, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.s_state:
                break;
            case R.id.s_dist:
                break;
            case R.id.s_school:
                break;
            case R.id.s_cohort:
                break;
            case R.id.btn_reset:
                break;
            case R.id.btn_search:
                Intent intent = new Intent();
                intent.putExtra("param", "");
                setResult(code, intent);
                finish();
                break;
        }
    }
}
