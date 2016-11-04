package com.random.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙数据的读取，输出，维护一张蓝牙关系表
 */
public class ChatClient implements Runnable {
    private final BluetoothDevice device;
    private BluetoothSocket socket;
    private List<Handler> list = new ArrayList<>();

    public ChatClient(BluetoothSocket socket) {
        this.socket = socket;
        device = socket.getRemoteDevice();
        new Thread(this).start();
    }

    /**
     * 客户端发送信息
     *
     * @param msg
     */
    public void send(final String msg) {
        new Thread() {
            @Override
            public void run() {
                try {
                    DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                    os.writeUTF(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 添加蓝牙配对
     *
     * @param handler
     */
    public void register(Handler handler) {
        list.add(handler);
    }

    public void unregister(Handler handler) {
        list.remove(handler);
    }

    /**
     * 将信息转发给所有连接的蓝牙设备
     */
    @Override
    public void run() {
        try {
            //获取输入信息
            DataInputStream is = new DataInputStream(socket.getInputStream());
            String msg;

            while ((msg = is.readUTF()) != null) {
                Message message = Message.obtain();
                message.what = 0;
                message.obj = device.getName() + ":" + msg;

                // 将信息转发给所有连接的蓝牙设备
                for (Handler h : list) {
                    h.sendMessage(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
