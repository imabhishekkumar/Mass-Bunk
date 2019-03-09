package com.android.imabhishekkumar.attendance_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button studentBtn, teacherBtn;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser mUser;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    Boolean isStudent = false, isTeacher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mUser = mAuth.getCurrentUser();

        databaseReference = firebaseDatabase.getReference();
        studentBtn = findViewById(R.id.studentBtn);
        teacherBtn = findViewById(R.id.teacherBtn);
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Verifying");
        progressDialog.show();

        databaseReference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("students").child(mUser.getUid()).exists()){
                    isStudent=true;
                    Intent intent = new Intent(MainActivity.this, ReaderActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }
                else if (dataSnapshot.child("teachers").child(mUser.getUid()).exists()){
                    isTeacher=true;
                    Intent intent = new Intent(MainActivity.this, GeneratorActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        studentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("type", "students");
                startActivity(intent);
            }
        });

        teacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("type", "teachers");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}
