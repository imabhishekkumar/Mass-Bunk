package com.android.imabhishekkumar.attendance_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GeneratorActivity extends AppCompatActivity {
    EditText text;
    Button gen_btn;
    ImageView image;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, sessionDBref,addtoDB;
    long time;
    TextView textToJoin;
    String text2Qr, formattedText, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);
        text = findViewById(R.id.text);
        gen_btn = findViewById(R.id.gen_btn);
        image = findViewById(R.id.image);
        mAuth = FirebaseAuth.getInstance();
        textToJoin = findViewById(R.id.textToJoin);
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = firebaseDatabase.getReference();
        sessionDBref = firebaseDatabase.getReference().child("sessions");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("teachers").child(mAuth.getUid()).child("name").getValue().toString();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        gen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text2Qr = text.getText().toString().trim();
                time = System.currentTimeMillis();
                formattedText = text2Qr + String.valueOf(time);
                textToJoin.setText(formattedText);
                progressDialog.setMessage("Generating QRcode");
                progressDialog.show();
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    addtoDB = firebaseDatabase.getReference().child("sessions").child(formattedText);
                    //DatabaseReference addtoDB = sessionDBref.push();
                    //Map<String, String> dataToSave = new HashMap<>();
                    addtoDB.setValue(name);

                    //dataToSave.put("teacherName", name);
                    //dataToSave.put("sessionID", formattedText);
                    //addtoDB.setValue(dataToSave);
                    progressDialog.dismiss();
                    BitMatrix bitMatrix = multiFormatWriter.encode(formattedText, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    image.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }
        });


    }


}
