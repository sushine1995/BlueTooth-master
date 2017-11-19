package com.xiaoniu.mybluetoothdemo;

import android.app.Application;

import com.xiaoniu.mybluetoothdemo.Thread.ConnectThread;

/**
 * Created by hi on 2017/10/22.
 */

public class BTApplication extends Application{
    public ConnectThread conncet;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
