package com.speedtest.combo_sdk_demo;

import android.annotation.SuppressLint;
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

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.btn_live) {
      mTaskType = TaskType.LIVE_BROADCAST.name;
    } else if (id == R.id.btn_shop) {
      mTaskType = TaskType.E_COMMERCE.name;
    } else if (id == R.id.btn_ticket) {
      mTaskType = TaskType.BUY_TICKETS.name;
    } else if (id == R.id.btn_infomation) {
      mTaskType = TaskType.NEWS_TEST.name;
    } else if (id == R.id.btn_search) {
      mTaskType = TaskType.SEARCH_ENGINE.name;
    } else if (id == R.id.btn_video) {
      mTaskType = TaskType.VIDEO_TEST.name;
    } else if(id == R.id.btn_redpack) {
      mTaskType = TaskType.RED_WAR.name;
    } else if (id == R.id.btn_online_class) {
      mTaskType = TaskType.ONLINE_CLASS.name;
    } else if (id == R.id.btn_cloud_game) {
      mTaskType = TaskType.CLOUD_GAME.name;
    }

    Intent intent = new Intent(ComboListActivity.this, ComboActivity.class);
    intent.putExtra("TASK_TYPE", mTaskType);
    startActivity(intent);
  }
}
