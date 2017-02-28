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
import com.amsu.intelligentinsole.util.MyUtil;

public class SportAnalysisActivity extends BaseActivity {

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
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action==MotionEvent.ACTION_DOWN){
                    startY = event.getY();
                }
                else if (action==MotionEvent.ACTION_MOVE){
                    if (isShow){
                        float y = event.getY();
                        if (Math.abs(startY-y)> MyUtil.dp2px(SportAnalysisActivity.this,5)){
                            //滑动
                            ll_dump.setVisibility(View.GONE);
                            isShow = false;
                        }
                    }
                }
                else if (action==MotionEvent.ACTION_UP){
                    if (!isShow){
                        ll_dump.setVisibility(View.VISIBLE);
                        sv_anal_resullt.setVisibility(View.VISIBLE);

                        isShow = true;
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

    }

    @Override
    protected void initData() {

    }

    public void stopRunning(View view) {
        startActivity(new Intent(SportAnalysisActivity.this,RunTrailActivity.class));
        RunningActivity.activity.finish();
        finish();
    }

    public void continueRunning(View view) {
        finish();
    }
}
