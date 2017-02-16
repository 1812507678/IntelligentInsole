package com.amsu.intelligentinsole.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.amsu.intelligentinsole.R;
import com.amsu.intelligentinsole.bean.SoleDevice;

import java.util.List;

/**
 * Created by HP on 2016/12/23.
 */
public class SoleDeviceAdapter extends BaseAdapter{
    List<SoleDevice> SoleDeviceList ;
    Context context;

    public SoleDeviceAdapter(Context context, List<SoleDevice> SoleDeviceList) {
        this.SoleDeviceList = SoleDeviceList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return SoleDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SoleDevice SoleDevice = SoleDeviceList.get(position);
        View inflate = View.inflate(context, R.layout.item_soledevice_list, null);
        TextView tv_item_name = (TextView) inflate.findViewById(R.id.tv_item_name);


        tv_item_name.setText(SoleDevice.getName());
        return inflate;
    }
}
