package com.random.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

/**
 * 通过广播事件，搜索蓝牙，并加入到ListView中
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private DeviceAdapter adapter;

    public BluetoothReceiver(DeviceAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        adapter.add(device);
    }
}
