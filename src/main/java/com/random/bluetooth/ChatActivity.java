package com.random.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothDevice device;
    private ChatClient client;
    private EditText edit;
    private ArrayAdapter<String> adapter;
    private String name;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter.add(msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1.获取传递进来的蓝牙设备参数
        device = getIntent().getParcelableExtra("device");
        //获取设备名称,设置标题
        if (TextUtils.isEmpty(device.getName())) {
            setTitle("没有名字");
        } else {
            setTitle(device.getName());
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 2.获取蓝牙客户端对象,等待接收信息
        client = SocketThread.getClient(device);
        if (client == null) {
            Toast.makeText(this, "此设备不支持聊天", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 控件初始化，并设置样式
        edit = ((EditText) findViewById(R.id.chat_edit));
        ListView listView = (ListView) findViewById(R.id.chat_list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1); // ListView样式
        listView.setAdapter(adapter);
        // 获取本设备的蓝牙名称
        name = BluetoothAdapter.getDefaultAdapter().getName();
        // 3.点击事件,蓝牙发送消息
        findViewById(R.id.chat_send).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /**
     * 客户端发送消息
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_send:
                String s = edit.getText().toString();
                if (!TextUtils.isEmpty(s)) {
                    client.send(s); // 客户端发送消息
                    adapter.add(name + ":" + s); //listView增加样式
                    edit.setText("");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.unregister(handler);
    }
}
