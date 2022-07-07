package com.speedtest.combo_sdk_demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.speedtest.combo_sdk.TaskType;

public class ComboListActivity extends AppCompatActivity implements View.OnClickListener {

  private Button liveBtn;
  private Button shopBtn;
  private Button ticketBtn;
  private Button infoBtn;
  private Button searchBtn;
  private Button videoBtn;
  private Button redPackBtn;
  private Button onlineClassBtn;
  private Button cloudGameBtn;
  private String mTaskType;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_combo_list);
    liveBtn = findViewById(R.id.btn_live);
    shopBtn = findViewById(R.id.btn_shop);
    ticketBtn = findViewById(R.id.btn_ticket);
    infoBtn = findViewById(R.id.btn_infomation);
    searchBtn = findViewById(R.id.btn_search);
    videoBtn = findViewById(R.id.btn_video);
    redPackBtn = findViewById(R.id.btn_redpack);
    onlineClassBtn = findViewById(R.id.btn_online_class);
    cloudGameBtn = findViewById(R.id.btn_cloud_game);

    liveBtn.setOnClickListener(this);
    shopBtn.setOnClickListener(this);
    ticketBtn.setOnClickListener(this);
    infoBtn.setOnClickListener(this);
    searchBtn.setOnClickListener(this);
    videoBtn.setOnClickListener(this);
    redPackBtn.setOnClickListener(this);
    onlineClassBtn.setOnClickListener(this);
    cloudGameBtn.setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_live:
        mTaskType = TaskType.LIVE_BROADCAST.name;
        break;
      case R.id.btn_shop:
        mTaskType = TaskType.E_COMMERCE.name;
        break;
      case R.id.btn_ticket:
        mTaskType = TaskType.BUY_TICKETS.name;
        break;
      case R.id.btn_infomation:
        mTaskType = TaskType.NEWS_TEST.name;
        break;
      case R.id.btn_search:
        mTaskType = TaskType.SEARCH_ENGINE.name;
        break;
      case R.id.btn_video:
        mTaskType = TaskType.VIDEO_TEST.name;
        break;
      case R.id.btn_online_class:
        mTaskType = TaskType.ONLINE_CLASS.name;
        break;
      case R.id.btn_cloud_game:
        mTaskType = TaskType.CLOUD_GAME.name;
        break;
      default:
        mTaskType = TaskType.RED_WAR.name;
        break;


    }
    Intent intent = new Intent(ComboListActivity.this, ComboActivity.class);
    intent.putExtra("TASK_TYPE", mTaskType);
    startActivity(intent);
  }
}
