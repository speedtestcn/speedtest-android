package com.speedtest.mobile_network_sdk_demo;

import android.app.Application;
import android.content.Context;

import com.speedtest.lib_auth.SpeedtestInterface;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SpeedtestInterface.init(this, SDKConfig.APP_ID, SDKConfig.APP_KEY);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
