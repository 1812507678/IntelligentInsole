package com.amsu.intelligentinsole.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amsu.intelligentinsole.R;
import com.amsu.intelligentinsole.common.BaseActivity;
import com.amsu.intelligentinsole.map.SensorEventHelper;

public class StartRunActivity extends BaseActivity implements LocationSource,
        AMapLocationListener {

    private static final String TAG = "StartRunActivity";
    private MapView mv_startrun_map;
    private AMap mMap;
    private OnLocationChangedListener mListener;
    private static final int STROKE_COLOR = Color.argb(0, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(0, 0, 0, 180);
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Bundle mSavedInstanceState;
    private SensorEventHelper mSensorHelper;
    private boolean mFirstFix = false;
    private Marker mLocMarker;
    private ImageView iv_startrun_gpsstate;
    private int currentGpsState = AMapLocation.GPS_ACCURACY_UNKNOWN;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_run);
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mv_startrun_map.onSaveInstanceState(outState);
    }


    @Override
    protected void initView() {
        setCenterText("开始运动");
        setLeftImage(R.drawable.back_hei);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mv_startrun_map = (MapView) findViewById(R.id.mv_startrun_map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        //mv_startrun_map.onCreate(savedInstanceState);
        mv_startrun_map.onCreate(mSavedInstanceState);

        iv_startrun_gpsstate = (ImageView) findViewById(R.id.iv_startrun_gpsstate);

        mMap = mv_startrun_map.getMap();

    }

    @Override
    protected void initData() {
        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }

    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            chooseOpenGps();
        } else {
            // 弹出Toast
//          Toast.makeText(TrainDetailsActivity.this, "GPS is ready",
//                  Toast.LENGTH_LONG).show();
//          // 弹出对话框
//          new AlertDialog.Builder(this).setMessage("GPS is ready")
//                  .setPositiveButton("OK", null).show();
        }
    }

    private void chooseOpenGps() {
        bottomSheetDialog = new BottomSheetDialog(StartRunActivity.this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.choose_opengps_dailog, null);

        bottomSheetDialog.setContentView(inflate);
        Window window = bottomSheetDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.opengpsdialogstyle);  //添加动画
        bottomSheetDialog.show();

        TextView bt_opengps_cancel = (TextView) inflate.findViewById(R.id.bt_opengps_cancel);
        TextView bt_opengps_ok = (TextView) inflate.findViewById(R.id.bt_opengps_ok);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        bt_opengps_cancel.setOnClickListener(myOnClickListener);
        bt_opengps_ok.setOnClickListener(myOnClickListener);


    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_opengps_cancel:
                    bottomSheetDialog.dismiss();

                    break;
                case R.id.bt_opengps_ok:
                    bottomSheetDialog.dismiss();
                    //跳到设置页面
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                    break;
            }
        }
    }

    public void startRun(View view) {
        //initGPS();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            chooseOpenGps();
        }
        else {
            startActivity(new Intent(StartRunActivity.this,RunningActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv_startrun_map.onResume();   //地图
        if (mSensorHelper != null) {   //方向传感器，注册
            mSensorHelper.registerSensorListener();
        }

        // 设置定位监听
        mMap.setLocationSource(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mv_startrun_map.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            //mSensorHelper = null;
        }
        deactivate();  //关闭定位
        mFirstFix = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mv_startrun_map.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }


    /*定位回调
    *
    * 通过添加Marker的方式来再地图上显示位置，当改变手机方向时通过传感器来改变箭头方向
    *
    * */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                Log.i(TAG,"onLocationChanged:"+aMapLocation.toString());

                LatLng location = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                int gpsAccuracyStatus = aMapLocation.getGpsAccuracyStatus();
                //获取卫星信号强度，仅在gps定位时有效,值为#GPS_ACCURACY_BAD，#GPS_ACCURACY_GOOD，#GPS_ACCURACY_UNKNOWN
                Log.i(TAG,"gpsAccuracyStatus:"+gpsAccuracyStatus);
                if (aMapLocation.getLocationType()==1 && gpsAccuracyStatus!=currentGpsState){
                    currentGpsState = gpsAccuracyStatus;
                    if (currentGpsState==AMapLocation.GPS_ACCURACY_UNKNOWN){
                        //未知
                    }
                    else if (currentGpsState==AMapLocation.GPS_ACCURACY_BAD){
                        //信号差
                        iv_startrun_gpsstate.setImageResource(R.drawable.gps1);
                    }
                    else if (currentGpsState==AMapLocation.GPS_ACCURACY_GOOD){
                        //良好
                        iv_startrun_gpsstate.setImageResource(R.drawable.gps3);

                    }
                }



                if (!mFirstFix) {
                    mFirstFix = true;
                    //addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                } else {
                    //mCircle.setCenter(location);
                    //mCircle.setRadius(amapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                    mMap.moveCamera(CameraUpdateFactory.changeLatLng(location));
                }
                /*float zoom = mMap.getCameraPosition().zoom;
                if (zoom==10.0){
                    zoom=18;
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));*/

                /*mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));*/
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setGpsFirst(true);
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setSensorEnable(true);
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

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(), R.drawable.map_location);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = mMap.addMarker(options);
        mLocMarker.setTitle("mylocation");
    }


}
