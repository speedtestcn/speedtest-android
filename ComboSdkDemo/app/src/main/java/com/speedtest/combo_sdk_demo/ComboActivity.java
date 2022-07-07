package com.speedtest.combo_sdk_demo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.speedtest.combo_sdk.TaskType;
import com.speedtest.combo_sdk.TaskTypeUtil;

public class ComboActivity extends AppCompatActivity {
    private static final String TAG = "ComboActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo);

    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        String sTaskType = getIntent().getStringExtra("TASK_TYPE");
        TaskTypeUtil.setTaskType(TaskType.fromName(sTaskType));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
