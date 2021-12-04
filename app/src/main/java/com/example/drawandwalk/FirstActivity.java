package com.example.drawandwalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {
    private Button btnDrawingPage,btnShowDrawingPage;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);
        btnDrawingPage=findViewById(R.id.btnDrawingPage);
        btnShowDrawingPage=findViewById(R.id.btnShowDrawingPage);
        btnShowDrawingPage.setOnClickListener(new View.OnClickListener() {//작품 감상화면
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DrawFilesActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnDrawingPage.setOnClickListener(new View.OnClickListener() {//그림주제화면
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SelectActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
