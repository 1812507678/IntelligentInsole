package com.amsu.intelligentinsole.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amsu.intelligentinsole.R;
import com.amsu.intelligentinsole.common.BaseActivity;
import com.amsu.intelligentinsole.map.Util;
import com.amsu.intelligentinsole.util.MyUtil;
import com.amsu.intelligentinsole.view.HeightCurveView;

public class SportAnalysisActivity extends BaseActivity {

    private HeightCurveView hv_analysis_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_analysis);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        setHeadBackgroudColor("#37343d");
        setCenterTextColor("#FFFFFF");
        setCenterText("中途分析");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ScrollView sv_anal_resullt = (ScrollView) findViewById(R.id.sv_anal_resullt);
        final LinearLayout ll_dump = (LinearLayout) findViewById(R.id.ll_dump);

        //ScrollView设置滑动监听
        sv_anal_resullt.setOnTouchListener(new View.OnTouchListener() {
            float startY;
            boolean isShow = true;
            boolean isNeedShow = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action==MotionEvent.ACTION_DOWN){
                    startY = event.getY();
                }
                else if (action==MotionEvent.ACTION_MOVE){

                    float y = event.getY();
                    if (startY-y > MyUtil.dp2px(SportAnalysisActivity.this,5)){
                        //向下滑动
                        if (isShow){
                            ll_dump.setVisibility(View.GONE);
                            isShow = false;
                        }
                    }
                    else if (y-startY> MyUtil.dp2px(SportAnalysisActivity.this,5)){
                        //向上滑动
                        if (!isShow){
                            isNeedShow = true;
                        }
                    }
                }
                else if (action==MotionEvent.ACTION_UP){
                    if (isNeedShow && !isShow){
                        ll_dump.setVisibility(View.VISIBLE);
                        sv_anal_resullt.setVisibility(View.VISIBLE);
                        isShow = true;
                        isNeedShow = false;
                    }
                }
                return false;
            }
        });

        TextView tv_analy_consistencyleft = (TextView) findViewById(R.id.tv_analy_consistencyleft);
        TextView tv_analy_consistencyright = (TextView) findViewById(R.id.tv_analy_consistencyright);
        TextView tv_analy_ankleleft = (TextView) findViewById(R.id.tv_analy_ankleleft);
        TextView tv_analy_ankleright = (TextView) findViewById(R.id.tv_analy_ankleright);
        TextView tv_analy_toeoutleft = (TextView) findViewById(R.id.tv_analy_toeoutleft);
        TextView tv_analy_toeoutright = (TextView) findViewById(R.id.tv_analy_toeoutright);

        hv_analysis_line = (HeightCurveView) findViewById(R.id.hv_analysis_line);


    }

    @Override
    protected void initData() {
        int[] data = new int[50];

        for (int i=0;i<data.length;i++){
            data[i] = (int) (Math.random()*(65-60) + 60);
        }
        hv_analysis_line.setData(data,50);


    }

    public void stopRunning(View view) {
        long recordID = Util.saveRecord(RunningActivity.record.getPathline(), RunningActivity.record.getDate(), this, RunningActivity.mStartTime);

        Intent intent = new Intent(SportAnalysisActivity.this, SportFinishActivity.class);
        if (recordID!=-1){
            intent.putExtra("createrecord",recordID);
        }
        startActivity(intent);
        RunningActivity.mlocationClient.stopLocation();
        RunningActivity.activity.finish();
        finish();
    }

    public void continueRunning(View view) {
        finish();
    }
}
