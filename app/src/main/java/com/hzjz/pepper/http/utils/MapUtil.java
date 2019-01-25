package com.hzjz.pepper.http.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.amap.api.maps.model.LatLng;

public class MapUtil {
    //进入百度地图
    public static void invokeBaiDuMap(Context context, LatLng latLng, String addressName) {
        try {
            Intent intent = new Intent();
            intent.setData(Uri.parse("baidumap://map/marker?location=" + latLng.latitude + ","+  latLng.longitude + "&title=" + addressName + "&content="+ "" +"&traffic=on"+"&src=andr.baidu.openAPIdem"));
            intent.setPackage("com.baidu.BaiduMap");
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }
    //进入腾讯地图
    public static void invoketencentMap(Context context,LatLng latLng,String addressName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://apis.map.qq.com/uri/v1/marker?marker=coord:"+ latLng.latitude  + "," + latLng.longitude + ";" + "title:" + addressName + ";addr:" + " " + "&referer=myapp"));
        context.startActivity(intent);
    }
}
