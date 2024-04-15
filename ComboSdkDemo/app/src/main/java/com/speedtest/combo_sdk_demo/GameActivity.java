package com.speedtest.combo_sdk_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.speedtest.combo_sdk.TaskType;
import com.speedtest.combo_sdk.TaskTypeUtil;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        TaskTypeUtil.setTaskType(TaskType.PLAY_GAMES);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}