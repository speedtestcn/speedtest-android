package com.speedtest.speedtest_sdk;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
import com.speedtest.lib_api.http.bean.IpInfoBean;
import com.speedtest.lib_api.http.bean.LocationInfoBean;
import com.speedtest.lib_api.http.bean.NodeListBean;
import com.speedtest.lib_auth.SdkThrowable;
import com.speedtest.lib_speedtest.bean.PingResultData;
import com.speedtest.lib_speedtest.bean.SpeedExtraData;
import com.speedtest.lib_speedtest.callback.SetNodeCallback;
import com.speedtest.lib_speedtest.unit.SpeedUnit;
import com.speedtest.speedtest_sdk.SpeedInterface;
import com.speedtest.speedtest_sdk.callback.GetIpInfoCallback;
import com.speedtest.speedtest_sdk.callback.GetNodeDelayCallback;
import com.speedtest.speedtest_sdk.callback.GetNodeListCallback;
import com.speedtest.speedtest_sdk.callback.GetSpeedExtraCallback;
import com.speedtest.speedtest_sdk.callback.PingCallback;
import com.speedtest.speedtest_sdk.callback.SpecialTestCallback;
import com.speedtest.speedtest_sdk.callback.SpeedtestCallback;
import com.speedtest.speedtest_sdk.data.SpeedAlgoType;
import com.speedtest.speedtest_sdk.data.SpeedtestState;
import com.speedtest.speedtest_sdk.data.SpeedtestType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.reactivex.disposables.Disposable;


public class NonUIActivity extends AppCompatActivity {
    private static final String TAG = "NonUIActivity";
    private Button startSpeedBtn;
    private Button startSpeedBtnMbs;
    private Button startSpeedBtnKbs;
    private Button addTestNodes;
    private Button stopSpeedtestBtn;
    private Button btnSkipToNodesSelect;
    private TextView editDownloadText;
    private TextView editUploadText;
    private TextView editPingText;
    private TextView tvSpeedExtraResultText;
    private TextView editProcessStateText;
    private TextView editPingLossText;
    private TextView txtGetNodeText;
    private TextView tvSpecialTestResult;
    private TextView tvGetIp;
    private TextView tvIpInfo;

    private TextView tvSdkVersion;
    private EditText etHoldValue;
    private Switch switchAutoSpeed;
    private Switch switchFastSpeed;
    private Switch switchAlgoType;
    private TextView tvAlgoType;

    private Button btSetDuration;
    private EditText etDownDuration;
    private EditText etUpDuration;
    private Spinner spinnerSelectNode;
    private Spinner spinnerSpeedType;
    private Disposable getNodeInfoDisposable;
    private List<String> nodeListId;
    private List<String> speedTypeList;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayAdapter<String> mTypeArrayAdapter;
    private SpeedInterface speedInterface;
    private int downCount;
    private int upCount;
    private PingResultData mPingResultData;
    private List<NodeListBean> mNodeListBeans;
    private int page = 1;
    private String speedUnitStr;

    private MyHandler mHandler = new MyHandler();

