package com.hzjz.pepper.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.hzjz.pepper.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CTPager4Fragment extends SupportMapFragment implements
        AMap.OnMarkerClickListener, AMapLocationListener,AMap.OnPOIClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String mParam1;
    private static JSONObject mParam2;
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
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
    TextView addr;
    @BindView(R.id.mapView)
    MapView mMapView;
    Unbinder unbinder;
    private OnFragmentInteractionListener mListener;
    private JSONObject mjo = new JSONObject();
    private AMap mAMap;
    private boolean isFristLocation = true;
    private String addressLocation;
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
        unbinder = ButterKnife.bind(this, view);
        if (mParam1.equals("edit")) {
            initData();
        }
        //初始化地图相关的方法
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        mAMap.setOnPOIClickListener(this);
        mAMap.setOnMarkerClickListener(this);
        //初始化定位服务
        initLocation();
        return view;
    }
    private void initLocation() {
        mlocationClient = new AMapLocationClient(getActivity());
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    /**
     * 底图poi点击回调
     */
    @Override
    public void onPOIClick(Poi poi) {
        mAMap.clear();
        //mAMap.setMapLanguage("en");
        Log.i("MY", poi.getPoiId() + poi.getName());
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(poi.getCoordinate());
        addr.setText(poi.getName());
        addressLocation = "{"+ "\"lat\""+":"+"\""+poi.getCoordinate().latitude+"\""+","+"\"lon\""+":"+"\""+poi.getCoordinate().longitude+"\""+"}";
        //textView.setBackgroundResource(R.mipmap.custom_info_bubble);
        //markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
        mAMap.addMarker(markOptiopns);
    }
    /**
     * Marker 点击回调
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        naviPara.setTargetPoint(marker.getPosition());
        // 设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
       /* try {
            // 调起高德地图导航
            AMapUtils.openAMapNavi(naviPara, getActivity());
        } catch (com.amap.api.maps.AMapException e) {
            // 如果没安装会进入异常，调起下载页面
            AMapUtils.getLatestAMapApp(getActivity());
        }*/
        mAMap.clear();
        return false;
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
        if (mMapView!=null){
            mMapView.onDestroy();
        }
    }

    @OnClick({R.id.btn_prevstop, R.id.btn_nextstop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_prevstop:
                mListener.onFragmentInteraction("prev", null);
                break;
            case R.id.btn_nextstop:
              /*  if (location.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.step4_iptlocation), Toast.LENGTH_SHORT).show();
                } else if (addr.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.step4_seladd), Toast.LENGTH_SHORT).show();
                } else {
                    mjo.put("geoLocation", location.getText().toString());
                    mjo.put("geoDestination", addr.getText().toString());
                    mjo.put("geoProps", "");
                    mListener.onFragmentInteraction("next", mjo);
                }*/
                mjo.put("geoLocation", location.getText().toString());
                mjo.put("geoDestination", addr.getText().toString());
                mjo.put("geoProps",addressLocation);
                mListener.onFragmentInteraction("next", mjo);
                break;
        }
    }

    private void initData() {
        location.setText(mParam2.getString("classroom"));
        addr.setText(mParam2.getString("geo_location"));
        addressLocation = mParam2.getString("geo_props");
        //{"lat":"41.9089787715602","lon":"123.40724376323043"}
        String substring = addressLocation.substring(2, addressLocation.length() - 1);
        String[] split = substring.split(",");
        String[] splitLat = split[0].split(":");
        String[] splitLon = split[1].split(":");
        String lat = splitLat[1];
        String lon = splitLon[1];
        String subLat = lat.substring(1,lat.length()-1);
        String subLon = lon.substring(1,lon.length()-1);
        LatLng latLng = new LatLng(Double.parseDouble(subLat),Double.parseDouble(subLon));
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(latLng);
        //markOptiopns.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
        if (mAMap==null){
            mAMap = mMapView.getMap();
        }
        mAMap.addMarker(markOptiopns);

    }

    //地图定位功能
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.i("tag", aMapLocation.getCity() + aMapLocation.getAddress());
        Log.e("AmapError", "location Error, ErrCode:"
                + aMapLocation.getErrorCode() + ", errInfo:"
                + aMapLocation.getErrorInfo());
        if (isFristLocation){
            isFristLocation = false;
            LatLng curLatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatlng, 16f));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String result, JSONObject mjo);
    }
}
