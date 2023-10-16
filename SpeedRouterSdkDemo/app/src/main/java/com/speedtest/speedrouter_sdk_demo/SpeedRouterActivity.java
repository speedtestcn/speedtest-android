package com.speedtest.speedrouter_sdk_demo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.speedtest.lib_api.http.bean.NodeListBean;
import com.speedtest.lib_auth.SdkThrowable;
import com.speedtest.lib_model.unit.SpeedUnit;
import com.speedtest.speedrouter_sdk.SpeedInterface;
import com.speedtest.speedrouter_sdk.callback.GetNodeListCallback;
import com.speedtest.speedrouter_sdk.callback.PingCallback;
import com.speedtest.speedrouter_sdk.callback.SpeedtestCallback;
import com.speedtest.speedrouter_sdk.data.PingResultData;
import com.speedtest.speedrouter_sdk.data.SpeedType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class SpeedRouterActivity extends AppCompatActivity {
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
    private TextView editPingLossText;
    private TextView txtGetNodeText;
    private Switch switchFastSpeed;
    private Spinner spinnerSelectNode;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private Disposable getNodeInfoDisposable;
    private List<String> nodeListId;
    private ArrayAdapter<String> mArrayAdapter;
    private SpeedInterface speedInterface;
    private int downCount;
    private int upCount;
    private PingResultData mPingResultData;
    private List<NodeListBean> mNodeListBeans;
    private int page = 1;
    private String speedUnitStr;

    private MyHandler mHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_router);
        startSpeedBtn = findViewById(R.id.btn_start_mbps);
        startSpeedBtnMbs = findViewById(R.id.btn_start_mbs);
        startSpeedBtnKbs = findViewById(R.id.btn_start_kbs);
        addTestNodes = findViewById(R.id.btn_add_nodes);
        stopSpeedtestBtn = findViewById(R.id.btn_stop_speedtest);
        editDownloadText = findViewById(R.id.edt_result);
        editUploadText = findViewById(R.id.edt_upload_result);
        editPingText = findViewById(R.id.edt_ping_result);
        editPingLossText = findViewById(R.id.edt_ping_loss_result);
        txtGetNodeText = findViewById(R.id.edt_select_node);
        switchFastSpeed = findViewById(R.id.switch_fast_speed);
        spinnerSelectNode = findViewById(R.id.spinner_select_node);
        radioButton1 = findViewById(R.id.rb_app_to_net);
        radioButton2 = findViewById(R.id.rb_app_to_router);
        radioButton3 = findViewById(R.id.rb_router_to_net);

        nodeListId = new ArrayList<String>();
        mNodeListBeans = new ArrayList<NodeListBean>();
        nodeListId.add("选择测速节点");
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, nodeListId);
        spinnerSelectNode.setAdapter(mArrayAdapter);
        spinnerSelectNode.setSelection(0);

        editDownloadText.setMovementMethod(ScrollingMovementMethod.getInstance());

        speedInterface = SpeedInterface.getSDK().fastSpeed(true);
        // 页面进入时调用readySpeedTest，生成三段测速的关联id
        speedInterface.readySpeedTest();
        speedInterface.setSpeedType(SpeedType.APP_TO_NET);
        startSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearValue();
                speedInterface.fastSpeed(switchFastSpeed.isChecked());
                speedUnitStr = "Mbps";
                downCount = 0;
                upCount = 0;
                speedInterface.startSpeedTest(pingCallback,downloadCallback,uploadCallback,SpeedUnit.Mbitps);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final NodeListBean bean = speedInterface.getSelectNode();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bean == null) {
                                    txtGetNodeText.setText("测速节点：未设置测速节点,正在获取默认节点");
                                } else {
                                    txtGetNodeText.setText("测速节点："+speedInterface.getSelectNode().toString());
                                }
                            }
                        });
                    }
                }, 1500);
            }
        });

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    speedInterface.setSpeedType(SpeedType.APP_TO_NET);
                }
            }
        });
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    speedInterface.setSpeedType(SpeedType.APP_TO_ROUTER);
                }
            }
        });
        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    speedInterface.setSpeedType(SpeedType.ROUTER_TO_NET);
                }
            }
        });

        startSpeedBtnMbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearValue();
                speedUnitStr = "MB/s";
                speedInterface
                        .fastSpeed(switchFastSpeed.isChecked());
                speedInterface.startSpeedTest(pingCallback,downloadCallback,uploadCallback, SpeedUnit.MBps);
            }
        });

        startSpeedBtnKbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearValue();
                speedUnitStr = "KB/s";
                speedInterface
                        .fastSpeed(switchFastSpeed.isChecked());
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

        stopSpeedtestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedInterface.stopSpeedtest();
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
                Toast.makeText(SpeedRouterActivity.this, throwable.code + throwable.msg, Toast.LENGTH_SHORT).show();
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
                    editPingText.setText("Ping Occur Error:---------------------"+"\n"+((SdkThrowable)msg.obj).code + ((SdkThrowable)msg.obj).msg);
                    break;
                case 2:
                    editPingLossText.setText("丢包：-----------" + msg.obj.toString() +"\n");
                    break;
                case 3:
                    editDownloadText.setText("Start SpeedTest Download:---------------------"+"\n");
                    break;
                case 4:
                    editDownloadText.setText("下载速度" + speedUnitStr + ":" + msg.obj.toString());
                    break;
                case 5:
                    editDownloadText.setText("End SpeedTest Download:---------------------次数："+ downCount +"\n" + msg.obj.toString());
                    break;
                case 6:
                    editDownloadText.setText("Download Occur Error:---------------------"+"\n"+((SdkThrowable)msg.obj).code + ((SdkThrowable)msg.obj).msg);
                    break;
                case 7:
                    editUploadText.setText("Start SpeedTest Upload:---------------------"+"\n");
                    break;
                case 8:
                    editUploadText.setText("上传速度" + speedUnitStr + ":" + msg.obj.toString());
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

    private void clearValue() {
        editPingText.setText("");
        editPingLossText.setText("");
        editDownloadText.setText("");
        editUploadText.setText("");
    }
}
