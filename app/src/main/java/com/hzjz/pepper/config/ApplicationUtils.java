package com.hzjz.pepper.config;

import android.app.Application;

import com.hzjz.pepper.http.OkHttpUtils;
import com.hzjz.pepper.plugins.CrashHandler;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.squareup.leakcanary.LeakCanary;

import android.content.Context;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApplicationUtils extends Application {
    public static Context mContext;
    static {
        ClassicsHeader.REFRESH_HEADER_PULLDOWN = "pull down to refresh";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = "refreshing...";
        ClassicsHeader.REFRESH_HEADER_LOADING = "loading...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = "release immediately refresh";
        ClassicsHeader.REFRESH_HEADER_FINISH = "refresh completed";
        ClassicsHeader.REFRESH_HEADER_FAILED = "refresh failed";
        ClassicsHeader.REFRESH_HEADER_LASTTIME = "'last update:' M-d HH:mm";

        ClassicsFooter.REFRESH_FOOTER_PULLUP = "pull up to load more";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = "release immediate loading";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = "refreshing...";
        ClassicsFooter.REFRESH_FOOTER_LOADING = "loading...";
        ClassicsFooter.REFRESH_FOOTER_FINISH = "loading completed";
        ClassicsFooter.REFRESH_FOOTER_FAILED = "loading failed";
        ClassicsFooter.REFRESH_FOOTER_ALLLOADED = "all loaded";

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
                @NonNull
                @Override
                public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                    layout.setPrimaryColorsId(com.hzjz.pepper.R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                    return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
                }
            });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
                @NonNull
                @Override
                public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                    //指定为经典Footer，默认是 BallPulseFooter
                    return new ClassicsFooter(context).setDrawableSize(20);
                }
            });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).build();
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
        mContext = getApplicationContext();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)//连接超时(单位:秒)
                .writeTimeout(20, TimeUnit.SECONDS)//写入超时(单位:秒)
                .readTimeout(20, TimeUnit.SECONDS)//读取超时(单位:秒)
//                .addInterceptor(new MyLogInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
        LeakCanary.install(this);
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    //拦截器,可以修改header,可以通过拦截器打印日志
    public class MyLogInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder()
//                    .header("shenfen", "chinesse")
                    .build();
            HttpUrl url = request.url();
            String httpUrl = url.url().toString();
            Log.e("TAG", "============" + httpUrl);
            Response response = chain.proceed(request);
            int code = response.code();
            Log.e("TAG", "============response.code() == " + code);
            return response;
        }
    }
}
