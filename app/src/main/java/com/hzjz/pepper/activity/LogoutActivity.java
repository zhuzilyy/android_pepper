package com.hzjz.pepper.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hzjz.pepper.R;
import com.hzjz.pepper.config.ApiConfig;
import com.hzjz.pepper.plugins.Base64ImageUtil;
import com.hzjz.pepper.plugins.GlideApp;
import com.hzjz.pepper.plugins.PopConfirmWindow;
import com.orhanobut.hawk.Hawk;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class LogoutActivity extends Activity {

    @BindView(R.id.backbtn)
    LinearLayout backbtn;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.centercursor)
    RelativeLayout centercursor;
    @BindView(R.id.logout_btn)
    Button logoutBtn;
    @BindView(R.id.login_username)
    TextView loginUsername;

    PopConfirmWindow PopConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        ButterKnife.bind(this);
        loginUsername.setText(Hawk.get("username").toString());
        String imgBase64 = Hawk.get("imgurl");
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.s31);
        try {
            if (!imgBase64.equals("")) {
                imgBase64 = ApiConfig.ServerUrl +imgBase64;
                Bitmap imgbmt = Base64ImageUtil.base64ToBitmap(imgBase64);
                //GlideApp.with(this).load(imgbmt).placeholder(R.mipmap.s31).dontAnimate().into(profileImage);
                Glide.with(this).load(imgBase64).apply(options).into(profileImage);
            } else {
                //GlideApp.with(this).load("sdfsdf").placeholder(R.mipmap.s31).dontAnimate().into(profileImage);
                Glide.with(this).load(R.mipmap.s31).apply(options).into(profileImage);
            }
        } catch (Exception e) {
            //GlideApp.with(this).load("sdfsdf").placeholder(R.mipmap.s31).dontAnimate().into(profileImage);
            Glide.with(this).load(R.mipmap.s31).apply(options).into(profileImage);
        }
    }
    @OnClick({R.id.backbtn, R.id.logout_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backbtn:
                finish();
                break;
            case R.id.logout_btn:
                PopConfirm = new PopConfirmWindow(this, "Are you sure you want to get out of the training?", listener);
                PopConfirm.showAtLocation(this.findViewById(R.id.main), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.popcfm_no:
                    PopConfirm.dismiss();
                    break;
                case R.id.popcfm_yes:
                    Hawk.delete("authid");
                    Hawk.delete("username");
                    Hawk.delete("imgurl");
                    Hawk.delete("isadmin");
                    Intent intent = new Intent("com.pepper.localbroadcast");
                    intent.putExtra("type", "finish");
                    LocalBroadcastManager.getInstance(LogoutActivity.this).sendBroadcast(intent);
                    PopConfirm.dismiss();
                    Toast.makeText(LogoutActivity.this, "Logout successfully!", Toast.LENGTH_SHORT).show();
                    Intent sintent = new Intent(LogoutActivity.this, LoginActivity.class);
                    startActivity(sintent);
                    finish();
                    break;
            }
        }
    };
}
