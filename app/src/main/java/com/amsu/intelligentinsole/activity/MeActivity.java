package com.amsu.intelligentinsole.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.amsu.intelligentinsole.R;
import com.amsu.intelligentinsole.adapter.SoleDeviceAdapter;
import com.amsu.intelligentinsole.bean.SoleDevice;
import com.amsu.intelligentinsole.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MeActivity extends BaseActivity {

    private SoleDeviceAdapter soleDeviceAdapter;
    List<SoleDevice> deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
    }

    @Override
    protected void initView() {
        setHeadBackgroudColor("#ff7e00");
        setCenterText("我的");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView lv_device_devicelist = (ListView) findViewById(R.id.lv_device_devicelist);
        deviceList = new ArrayList<>();
        //deviceList.add(new SoleDevice("智能运动鞋1","ss"));
        soleDeviceAdapter = new SoleDeviceAdapter(this, deviceList);

        lv_device_devicelist.setAdapter(soleDeviceAdapter);

        RelativeLayout rl_device_adddevice = (RelativeLayout) findViewById(R.id.rl_device_adddevice);

        rl_device_adddevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeActivity.this,SearchDevicehActivity.class);
                startActivityForResult(intent,130);
            }
        });


    }

    @Override
    protected void initData() {

    }


}
