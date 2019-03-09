package com.android.imabhishekkumar.attendance_app;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.zxing.BarcodeFormat;



public class teacherMain extends AppCompatActivity {
    Button startSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        startSession= findViewById(R.id.startSession);

        startSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(teacherMain.this,GeneratorActivity.class));
            }
        });

    }


}
