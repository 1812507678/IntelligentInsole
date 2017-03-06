package com.amsu.intelligentinsole.fragment;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amsu.intelligentinsole.R;
import com.amsu.intelligentinsole.bean.MyMapView;
import com.amsu.intelligentinsole.bean.PathRecord;
import com.amsu.intelligentinsole.map.DbAdapter;
import com.amsu.intelligentinsole.map.Util;
import com.amsu.intelligentinsole.util.MyUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SportTrailFragment extends Fragment implements AMap.OnMapLoadedListener {

    private static final String TAG = "SportTrailFragment";
    private View inflate;
    private MyMapView mv_finish_map;
    private AMap mAMap;
    private List<LatLng> mOriginLatLngList;
    private Polyline mOriginPolyline;
    private Marker mOriginStartMarker;
    private Marker mOriginEndMarker;
    private TextView tv_finish_mileage;
    private TextView tv_finish_time;
    private TextView tv_finish_speed;
    private TextView tv_finish_consume;

    public SportTrailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflate = inflater.inflate(R.layout.fragment_sport_trail, container, false);


        initView(savedInstanceState);
        return inflate;
    }


    private void initView(Bundle savedInstanceState) {
        mv_finish_map = (MyMapView) inflate.findViewById(R.id.mv_finish_map);
        mv_finish_map.onCreate(savedInstanceState);// 此方法必须重写
        RadioGroup gv_finish_type = (RadioGroup) inflate.findViewById(R.id.gv_finish_type);
        RadioButton rb_finish_oringinal = (RadioButton) inflate.findViewById(R.id.rb_finish_oringinal);
        RadioButton rb_finish_grasp = (RadioButton) inflate.findViewById(R.id.rb_finish_grasp);

        tv_finish_mileage = (TextView) inflate.findViewById(R.id.tv_finish_mileage);
        tv_finish_time = (TextView) inflate.findViewById(R.id.tv_finish_time);
        tv_finish_speed = (TextView) inflate.findViewById(R.id.tv_finish_speed);
        tv_finish_consume = (TextView) inflate.findViewById(R.id.tv_finish_consume);

        MyOcClickListener myOcClickListener = new MyOcClickListener();
        rb_finish_oringinal.setOnClickListener(myOcClickListener);
        rb_finish_grasp.setOnClickListener(myOcClickListener);



        initMap();


        


    }

    /**
     * 轨迹数据初始化
     *
     */
    private void setupRecord() {
        // 轨迹纠偏初始化
        LBSTraceClient mTraceClient = new LBSTraceClient(getContext());
        Intent intent = getActivity().getIntent();
        if (intent!=null){
            long createrecord = intent.getLongExtra("createrecord", -1);
            if (createrecord!=-1){
                Log.i(TAG,"createrecord:"+createrecord);
                DbAdapter dbAdapter = new DbAdapter(getActivity());
                dbAdapter.open();
                PathRecord pathRecord = dbAdapter.queryRecordById((int) createrecord);
                dbAdapter.close();
                Log.i(TAG,"pathRecord:"+pathRecord.toString());
                // pathRecord:recordSize:103, distance:4.15064m, duration:206.922s
                int distance = (int) Float.parseFloat(pathRecord.getDistance());
                int duration = (int) Float.parseFloat(pathRecord.getDuration());

                int secend = (distance%1000)/10;
                String myDistance = distance/1000+"."+secend;
                if (secend==0) {
                    myDistance = distance/1000+".00";
                }
                tv_finish_mileage.setText(myDistance);

                int durationSecend = (duration%(60*60))/60;
                int durationThrid = (duration%(60*60))%60;
                String durationSecendString =(duration%(60*60))/60+"";
                String durationThridString =(duration%(60*60))%60+"";

                if (durationSecend<10) {
                    durationSecendString = "0"+durationSecendString;
                }
                if (durationThrid<10) {
                    durationThridString = "0"+durationThridString;
                }
                String myDuration = duration/(60*60)+":"+durationSecendString+":"+durationThridString;

                tv_finish_time.setText(myDuration);


                List<AMapLocation> recordList = pathRecord.getPathline();
                AMapLocation startLoc = pathRecord.getStartpoint();
                AMapLocation endLoc = pathRecord.getEndpoint();
                if (recordList == null || startLoc == null || endLoc == null) {
                    return;
                }
                LatLng startLatLng = new LatLng(startLoc.getLatitude(), startLoc.getLongitude());
                LatLng endLatLng = new LatLng(endLoc.getLatitude(), endLoc.getLongitude());
                mOriginLatLngList = Util.parseLatLngList(recordList);
                addOriginTrace(startLatLng, endLatLng, mOriginLatLngList);

               /* List<TraceLocation> mGraspTraceLocationList = Util.parseTraceLocationList(recordList);
                // 调用轨迹纠偏，将mGraspTraceLocationList进行轨迹纠偏处理
                mTraceClient.queryProcessedTrace(1, mGraspTraceLocationList, LBSTraceClient.TYPE_AMAP, this);*/
            }
        }


    }

    /**
     * 地图上添加原始轨迹线路及起终点、轨迹动画小人
     *
     * @param startPoint
     * @param endPoint
     * @param originList
     */
    private void addOriginTrace(LatLng startPoint, LatLng endPoint, List<LatLng> originList) {
        mOriginPolyline = mAMap.addPolyline(new PolylineOptions().color(Color.parseColor("#f17456")).width(getResources().getDimension(R.dimen.x8)).addAll(originList));
        mOriginStartMarker = mAMap.addMarker(new MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.iv_point_white)));
        mOriginEndMarker = mAMap.addMarker(new MarkerOptions().position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.iv_point_green)));

        try {
            /*
            * 返回CameraUpdate对象，这个对象包含一个经纬度限制的区域，并且是最大可能的缩放级别。
            你可以设置一个边距数值来控制插入区域与view的边框之间的空白距离。
            方法必须在地图初始化完成之后使用。*/
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(), 50));
            mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mOriginLatLngList.get(0)));  //只改变定图中心点位置，不改变缩放级别
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private LatLngBounds getBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (mOriginLatLngList == null) {
            return b.build();
        }
        for (int i = 0; i < mOriginLatLngList.size(); i++) {
            b.include(mOriginLatLngList.get(i));
        }
        return b.build();

    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mv_finish_map.getMap();
            mAMap.setOnMapLoadedListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mv_finish_map.onResume();   //地图
    }

    @Override
    public void onPause() {
        super.onPause();
        mv_finish_map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mv_finish_map.onDestroy();
    }

    @Override
    public void onMapLoaded() {
        setupRecord();
    }

    class MyOcClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rb_finish_oringinal:

                    break;

                case R.id.rb_finish_grasp:

                    break;

            }
        }
    }


}
