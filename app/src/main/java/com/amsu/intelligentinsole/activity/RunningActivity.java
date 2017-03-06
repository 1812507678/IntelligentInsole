package com.amsu.intelligentinsole.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.trace.TraceLocation;
import com.amsu.intelligentinsole.R;
import com.amsu.intelligentinsole.bean.PathRecord;
import com.amsu.intelligentinsole.common.BaseActivity;
import com.amsu.intelligentinsole.map.DbAdapter;
import com.amsu.intelligentinsole.map.Util;
import com.amsu.intelligentinsole.util.MyUtil;
import com.amsu.intelligentinsole.util.RunTimerTaskUtil;

import java.util.ArrayList;
import java.util.List;

public class RunningActivity extends BaseActivity implements
        AMapLocationListener {

    private static final String TAG = "RunningActivity";
    private RelativeLayout rl_run_stop;
    private RelativeLayout rl_run_continue;
    private TextView tv_run_time;
    private RunTimerTaskUtil runTimerTaskUtil;
    public static Activity activity;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private PathRecord record;    //存放未纠偏轨迹记录信息
    private List<TraceLocation> mTracelocationlist = new ArrayList<>();   //偏轨后轨迹
    private DbAdapter DbHepler;
    private long mStartTime;
    private long mEndTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
    }

    @Override
    protected void initView() {
        activity = this;

        TextView tv_run_mileage = (TextView) findViewById(R.id.tv_run_mileage);
        tv_run_time = (TextView) findViewById(R.id.tv_run_time);
        TextView tv_run_stride = (TextView) findViewById(R.id.tv_run_stride);
        TextView tv_run_avespeed = (TextView) findViewById(R.id.tv_run_avespeed);
        TextView tv_run_freqstride = (TextView) findViewById(R.id.tv_run_freqstride);
        TextView tv_run_maxspeed = (TextView) findViewById(R.id.tv_run_maxspeed);
        TextView tv_run_minspeed = (TextView) findViewById(R.id.tv_run_minspeed);
        TextView tv_run_kcal = (TextView) findViewById(R.id.tv_run_kcal);

        TextView tv_run_sptop = (TextView) findViewById(R.id.tv_run_sptop);
        TextView tv_run_lock = (TextView) findViewById(R.id.tv_run_lock);
        ImageView iv_run_map = (ImageView) findViewById(R.id.iv_run_map);
        TextView tv_run_continue = (TextView) findViewById(R.id.tv_run_continue);
        TextView tv_run_analysis = (TextView) findViewById(R.id.tv_run_analysis);
        TextView tv_run_end = (TextView) findViewById(R.id.tv_run_end);

        rl_run_stop = (RelativeLayout) findViewById(R.id.rl_run_stop);
        rl_run_continue = (RelativeLayout) findViewById(R.id.rl_run_continue);


        MyOnClickListener myOnClickListener = new MyOnClickListener();

        tv_run_lock.setOnClickListener(myOnClickListener);
        iv_run_map.setOnClickListener(myOnClickListener);
        tv_run_continue.setOnClickListener(myOnClickListener);
        tv_run_analysis.setOnClickListener(myOnClickListener);
        tv_run_end.setOnClickListener(myOnClickListener);

        runTimerTaskUtil = new RunTimerTaskUtil(this);
        runTimerTaskUtil.setOnTimeChangeListerner("HH:mm:ss", 1000, new RunTimerTaskUtil.OnTimeChangeListerner() {
            @Override
            public String onFormatStringTimeChange(String formatTime) {
                //Log.i(TAG,"formatTime:"+formatTime);
                tv_run_time.setText(formatTime);
                return null;
            }
        });
        runTimerTaskUtil.startTime();


        tv_run_sptop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                rl_run_stop.setVisibility(View.GONE);
                rl_run_continue.setVisibility(View.VISIBLE);
                runTimerTaskUtil.stopTime();
                return false;
            }
        });




    }

    @Override
    protected void initData() {
        mlocationClient = new AMapLocationClient(this);
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
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();

        if (record==null){
            record = new PathRecord();
            mStartTime = System.currentTimeMillis();
            record.setDate(MyUtil.getCueMapDate(mStartTime));
        }

    }



    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.i(TAG,"onLocationChanged:"+aMapLocation.toString());
        if (aMapLocation!=null){
            record.addpoint(aMapLocation);
            mTracelocationlist.add(Util.parseTraceLocation(aMapLocation));

        }

    }

    //保存数据到数据库
    protected long saveRecord(List<AMapLocation> list, String time) {
        if (list != null && list.size() > 0) {
            mEndTime = System.currentTimeMillis();
            DbHepler = new DbAdapter(this);
            DbHepler.open();
            String duration = getDuration();
            float distance = getDistance(list);
            String average = getAverage(distance);
            String pathlineSring = getPathLineString(list);
            AMapLocation firstLocaiton = list.get(0);
            AMapLocation lastLocaiton = list.get(list.size() - 1);
            String stratpoint = amapLocationToString(firstLocaiton);
            String endpoint = amapLocationToString(lastLocaiton);
            long createrecord = DbHepler.createrecord(String.valueOf(distance), duration, average, pathlineSring, stratpoint, endpoint, time);
            Log.i(TAG,"createrecord:"+createrecord);
            DbHepler.close();
            return createrecord;
        } else {
            /*Toast.makeText(RunTrailMapActivity.this, "没有记录到路径", Toast.LENGTH_SHORT)
                    .show();*/
            return -1;
        }

    }

    private String getDuration() {
        return String.valueOf((mEndTime - mStartTime) / 1000f);
    }

    private float getDistance(List<AMapLocation> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstpoint = list.get(i);
            AMapLocation secondpoint = list.get(i + 1);
            LatLng firstLatLng = new LatLng(firstpoint.getLatitude(), firstpoint.getLongitude());
            LatLng secondLatLng = new LatLng(secondpoint.getLatitude(), secondpoint.getLongitude());
            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng, secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }

    private String getAverage(float distance) {
        return String.valueOf(distance / (float) (mEndTime - mStartTime));
    }

    private String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    private String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_run_lock:


                    break;
                case R.id.iv_run_map:
                    startActivity(new Intent(RunningActivity.this,RunTrailMapActivity.class));

                    break;
                case R.id.tv_run_continue:
                    rl_run_stop.setVisibility(View.VISIBLE);
                    rl_run_continue.setVisibility(View.GONE);
                    if (runTimerTaskUtil!=null){
                        runTimerTaskUtil.startTime();
                    }

                    break;
                case R.id.tv_run_analysis:
                    Intent intent1 = new Intent(RunningActivity.this, SportAnalysisActivity.class);

                    startActivity(intent1);

                    break;
                case R.id.tv_run_end:
                    if (runTimerTaskUtil!=null){
                        runTimerTaskUtil.destoryTime();
                        runTimerTaskUtil = null;
                    }
                    long  createrecord = saveRecord(record.getPathline(), record.getDate());
                    mlocationClient.stopLocation();
                    Intent intent = new Intent(RunningActivity.this, SportFinishActivity.class);
                    if (createrecord!=-1){
                        intent.putExtra("createrecord",createrecord);
                    }
                    startActivity(intent);
                    finish();

                    break;


            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlocationClient.stopLocation();
    }
}
