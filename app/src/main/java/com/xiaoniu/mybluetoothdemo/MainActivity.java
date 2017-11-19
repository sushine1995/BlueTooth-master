package com.xiaoniu.mybluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoniu.mybluetoothdemo.Thread.ConnectThread;
import com.xiaoniu.mybluetoothdemo.adapter.BlueToothDeviceAdapter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothAdapter bTAdatper;
    private BlueToothDeviceAdapter adapter;
    private static final String TAG = "MainActivity";
    private TextView text_state;
    private static final String NAME = "BT_DEMO";
    private static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private ConnectThread connectThread;
    private BTApplication mApp;
    private Intent intent;
    private Handler mHandler;
    private boolean isConnected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApp = (BTApplication)this.getApplication();
        initView();
        intent = new Intent();

        intent.setClass(this, RecvActivity.class);
        bTAdatper = BluetoothAdapter.getDefaultAdapter();
        initReceiver();
        ListenerThread listenerThread = new ListenerThread();
        listenerThread.start();
    }

    private void initView() {
        findViewById(R.id.btn_openBT).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.btn_image).setOnClickListener(this);
        text_state = (TextView) findViewById(R.id.text_state);
        TextView text_msg = (TextView) findViewById(R.id.text_msg);
        ListView listView = (ListView) findViewById(R.id.listView);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0){
                    text_state.setText(getResources().getString(R.string.connect_error));
                    isConnected=false;
                }else{     text_state.setText(getResources().getString(R.string.connect_success));
                    //intent.putExtra("connect", connectThread);
                            isConnected=true;
                    //startActivity(intent);
                }
            }
        };
        adapter = new BlueToothDeviceAdapter(getApplicationContext(), R.layout.bluetooth_device_list_item);
        //1115
        assert listView != null;

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (bTAdatper.isDiscovering()) {
                    bTAdatper.cancelDiscovery();
                }
                BluetoothDevice device = adapter.getItem(position);
                //连接设备
                connectDevice(device);
            }
        });
    }

    private void initReceiver() {
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_openBT:
                openBlueTooth();
                break;
            case R.id.btn_search:
                if(!bTAdatper.isEnabled()){
                    openBlueTooth();
                }
                searchDevices();
                break;
            case R.id.btn_image:
                if(!bTAdatper.isEnabled()){
                    openBlueTooth();
                    searchDevices();
                }
                if (isConnected) {
                    startActivity(intent);
                }
                else Toast.makeText(MainActivity.this,"未连接设备",Toast.LENGTH_SHORT).show();
                    break;
        }
    }

    /**
     * 开启蓝牙
     */
    private void openBlueTooth() {
        if (bTAdatper == null) {
            Toast.makeText(this, "当前设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
        }
        if (!bTAdatper.isEnabled()) {
           /* Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(i);*/
            bTAdatper.enable();
        }
        //开启被其它蓝牙设备发现的功能
        if (bTAdatper.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //设置为一直开启
            i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(i);
        }
    }

    /**
     * 搜索蓝牙设备
     */
    private void searchDevices() {
        if (bTAdatper.isDiscovering()) {
            bTAdatper.cancelDiscovery();
        }
        getBoundedDevices();
        bTAdatper.startDiscovery();
    }

    /**
     * 获取已经配对过的设备
     */
    private void getBoundedDevices() {
        //获取已经配对过的设备
        Set<BluetoothDevice> pairedDevices = bTAdatper.getBondedDevices();
        //将其添加到设备列表中
        adapter.clear();    //先清空，避免重复添加
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                adapter.add(device);
            }
        }
    }

    /**
     * 连接蓝牙设备
     */
    private void connectDevice(BluetoothDevice device) {
        text_state.setText(getResources().getString(R.string.connecting));
        try {
            //创建Socket
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(BT_UUID);
            //启动连接线程

            mApp.conncet = new ConnectThread(socket, true, mHandler);
            mApp.conncet.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消搜索
        if (bTAdatper != null && bTAdatper.isDiscovering()) {
            bTAdatper.cancelDiscovery();
        }
        //注销BroadcastReceiver，防止资源泄露
        unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //避免重复添加已经绑定过的设备
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    adapter.add(device);
                    adapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(MainActivity.this, "开始搜索", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(MainActivity.this, "搜索完毕", Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * 监听线程
     */
    private class ListenerThread extends Thread {

        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;

        @Override
        public void run() {
            try {
                serverSocket = bTAdatper.listenUsingRfcommWithServiceRecord(
                        NAME, BT_UUID);
                while (true) {
                    //线程阻塞，等待别的设备连接
                    socket = serverSocket.accept();
                    text_state.post(new Runnable() {
                        @Override
                        public void run() {
                            text_state.setText(getResources().getString(R.string.connecting));
                        }
                    });
                    mApp.conncet = new ConnectThread(socket, false, mHandler);
                    mApp.conncet.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
