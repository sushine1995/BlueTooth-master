package com.xiaoniu.mybluetoothdemo.Thread;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**

 * Created by hi on 2017/10/21.
 */

public class ConnectThread extends Thread implements Serializable{
    private BluetoothSocket socket;
    private boolean activeConnect;
    private Handler mHandler;
    private Message conStatus;
    public InputStream inputStream;
    public OutputStream outputStream;


    public ConnectThread(BluetoothSocket socket, boolean connect, Handler mHandler) {
        this.socket = socket;
        this.activeConnect = connect;
        this.mHandler = mHandler;
        conStatus = mHandler.obtainMessage();
    }


    @Override
    public void run() {
        try {
            //如果是自动连接 则调用连接方法
            if (activeConnect) {
                socket.connect();
            }
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            conStatus.what = 1;
            mHandler.sendMessage(conStatus);
        } catch (IOException e) {
            e.printStackTrace();
            conStatus.what = 0;
            mHandler.sendMessage(conStatus);
        }
    }
}
