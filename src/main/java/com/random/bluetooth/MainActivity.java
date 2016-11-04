package com.random.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DeviceAdapter lvAdapter; // ListView样式
    private BluetoothReceiver receiver; // 蓝牙广播
    private BluetoothAdapter mAdapter; // 本地蓝牙适配器

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 控件初始化
        ListView listView = (ListView) findViewById(R.id.list);

        //1.获取本地蓝牙适配器
        mAdapter = BluetoothAdapter.getDefaultAdapter();

        //2.检查设备是否支持蓝牙通信模块
        if (mAdapter == null) {//设备不支持蓝牙硬件

            Toast.makeText(this, "本设备没有蓝牙模块", Toast.LENGTH_SHORT).show();
            finish();
        } else {// 设备支持蓝牙通信

            //获取所有配对过的设备,并显示在ListView中
            List<BluetoothDevice> list = new ArrayList<>(mAdapter.getBondedDevices());
            lvAdapter = new DeviceAdapter(this, list);
            listView.setAdapter(lvAdapter);

            //3.开启蓝牙设备
            if (!mAdapter.isEnabled()) {
                //请求开启蓝牙
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 0);
            }

            // 4.搜索蓝牙,开启蓝牙服务端
            startBluetooth();

            //5.添加点击事件
            listView.setOnItemClickListener(this);
        }
    }

    /**
     * 通过广播开启蓝牙,并开启服务端，等待客户端连接
     */
    private void startBluetooth() {

        // 搜索蓝牙设备
        mAdapter.startDiscovery();

        // 动态注册广播事件过滤器
        receiver = new BluetoothReceiver(lvAdapter);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        // 开启蓝牙连接
        new SocketThread(handler).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    /**
     * 蓝牙开启回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                startBluetooth();
                Toast.makeText(this, "蓝牙开启成功", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "蓝牙开启失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 点击进入聊天界面
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = (BluetoothDevice) parent.getAdapter().getItem(position);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("device", device);
        startActivity(intent);
    }
}
