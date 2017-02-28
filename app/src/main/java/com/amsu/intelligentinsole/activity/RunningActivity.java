package com.amsu.intelligentinsole.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.intelligentinsole.R;
import com.amsu.intelligentinsole.common.BaseActivity;
import com.amsu.intelligentinsole.util.RunTimerTaskUtil;

public class RunningActivity extends BaseActivity {

    private static final String TAG = "RunningActivity";
    private RelativeLayout rl_run_stop;
    private RelativeLayout rl_run_continue;
    private TextView tv_run_time;
    private RunTimerTaskUtil runTimerTaskUtil;
    public static Activity activity;

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
                Log.i(TAG,"formatTime:"+formatTime);
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

    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_run_lock:


                    break;
                case R.id.iv_run_map:


                    break;
                case R.id.tv_run_continue:
                    rl_run_stop.setVisibility(View.VISIBLE);
                    rl_run_continue.setVisibility(View.GONE);
                    if (runTimerTaskUtil!=null){
                        runTimerTaskUtil.startTime();
                    }

                    break;
                case R.id.tv_run_analysis:
                    startActivity(new Intent(RunningActivity.this,SportAnalysisActivity.class));

                    break;
                case R.id.tv_run_end:
                    if (runTimerTaskUtil!=null){
                        runTimerTaskUtil.destoryTime();
                        runTimerTaskUtil = null;
                    }

                    break;


            }
        }
    }
}
