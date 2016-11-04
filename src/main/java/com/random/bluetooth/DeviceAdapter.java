package com.random.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * ListView样式
 */
public class DeviceAdapter extends BaseAdapter {
    private Context context;
    private List<BluetoothDevice> list;

    public DeviceAdapter(Context context, List<BluetoothDevice> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        BluetoothDevice device = list.get(position);

        // 获取蓝牙名称
        if (TextUtils.isEmpty(device.getName())) {
            holder.name.setText("没有名字");
        } else {
            holder.name.setText(device.getName());
        }
        // 获取蓝牙Mac地址
        holder.address.setText(device.getAddress());
        switch (device.getBondState()) {
            //已经绑定过的用黑色
            case BluetoothDevice.BOND_BONDED:
                holder.name.setTextColor(Color.BLACK);
                break;
            //尚未绑定使用红色
            case BluetoothDevice.BOND_NONE:
                holder.name.setTextColor(Color.RED);
                break;
        }
        return convertView;
    }

    public static class ViewHolder {
        private TextView name;
        private TextView address;

        public ViewHolder(View itemView) {
            name = ((TextView) itemView.findViewById(R.id.item_name));
            address = ((TextView) itemView.findViewById(R.id.item_address));
        }
    }

    /**
     * 添加搜索到的蓝牙设备
     *
     * @param device
     */
    public void add(BluetoothDevice device) {
        if (!list.contains(device)) {
            list.add(device);
            notifyDataSetChanged();
        }
    }
}
