package com.speedtest.speedtest_android_sdk_v2;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import cn.speedtest.speedtest_sdk.SpeedtestInterface;


public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        SpeedtestInterface.init(this, SDKConfig.APP_ID, SDKConfig.APP_KEY);
        SpeedtestInterface.setAutoRun(false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //Reflection.unseal(base);
    }
}
