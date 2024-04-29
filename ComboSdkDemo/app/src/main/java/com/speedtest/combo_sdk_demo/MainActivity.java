package com.speedtest.combo_sdk_demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.speedtest.combo_sdk.TaskType;


public class MainActivity extends AppCompatActivity {

    private Button comboBtn;
    private Button gameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        comboBtn = findViewById(R.id.btn_combo);
        gameBtn = findViewById(R.id.btn_game);

        comboBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ComboListActivity.class);
                startActivity(intent);
            }
        });
        gameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

    }

}
