package com.speedtest.speedtest_android_sdk_v2;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.speedmanager.speedtest_api.http.bean.ServerListData;
import com.speedmanager.speedtest_api.http.bean.ServerListsBean;
import com.speedmanager.speedtest_core.unit.SpeedUnit;

import java.util.ArrayList;
import java.util.List;

import cn.speedtest.speedtest_sdk.SpeedtestInterface;
import cn.speedtest.speedtest_sdk.callback.GetNodeListCallback;
import cn.speedtest.speedtest_sdk.callback.PingCallback;
import cn.speedtest.speedtest_sdk.callback.SpeedtestCallback;
import cn.speedtest.speedtest_sdk.data.PingResultData;
import io.reactivex.disposables.Disposable;

public class NonUIActivity extends AppCompatActivity {
    private static final String TAG = "NonUIActivity";
    private Button startSpeedBtn;
    private Button startSpeedBtnMbs;
    private Button startSpeedBtnKbs;
    private Button abortBtn;
    private Button btnSkipToNodesSelect;
    private TextView editText;
    private TextView editUploadText;
    private TextView editPingText;
    private TextView editPingLossText;
    private TextView txtGetNodeText;
    private EditText etHoldValue;
    private Switch switchAutoSpeed;
    private Switch switchFastSpeed;
    private Spinner spinnerSelectNode;
    private Disposable getNodeInfoDisposable;
    private List<String> nodeListId;
    private ArrayAdapter<String> mArrayAdapter;
    private List<ServerListsBean> serverListsBeans;
    private SpeedtestInterface speedtestInterface;
    private int downCount;
    private int upCount;
    private PingResultData mPingResultData;

