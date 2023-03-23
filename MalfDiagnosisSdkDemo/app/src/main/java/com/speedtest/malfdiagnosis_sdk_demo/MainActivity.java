package com.speedtest.malfdiagnosis_sdk_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnMalfDiagnosis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMalfDiagnosis = findViewById(R.id.btn_malf_diagnosis);
        btnMalfDiagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MalfDiagnosisActivity.class);
                startActivity(intent);
            }
        });
    }
}