    private List<Double> downList = new ArrayList<>();
    private SpeedUnit mUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noui);
        startSpeedBtn = findViewById(R.id.btn_start_mbps);
        startSpeedBtnMbs = findViewById(R.id.btn_start_mbs);
        startSpeedBtnKbs = findViewById(R.id.btn_start_kbs);
        addTestNodes = findViewById(R.id.btn_add_nodes);
        stopSpeedtestBtn = findViewById(R.id.btn_abort);
        tvSpeedExtraResultText = findViewById(R.id.tv_speed_extra_result);
        editDownloadText = findViewById(R.id.edt_result);
        editUploadText = findViewById(R.id.edt_upload_result);
        editPingText = findViewById(R.id.edt_ping_result);
        editProcessStateText = findViewById(R.id.edt_process_state);
        editPingLossText = findViewById(R.id.edt_ping_loss_result);
        txtGetNodeText = findViewById(R.id.edt_select_node);
        etHoldValue = findViewById(R.id.et_hold_value);
        etDownDuration = findViewById(R.id.et_download_duration);
        etUpDuration = findViewById(R.id.et_upload_duration);
        btSetDuration = findViewById(R.id.bt_set_duration);
        switchAutoSpeed = findViewById(R.id.switch_auto_speed);
        switchFastSpeed = findViewById(R.id.switch_fast_speed);
        spinnerSelectNode = findViewById(R.id.spinner_select_node);
        spinnerSpeedType = findViewById(R.id.spinner_speed_type);
        switchAlgoType = findViewById(R.id.switch_algo_type);
        tvAlgoType = findViewById(R.id.tv_algo_type);
        tvSpecialTestResult = findViewById(R.id.tv_special_test_result);
        tvSdkVersion = findViewById(R.id.tv_sdk_version);
        tvGetIp = findViewById(R.id.btn_getip);
        tvIpInfo = findViewById(R.id.tv_ip_info);
        nodeListId = new ArrayList<String>();
        speedTypeList = Arrays.asList("下载&上传", "下载", "上传");
        mNodeListBeans = new ArrayList<NodeListBean>();
        nodeListId.add("选择测速节点");
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, nodeListId);
        mTypeArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, speedTypeList);
        spinnerSelectNode.setAdapter(mArrayAdapter);
        spinnerSpeedType.setAdapter(mTypeArrayAdapter);
        spinnerSelectNode.setSelection(0);

        editDownloadText.setMovementMethod(ScrollingMovementMethod.getInstance());

        speedInterface = SpeedInterface.getSDK();
        if (tvSdkVersion != null) {
            tvSdkVersion.setText("SDK版本号：" + speedInterface.getSdkVersion() +" \n 环境切换：1-测试环境；2-预发布环境；3-海外正式环境；4-正式环境；5-海外测试环境    当前环境： "+speedInterface.getSDKType());
        }

        // 算法切换开关
        updateAlgoTypeLabel();
        switchAlgoType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                speedInterface.setSpeedAlgoType(SpeedAlgoType.C_JNI);
            } else {
                speedInterface.setSpeedAlgoType(SpeedAlgoType.NATIVE);
            }
            updateAlgoTypeLabel();
        });

        speedInterface.getSpeedExtraData(new GetSpeedExtraCallback() {
            @Override
            public void onResult(SpeedExtraData speedExtraData) {
                Message msg = mHandler.obtainMessage();
                msg.what = 11;
                msg.obj = speedExtraData;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(SdkThrowable sdkThrowable) {
                Message msg = mHandler.obtainMessage();
                msg.obj = sdkThrowable;
                msg.what = 12;
                mHandler.sendMessage(msg);
            }
        });

        speedInterface.setSpecialTestCallback(new SpecialTestCallback() {
            @Override
            public void onResult(String toolName, long avgVal) {
                Message msg = mHandler.obtainMessage();
                msg.what = 14;
                msg.obj = toolName;
                msg.arg1 = (int) avgVal;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(SdkThrowable throwable) {
                Message msg = mHandler.obtainMessage();
                msg.obj = throwable;
                msg.what = 15;
                mHandler.sendMessage(msg);
            }
        });

        startSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUnit = SpeedUnit.Mbitps;
                clearValue();
                downCount = 0;
                upCount = 0;
                updateAlgoTypeLabel();
                speedInterface.startSpeedTest(pingCallback, downloadCallback, uploadCallback, mUnit);
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
                                    txtGetNodeText.setText("测速节点：" + speedInterface.getSelectNode().toString());
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
                mUnit = SpeedUnit.MBps;
                clearValue();
                downCount = 0;
                upCount = 0;
                updateAlgoTypeLabel();
                speedInterface.startSpeedTest(pingCallback, downloadCallback, uploadCallback, mUnit);
                final NodeListBean bean = speedInterface.getSelectNode();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bean == null) {
                            txtGetNodeText.setText("测速节点：未设置测速节点");
                        } else {
                            txtGetNodeText.setText("测速节点：" + speedInterface.getSelectNode().toString());
                        }
                    }
                });
            }
        });

        startSpeedBtnKbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUnit = SpeedUnit.KBps;
                clearValue();
                downCount = 0;
                upCount = 0;
                updateAlgoTypeLabel();
                speedInterface.startSpeedTest(pingCallback, downloadCallback, uploadCallback, mUnit);
                final NodeListBean bean = speedInterface.getSelectNode();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bean == null) {
                            txtGetNodeText.setText("测速节点：未设置测速节点");
                        } else {
                            txtGetNodeText.setText("测速节点：" + speedInterface.getSelectNode().toString());
                        }
                    }
                });
            }
        });

        tvGetIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedInterface.getIpLocation(new GetIpInfoCallback() {
                    @Override
                    public void onResult(IpInfoBean ipInfoBean) {
                        tvIpInfo.setText(new Gson().toJson(ipInfoBean));
                        Toast.makeText(NonUIActivity.this, "ip:" + ipInfoBean.getIp(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SdkThrowable throwable) {
                        Toast.makeText(NonUIActivity.this, String.valueOf(throwable.code), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        addTestNodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                addTestNode(page);
            }
        });

        stopSpeedtestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedInterface.stopSpeedtest();
            }
        });

        btSetDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etDownDuration.getText().toString())) {
                    int downSet = speedInterface.setDownloadMaxTestDuration(
                            Integer.parseInt(etDownDuration.getText().toString()));
                    if (downSet == 1) {
                        Toast.makeText(NonUIActivity.this, "下载时长设置成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NonUIActivity.this, "下载时长设置失败", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!TextUtils.isEmpty(etUpDuration.getText().toString())) {
                    int upSet = speedInterface.setUploadMaxTestDuration(
                            Integer.parseInt(etUpDuration.getText().toString()));
                    if (upSet == 1) {
                        Toast.makeText(NonUIActivity.this, "上传时长设置成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NonUIActivity.this, "上传时长设置失败", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        spinnerSelectNode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!CollectionUtil.isEmpty(mNodeListBeans)) {
                    speedInterface.setSelectNode(mNodeListBeans.get(position).getId(), new SetNodeCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(NonUIActivity.this, "节点设置成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(SdkThrowable sdkThrowable) {
                            Toast.makeText(NonUIActivity.this, "节点设置失败：" + String.valueOf(sdkThrowable.code), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSpeedType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    speedInterface.setSpeedTestType(SpeedtestType.DOWN_UP);
                } else if (position == 1) {
                    speedInterface.setSpeedTestType(SpeedtestType.DOWN);
                } else if (position == 2) {
                    speedInterface.setSpeedTestType(SpeedtestType.UP);
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
                if (page == 1) {
                    mNodeListBeans.clear();
                    nodeListId.clear();
                    mArrayAdapter.notifyDataSetChanged();
                }
                if (!CollectionUtil.isEmpty(listBeans)) {
                    mNodeListBeans.addAll(listBeans);
                    for (NodeListBean bean : listBeans) {
                        nodeListId.add(String.valueOf(bean.getId()) + "-" + bean.getSponsor() + "-" + bean.getOperator() + "-" + bean.getCity() + "-" + bean.getDelay() + "-" + (bean.getLimitNodeType() == 1? "限制节点": "正常节点"));
                    }
                    mArrayAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(SdkThrowable throwable) {
                Toast.makeText(NonUIActivity.this, String.valueOf(throwable.code), Toast.LENGTH_SHORT).show();
            }
        }, new GetNodeDelayCallback() {
            @Override
            public void onResult(NodeListBean nodeBean) {
                Log.e("NodeDelay", nodeBean.toString());
                for (NodeListBean bean : mNodeListBeans) {
                    if (bean.equals(nodeBean)) {
                        bean.setDelay(nodeBean.getDelay());
                    }
                }
                if (!CollectionUtil.isEmpty(mNodeListBeans)) {
                    nodeListId.clear();
                    for (NodeListBean bean : mNodeListBeans) {
                        nodeListId.add(String.valueOf(bean.getId()) + "-" + bean.getSponsor() + "-" + bean.getOperator() + "-" + bean.getCity() +
                                "-" + bean.getDelay() + "-" + (bean.getLimitNodeType() == 1? "限制节点": "正常节点"));
                    }
                    mArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(SdkThrowable throwable) {

            }
        });


    }

    private PingCallback pingCallback = new PingCallback() {
        @Override
        public void onResult(PingResultData pingResultData) {
            switch (mUnit) {
                case Mbitps:
                    speedUnitStr = "Mbps";
                    break;
                case MBps:
                    speedUnitStr = "MB/s";
                    break;
                case KBps:
                    speedUnitStr = "kB/s";
                    break;
            }

            mPingResultData = pingResultData;
            Message msg = mHandler.obtainMessage();
            mHandler.sendEmptyMessage(0);
        }

        @Override
        public void onError(SdkThrowable throwable) {
            Message msg = mHandler.obtainMessage();
            msg.obj = throwable;
            msg.what = 1;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onPckLoss(double pckLoss) {
            Message msg = mHandler.obtainMessage();
            msg.what = 2;
            msg.obj = pckLoss;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onProcessState(SpeedtestState state) {
            Message msg = mHandler.obtainMessage();
            msg.what = 13;
            msg.obj = state;
            mHandler.sendMessage(msg);
        }
    };

    private SpeedtestCallback downloadCallback = new SpeedtestCallback() {

        @Override
        public void onStart() {
            Message msg = mHandler.obtainMessage();
            mHandler.sendEmptyMessage(3);
        }

        @Override
        public void onProcess(double mbps) {
            downCount++;
            downList.add(mbps);
            Message msg = mHandler.obtainMessage();
            msg.what = 4;
            msg.obj = mbps;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onEnd(double mbps, double flow) {
            Message msg = mHandler.obtainMessage();
            msg.what = 5;
            msg.obj = mbps + "使用流量：" + flow + "MB";
            mHandler.sendMessage(msg);
        }

        @Override
        public void onError(SdkThrowable throwable) {
            Message msg = mHandler.obtainMessage();
            msg.what = 6;
            msg.obj = throwable;
            mHandler.sendMessage(msg);
        }
    };

    private SpeedtestCallback uploadCallback = new SpeedtestCallback() {

        @Override
        public void onStart() {
            Message msg = mHandler.obtainMessage();
            mHandler.sendEmptyMessage(7);
        }

        @Override
        public void onProcess(double mbps) {
            upCount++;
            Message msg = mHandler.obtainMessage();
            msg.what = 8;
            msg.obj = mbps;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onEnd(double mbps, double flow) {
            Message msg = mHandler.obtainMessage();
            msg.what = 9;
            msg.obj = mbps + "使用流量：" + flow + "MB";
            mHandler.sendMessage(msg);
        }

        @Override
        public void onError(SdkThrowable throwable) {
            Message msg = mHandler.obtainMessage();
            msg.what = 10;
            msg.obj = throwable;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speedInterface.abort();
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
                    editPingText.setText("Ping Occur Error:---------------------"+"\n"+((SdkThrowable)msg.obj).code);
                    break;
                case 2:
                    editPingLossText.setText("丢包：-----------：" + msg.obj.toString() +"\n");
                    break;
                case 3:
                    editDownloadText.setText("Start SpeedTest Download:---------------------"+"\n");
                    break;
                case 4:
                    editDownloadText.setText("下载速度" + speedUnitStr + ":" + msg.obj.toString());
                    break;
                case 5:
                    editDownloadText.setText("End SpeedTest Download:---------------------次数：" +
                            ""+ downCount +"\n" + "平均下载速度" + speedUnitStr + ":" + msg.obj.toString()
                            + "最高下载速度" + speedUnitStr + ":" + Collections.max(downList));
                    break;
                case 6:
                    editDownloadText.setText("Download Occur Error:---------------------"+"\n"+((SdkThrowable)msg.obj).code);
                    break;
                case 7:
                    editUploadText.setText("Start SpeedTest Upload:---------------------"+"\n");
                    break;
                case 8:
                    editUploadText.setText("上传速度" + speedUnitStr + ":" + msg.obj.toString());
                    break;
                case 9:
                    editUploadText.setText("End SpeedTest Upload:---------------------次数："+ upCount +"\n" + "上传速度" + speedUnitStr + ":" + msg.obj.toString());
                    break;
                case 10:
                    editUploadText.setText("Upload Occur Error:---------------------"+"\n"+((SdkThrowable)msg.obj).code);
                    break;
                case 11:
                    SpeedExtraData extraData = (SpeedExtraData)msg.obj;
                    tvSpeedExtraResultText.setText("下载速率峰值：" + extraData.getDownloadCrest() + "  上传速率峰值：" + extraData.getUploadCrest() + "\n"+
                            "下载忙时时延最大值：" + extraData.getBusyDownloadPingMax() + "  上传忙时时延最大值：" + extraData.getBusyUploadPingMax() + "\n"+
                            "下载忙时时延最小值：" + extraData.getBusyDownloadPingMin() + "  上传忙时时延最小值：" + extraData.getBusyUploadPingMin()+ "\n" +
                            "下载忙时时延：" + extraData.getBusyDownloadPing() + "  上传忙时时延：" + extraData.getBusyUploadPing() + "\n"+
                            "闲时时延最大值：" + extraData.getMaxPing() + "  闲时时延最小值：" + extraData.getMinPing() + "\n"+
                            "下载忙时抖动：" + extraData.getBusyDownloadJitter() + "  上传忙时抖动：" + extraData.getBusyUploadJitter());
                    break;
                case 12:
                    tvSpeedExtraResultText.setText("Download Occur Error:---------------------"+"\n"+((SdkThrowable)msg.obj).code);
                    break;
                case 13:
                    editProcessStateText.setText("SpeedTest Process State:" + (SpeedtestState)msg.obj);
                    break;
                case 14:
                    tvSpecialTestResult.append("加测名称：" + (String) msg.obj + "，测试平均时间（ms）：" + msg.arg1 + "\n");
                    break;
                case 15:
                    tvSpecialTestResult.setText("加测 Occur Error：---------------------" + ((SdkThrowable)msg.obj).code);
                    break;
                default:
                    break;
            }
        }
    }


    private void clearValue() {
        editPingText.setText("");
        editPingLossText.setText("");
        editDownloadText.setText("");
        editUploadText.setText("");
        tvSpeedExtraResultText.setText("");
        editProcessStateText.setText("");
        tvSpecialTestResult.setText("");
        downList.clear();
    }

    private void updateAlgoTypeLabel() {
        SpeedAlgoType currentType = speedInterface.getSpeedAlgoType();
        if (currentType == SpeedAlgoType.NATIVE) {
            tvAlgoType.setText("当前算法：原生算法");
        } else {
            tvAlgoType.setText("当前算法：C测速算法");
        }
    }
}