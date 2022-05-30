package com.speedtest.speedtest_sdk;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.speedtest.lib_api.http.bean.NodeListBean;
import com.speedtest.lib_auth.SdkThrowable;
import com.speedtest.lib_model.unit.SpeedUnit;
import com.speedtest.speedtest_sdk.SpeedInterface;
import com.speedtest.speedtest_sdk.callback.GetNodeListCallback;
import com.speedtest.speedtest_sdk.callback.PingCallback;
import com.speedtest.speedtest_sdk.callback.SpeedtestCallback;
import com.speedtest.speedtest_sdk.data.PingResultData;

import java.util.ArrayList;
import java.util.List;


public class NonUIActivity extends AppCompatActivity {
    private static final String TAG = "NonUIActivity";
    private Button startSpeedBtn;
    private Button startSpeedBtnMbs;
    private Button startSpeedBtnKbs;
    private Button addTestNodes;
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
    private List<String> nodeListId;
    private ArrayAdapter<String> mArrayAdapter;
    private SpeedInterface speedInterface;
    private int downCount;
    private int upCount;
    private PingResultData mPingResultData;
    private List<NodeListBean> mNodeListBeans;
    private int page = 1;

    private MyHandler mHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noui);
        startSpeedBtn = findViewById(R.id.btn_start_mbps);
        startSpeedBtnMbs = findViewById(R.id.btn_start_mbs);
        startSpeedBtnKbs = findViewById(R.id.btn_start_kbs);
        addTestNodes = findViewById(R.id.btn_add_nodes);
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
        mNodeListBeans = new ArrayList<NodeListBean>();
        nodeListId.add("选择测速节点");
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, nodeListId);
        spinnerSelectNode.setAdapter(mArrayAdapter);
        spinnerSelectNode.setSelection(0);

        editText.setMovementMethod(ScrollingMovementMethod.getInstance());

        speedInterface = SpeedInterface.getSDK().autoDown(true).fastSpeed(true);
        startSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedInterface.autoDown(switchAutoSpeed.isChecked()).fastSpeed(switchFastSpeed.isChecked());
                downCount = 0;
                upCount = 0;
                speedInterface.startSpeedTest(pingCallback,downloadCallback,uploadCallback,SpeedUnit.Mbitps);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final NodeListBean bean = speedInterface.getSelectNode();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bean == null) {
                                    txtGetNodeText.setText("测速节点：未设置测速节点");
                                } else {
                                    txtGetNodeText.setText("测速节点："+speedInterface.getSelectNode().toString());
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
                speedInterface
                    .autoDown(switchAutoSpeed.isChecked()).fastSpeed(switchFastSpeed.isChecked());
                speedInterface.startSpeedTest(pingCallback,downloadCallback,uploadCallback, SpeedUnit.MBps);
            }
        });

        startSpeedBtnKbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedInterface
                    .autoDown(switchAutoSpeed.isChecked()).fastSpeed(switchFastSpeed.isChecked());
                speedInterface.startSpeedTest(pingCallback,downloadCallback,uploadCallback,SpeedUnit.KBps);
            }
        });

        addTestNodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                addTestNode(page);
            }
        });

        abortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedInterface.abort();
            }
        });

        spinnerSelectNode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    speedInterface.setSelectNode(mNodeListBeans.get(position - 1).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addTestNode(page);
    }

    private void addTestNode(final int page) {
        // 手动设置测速节点
        speedInterface.getNodeList(page, new GetNodeListCallback() {
            @Override
            public void onResult(List<NodeListBean> listBeans) {
                if (page == 1 && (listBeans == null || listBeans.size() == 0)) {
                    mNodeListBeans.clear();
                    nodeListId.clear();
                    mArrayAdapter.notifyDataSetChanged();
                    return;
                }

                mNodeListBeans.addAll(listBeans);
                for (NodeListBean bean : listBeans) {
                    nodeListId.add(String.valueOf(bean.getId()) + "-" + bean.getSponsor() + "-" + bean.getOperator() + "-" + bean.getCity());
                }
                mArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(SdkThrowable throwable) {
                Toast.makeText(NonUIActivity.this, throwable.code + throwable.msg, Toast.LENGTH_SHORT).show();
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
        public void onError(SdkThrowable throwable) {
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
        public void onError(SdkThrowable throwable) {
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
        public void onError(SdkThrowable throwable) {
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
                    editPingText.setText("Ping Occur Error:---------------------"+"\n"+((SdkThrowable)msg.obj).code + ((SdkThrowable)msg.obj).msg);
                    break;
                case 2:
                    editPingLossText.setText("丢包：-----------" + msg.obj.toString() +"\n");
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
                    editText.setText("Download Occur Error:---------------------"+"\n"+((SdkThrowable)msg.obj).code + ((SdkThrowable)msg.obj).msg);
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
                    editUploadText.setText("Upload Occur Error:---------------------"+"\n"+((SdkThrowable)msg.obj).code + ((SdkThrowable)msg.obj).msg);
                    break;
                default:
                    break;
            }
        }
    }
}
