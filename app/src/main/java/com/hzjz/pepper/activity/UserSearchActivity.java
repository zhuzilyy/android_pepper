package com.hzjz.pepper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.hzjz.pepper.R;
import com.hzjz.pepper.fragment.AdSearchFragment;
import com.hzjz.pepper.fragment.ReSearchFragment;
import com.hzjz.pepper.fragment.UserAdSearchFragment;
import com.hzjz.pepper.fragment.UserReSearchFragment;

import java.util.ArrayList;
import java.util.Base64;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserSearchActivity extends FragmentActivity implements OnTabSelectListener, UserAdSearchFragment.OnFragmentInteractionListener, UserReSearchFragment.OnFragmentInteractionListener {
    private String type = "0";
    private Bundle param;

    @BindView(R.id.btn_back)
    LinearLayout btnBack;
    @BindView(R.id.search_tab)
    SlidingTabLayout searchTab;
    @BindView(R.id.vp)
    ViewPager vp;

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles = {"Regular Search", "Advanced Search"};
    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        ButterKnife.bind(this);
        param = getIntent().getBundleExtra("param");
        type = param.getString("type");
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);

        mFragments.add(UserAdSearchFragment.newInstance(type, param));
        mFragments.add(UserReSearchFragment.newInstance(type, param));
        searchTab.setViewPager(vp, mTitles, this, mFragments);
        if (type.equals("0") || type.equals("1")) {
            searchTab.setCurrentTab(0);
        } else {
            searchTab.setCurrentTab(1);
        }
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onTabSelect(int position) {
//        Toast.makeText(this, "onTabSelect&position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTabReselect(int position) {
//        Toast.makeText(this, "onTabReselect&position--->" + position, Toast.LENGTH_SHORT).show();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    @Override
    public void onFragmentInteraction(int code, String result) {
        Intent intent = new Intent();
        intent.putExtra("param", result);
        setResult(code, intent);
        finish();
    }
}
