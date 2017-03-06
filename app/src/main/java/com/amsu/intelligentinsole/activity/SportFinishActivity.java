package com.amsu.intelligentinsole.activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.intelligentinsole.R;
import com.amsu.intelligentinsole.adapter.ViewPageFragmentAdapter;
import com.amsu.intelligentinsole.common.BaseActivity;
import com.amsu.intelligentinsole.fragment.SportDetailFragment;
import com.amsu.intelligentinsole.fragment.SportFootFragment;
import com.amsu.intelligentinsole.fragment.SportTrailFragment;
import com.amsu.intelligentinsole.util.MyUtil;

import java.util.ArrayList;
import java.util.List;

public class SportFinishActivity extends BaseActivity {
    private List<Fragment> fragmentList;
    private ViewPager vp_finish_content;
    private View v_finish_select;
    private TextView tv_finish_trail;
    private TextView tv_finish_detial;
    private TextView tv_finish_foot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_finish);
    }

    @Override
    protected void initView() {
        setHeadBackgroudColor("#37343d");
        setCenterTextColor("#FFFFFF");
        setCenterText("运动结束");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        vp_finish_content = (ViewPager) findViewById(R.id.vp_finish_content);
        v_finish_select = findViewById(R.id.v_finish_select);
        tv_finish_trail = (TextView) findViewById(R.id.tv_finish_trail);
        tv_finish_detial = (TextView) findViewById(R.id.tv_finish_detial);
        tv_finish_foot = (TextView) findViewById(R.id.tv_finish_foot);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        tv_finish_trail.setOnClickListener(myOnClickListener);
        tv_finish_detial.setOnClickListener(myOnClickListener);
        tv_finish_foot.setOnClickListener(myOnClickListener);

        fragmentList = new ArrayList<>();
        fragmentList.add(new SportTrailFragment());
        fragmentList.add(new SportDetailFragment());
        fragmentList.add(new SportFootFragment());

        vp_finish_content.setAdapter(new ViewPageFragmentAdapter(getSupportFragmentManager(),fragmentList));
        vp_finish_content.setOffscreenPageLimit(2);

    }

    @Override
    protected void initData() {


    }


    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_finish_trail:
                    setViewPageItem(0,vp_finish_content.getCurrentItem());
                    break;
                case R.id.tv_finish_detial:
                    setViewPageItem(1,vp_finish_content.getCurrentItem());
                    break;
                case R.id.tv_finish_foot:
                    setViewPageItem(2,vp_finish_content.getCurrentItem());
                    break;
            }
        }
    }

    //点击时设置选中条目
    public void setViewPageItem(int viewPageItem,int currentItem) {
        if (currentItem==viewPageItem){
            return;
        }
        vp_finish_content.setCurrentItem(viewPageItem);
        float oneTableWidth = MyUtil.getScreeenWidth(this)/3;
        RelativeLayout.LayoutParams layoutParams =   (RelativeLayout.LayoutParams) v_finish_select.getLayoutParams();
        int floatWidth= (int) (oneTableWidth*viewPageItem);  //view向左的偏移量
        layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
        v_finish_select.setLayoutParams(layoutParams);



    }



}
