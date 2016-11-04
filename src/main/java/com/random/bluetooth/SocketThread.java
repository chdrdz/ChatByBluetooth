package com.random.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 创建蓝牙客户端和服务端
 */
public class SocketThread extends Thread {

    private BluetoothServerSocket serverSocket;
    public static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static Map<BluetoothDevice, ChatClient> map = new HashMap<>();
    private static Handler handler;

    /**
     * 获取蓝牙客户端
     *
     * @param device
     * @return
     */
    public static ChatClient getClient(BluetoothDevice device) {
        ChatClient client = map.get(device);
        if (client == null) {
            try {
                //获取到一个Socket对象
                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
                //建立连接
                socket.connect();

                client = new ChatClient(socket);
                client.register(handler);
                map.put(device, client);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    /**
     * 新建一个蓝牙服务端
     *
     * @param handler
     */
    public SocketThread(Handler handler) {
        this.handler = handler;
        try {
            serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("", uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (serverSocket != null) {
            BluetoothSocket socket;
            try {
                while ((socket = serverSocket.accept()) != null) {
                    BluetoothDevice device = socket.getRemoteDevice();
                    ChatClient client = new ChatClient(socket);
                    client.register(handler);
                    map.put(device, client);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
