package com.example.drawandwalk;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Random;

public class SelectActivity extends AppCompatActivity {
    private String topic="자유주제";
    private int hour = 0, min = 0;
    private boolean timeLimit;
    private RadioGroup rdoGroup;
    private Button btnRand, btnGPSStart;
    private LinearLayout linearRand, linearTime;
    private TextView tvRandTopic;
    private Switch switchTime;
    private EditText etHour, etMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        setTitle("Walk and Draw");
        Random random = new Random();
        rdoGroup = findViewById(R.id.rdoGroup);
        linearRand = findViewById(R.id.linearRand);
        linearTime = findViewById(R.id.linearTime);
        tvRandTopic = findViewById(R.id.tvRandTopic);
        btnRand = findViewById(R.id.btnRand);
        switchTime = findViewById(R.id.switchTime);
        btnGPSStart = findViewById(R.id.btnGPSStart);
        etHour = findViewById(R.id.etHour);
        etMin = findViewById(R.id.etMin);
        timeLimit = false;
        String[] randTopics = {"고양이", "강아지", "뱀", "사람", "물병",
                "공룡", "사자", "호랑이", "코뿔소", "칫솔", "가위", "컵", "토끼", "기니피그", "돼지"
        };
        rdoGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdoFree) {
                    linearRand.setVisibility(View.INVISIBLE);
                    topic = "자유 주제";
                } else if (checkedId == R.id.rdoRand) {
                    linearRand.setVisibility(View.VISIBLE);
                    int randNum = random.nextInt(randTopics.length);
                    topic = randTopics[randNum];
                    tvRandTopic.setText("랜덤 주제: " + topic);
                }
            }
        });
        btnRand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int randNum = random.nextInt(randTopics.length);
                topic = randTopics[randNum];
                tvRandTopic.setText("랜덤 주제: " + topic);
            }
        });
        switchTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//on
                    linearTime.setVisibility(View.VISIBLE);
                    switchTime.setText("제한시간: 있음");
                    timeLimit = true;
                } else {
                    linearTime.setVisibility(View.INVISIBLE);
                    switchTime.setText("제한시간: 없음");
                    hour = 0;
                    min = 0;
                    timeLimit = false;
                }
            }
        });
        btnGPSStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeLimit == false) {//시간제한 없을때
                    startGPS();
                } else {//시간제한 있을때
                    if (checkHour() && checkMin()) {
                        if (hour == 0 && min == 0) {
                            showToast("0시간 0분은 설정할 수 없습니다.");
                        } else {
                            startGPS();
                        }
                    }
                }
            }
        });
    }

    public boolean checkHour() {
        if (etHour.getText().toString().matches("")) {
            showToast("시간을 입력해 주세요");
            return false;
        }
        hour = Integer.parseInt(etHour.getText().toString());
        if (hour >= 5) {
            showToast("5시간 이상은 설정할 수 없습니다.");
            etHour.setText(null);
            return false;
        }
        return true;
    }

    public boolean checkMin() {
        if (etMin.getText().toString().matches("")) {
            showToast("시간을 입력해 주세요");
            return false;
        }
        min = Integer.parseInt(etMin.getText().toString());
        if (min > 60) {
            showToast("60분 이상은 설정할 수 없습니다.");
            etMin.setText(null);
            return false;
        }
        return true;
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void startGPS() {
        Intent intent = new Intent(this, GpsDrawActivity.class);
        intent.putExtra("topic", topic);
        intent.putExtra("hour", hour);
        intent.putExtra("min", min);
        intent.putExtra("timeLimit", timeLimit);
        startActivity(intent);
        finish();
    }

}
