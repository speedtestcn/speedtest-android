package com.speedtest.speedtest_android_sdk_v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class UIActivity extends AppCompatActivity {
    private static final String TAG = "UIActivity";
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double download = intent.getDoubleExtra("download", 0);
            double upload = intent.getDoubleExtra("upload", 0);
            double ping = intent.getDoubleExtra("ping", 0);
            double jitter = intent.getDoubleExtra("jitter", 0);
            double pckLoss = intent.getDoubleExtra("pckLoss", 0);
            String wifiName = intent.getStringExtra("wifiName");

            Log.d(TAG, "onReceive: download -> " + download);
            Log.d(TAG, "onReceive: upload -> " + upload);
            Log.d(TAG, "onReceive: ping -> " + ping);
            Log.d(TAG, "onReceive: jitter -> " + jitter);
            Log.d(TAG, "onReceive: pckLoss -> " + pckLoss);
            Log.d(TAG, "onReceive: wifiName -> " + wifiName);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);
        IntentFilter intentFilter = new IntentFilter("cn.speedtest.sdk");
        registerReceiver(mBroadcastReceiver, intentFilter);
//      SpeedtestInterface.setUnit(this, "MB/s");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