    private MyHandler mHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noui);
        startSpeedBtn = findViewById(R.id.btn_start_mbps);
        startSpeedBtnMbs = findViewById(R.id.btn_start_mbs);
        startSpeedBtnKbs = findViewById(R.id.btn_start_kbs);
        abortBtn = findViewById(R.id.btn_abort);
        editText = findViewById(R.id.edt_result);
        editUploadText = findViewById(R.id.edt_upload_result);
        editPingText = findViewById(R.id.edt_ping_result);
        editPingLossText = findViewById(R.id.edt_ping_loss_result);
        txtGetNodeText = findViewById(R.id.edt_select_node);
        etHoldValue = findViewById(R.id.et_hold_value);
        switchAutoSpeed = findViewById(R.id.switch_auto_speed);
        switchFastSpeed = findViewById(R.id.switch_fast_speed);
        spinnerSelectNode = findViewById(R.id.spinner_select_node);
        nodeListId = new ArrayList<String>();
        nodeListId.add("选择测速节点");
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, nodeListId);
        spinnerSelectNode.setAdapter(mArrayAdapter);
        spinnerSelectNode.setSelection(0);

        editText.setMovementMethod(ScrollingMovementMethod.getInstance());
        speedtestInterface = SpeedtestInterface.getSDK(NonUIActivity.this).holdValue(1).autoDown(true).fastSpeed(true);
        startSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedtestInterface.holdValue(Double.valueOf(TextUtils.isEmpty(etHoldValue.getText().toString())?"0":etHoldValue.getText().toString()))
                    .autoDown(switchAutoSpeed.isChecked()).fastSpeed(switchFastSpeed.isChecked());
                downCount = 0;
                upCount = 0;
                speedtestInterface.startSpeedTest(pingCallback,downloadCallback,uploadCallback,SpeedUnit.Mbitps);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final ServerListsBean bean = speedtestInterface.getSelectNode();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bean == null) {
                                    txtGetNodeText.setText("测速节点：未设置测速节点");
                                } else {
                                    txtGetNodeText.setText("测速节点："+speedtestInterface.getSelectNode().toString());
                                }
                            }
                        });
                    }
                });

            }
        });

        startSpeedBtnMbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedtestInterface.holdValue(Double.valueOf(TextUtils.isEmpty(etHoldValue.getText().toString())?"0":etHoldValue.getText().toString()))
                    .autoDown(switchAutoSpeed.isChecked()).fastSpeed(switchFastSpeed.isChecked());
                speedtestInterface.startSpeedTest(pingCallback,downloadCallback,uploadCallback,SpeedUnit.MBps);
            }
        });

        startSpeedBtnKbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedtestInterface.holdValue(Double.valueOf(TextUtils.isEmpty(etHoldValue.getText().toString())?"0":etHoldValue.getText().toString()))
                    .autoDown(switchAutoSpeed.isChecked()).fastSpeed(switchFastSpeed.isChecked());
                speedtestInterface.startSpeedTest(pingCallback,downloadCallback,uploadCallback,SpeedUnit.KBps);
            }
        });

        abortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedtestInterface.abort();
            }
        });

        // 手动设置测速节点
        speedtestInterface.getNodeList(1, new GetNodeListCallback() {
            @Override
            public void onResult(ServerListData serverListData) {
                if (serverListData == null) {
                    return;
                }
                serverListsBeans = serverListData.getData();
                for (ServerListsBean bean : serverListsBeans) {
                    nodeListId.add(String.valueOf(bean.getId()) + "-" + bean.getSponsor() + "-" + bean.getOperator());
                }
                mArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });

        spinnerSelectNode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    speedtestInterface.setSelectNode(serverListsBeans.get(position - 1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private PingCallback pingCallback = new PingCallback() {
        @Override
        public void onResult(PingResultData pingResultData) {
            mPingResultData = pingResultData;
            Message msg = mHandler.obtainMessage();
            mHandler.sendEmptyMessage(0);
            //editPingText.setText("Ping Result:---------------------"+"\n");
            //LogUtil.e("Ping Result:" + pingResultData.toString());
            //editPingText.setText(pingResultData.toString());

        }

        @Override
        public void onError(Throwable throwable) {
            //editPingText.setText("Ping Occur Error:---------------------"+"\n"+throwable.getMessage());
            Message msg = mHandler.obtainMessage();
            msg.obj = throwable;
            msg.what = 1;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onPckLoss(double pckLoss) {
            //editPingText.append("丢包：-----------" + pckLoss +"\n");
            Message msg = mHandler.obtainMessage();
            msg.what = 2;
            msg.obj = pckLoss;
            mHandler.sendMessage(msg);
        }
    };

    private SpeedtestCallback downloadCallback = new SpeedtestCallback() {

        @Override
        public void onStart() {
            //editText.setText("Start SpeedTest Download:---------------------"+"\n");
            Message msg = mHandler.obtainMessage();
            mHandler.sendEmptyMessage(3);
        }

        @Override
        public void onProcess(double mbps) {
            //editText.append(mbps + "\n");
            downCount++;
            Message msg = mHandler.obtainMessage();
            msg.what = 4;
            msg.obj = mbps;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onEnd(double mbps) {
            //editText.setText("End SpeedTest Download:---------------------次数："+ downCount +"\n"+ mbps);
            Message msg = mHandler.obtainMessage();
            msg.what = 5;
            msg.obj = mbps;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onError(Throwable throwable) {
            //editText.setText("SpeedTest Download Occur Error:---------------------"+"\n");
            Message msg = mHandler.obtainMessage();
            msg.what = 6;
            msg.obj = throwable;
            mHandler.sendMessage(msg);
        }
    };

    private SpeedtestCallback uploadCallback = new SpeedtestCallback() {

        @Override
        public void onStart() {
            //editUploadText.setText("Start SpeedTest Upload:---------------------"+"\n");
            Message msg = mHandler.obtainMessage();
            mHandler.sendEmptyMessage(7);
        }

        @Override
        public void onProcess(double mbps) {
            //editUploadText.append(mbps + "\n");
            upCount++;
            Message msg = mHandler.obtainMessage();
            msg.what = 8;
            msg.obj = mbps;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onEnd(double mbps) {
            //editUploadText.setText("End SpeedTest Upload:---------------------次数：" + upCount +"\n"+mbps);
            Message msg = mHandler.obtainMessage();
            msg.what = 9;
            msg.obj = mbps;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onError(Throwable throwable) {
            //editUploadText.setText("SpeedTest Upload Occur Error:---------------------"+"\n");
            Message msg = mHandler.obtainMessage();
            msg.what = 10;
            msg.obj = throwable;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    editPingText.setText("Ping Result:---------------------"+"\n" + mPingResultData.toString());
                    break;
                case 1:
                    editPingText.setText("Ping Occur Error:---------------------"+"\n"+((Throwable)msg.obj).getMessage());
                    break;
                case 2:
                    editPingText.append("丢包：-----------" + msg.obj.toString() +"\n");
                    break;
                case 3:
                    editText.setText("Start SpeedTest Download:---------------------"+"\n");
                    break;
                case 4:
                    editText.append(msg.obj.toString() + "\n");
                    break;
                case 5:
                    editText.setText("End SpeedTest Download:---------------------次数："+ downCount +"\n" + msg.obj.toString());
                    break;
                case 6:
                    editText.setText("Download Occur Error:---------------------"+"\n"+((Throwable)msg.obj).getMessage());
                    break;
                case 7:
                    editUploadText.setText("Start SpeedTest Upload:---------------------"+"\n");
                    break;
                case 8:
                    editUploadText.append(msg.obj.toString() + "\n");
                    break;
                case 9:
                    editUploadText.setText("End SpeedTest Upload:---------------------次数："+ upCount +"\n" + msg.obj.toString());
                    break;
                case 10:
                    editUploadText.setText("Upload Occur Error:---------------------"+"\n"+((Throwable)msg.obj).getMessage());
                    break;
                default:
                    break;
            }
        }
    }
}
