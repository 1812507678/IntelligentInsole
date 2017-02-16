package com.amsu.intelligentinsole.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amsu.intelligentinsole.R;
import com.amsu.intelligentinsole.common.BaseActivity;
import com.amsu.intelligentinsole.view.CircleRingView;

public class HomeActivity extends BaseActivity implements LocationSource,
        AMapLocationListener {

    private static final String TAG = "HomeActivity";
    private CircleRingView vr_home_ring;
    private MapView mv_home_map;
    private AMap mMap;
    private OnLocationChangedListener mListener;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mv_home_map.onSaveInstanceState(outState);
    }

    @Override
    protected void initView() {
        setLeftImage(R.drawable.home_menu);
        setCenterText("智能跑鞋");
        setRightImage(R.drawable.home_run);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,MeActivity.class));
            }
        });

        getIv_base_rightimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,StartRunActivity.class));
            }
        });

        vr_home_ring = (CircleRingView) findViewById(R.id.vr_home_ring);

        mv_home_map = (MapView) findViewById(R.id.mv_home_map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        //mv_home_map.onCreate(savedInstanceState);
        mv_home_map.onCreate(mSavedInstanceState);


    }

    @Override
    protected void initData() {
        mMap = mv_home_map.getMap();
        // 设置定位监听
        mMap.setLocationSource(this);
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setupLocationStyle();

    }


    @Override
    protected void onResume() {
        super.onResume();
        vr_home_ring.setValue(100);
        mv_home_map.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mv_home_map.onPause();
        deactivate();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mv_home_map.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    //设置定位点图标
    private void setupLocationStyle(){
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        mMap.setMyLocationStyle(myLocationStyle);
    }

    //定位回调
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                Log.i(TAG,"onLocationChanged:"+aMapLocation.toString());
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                float zoom = mMap.getCameraPosition().zoom;
                Log.i(TAG,"zoom:"+zoom);
                if (zoom==10.0){
                    zoom=18;
                }
                mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}
