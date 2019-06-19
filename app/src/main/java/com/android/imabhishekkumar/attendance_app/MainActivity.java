package com.android.imabhishekkumar.attendance_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.lang.invoke.ConstantCallSite;
import java.util.HashMap;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    private Button scan_btn,submit_btn;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, sessionDBref,dataStudentDRef;
    LocationManager locationManager;
    ProgressBar progressBar;
    EditText readerET;
    Double lattitude, longitude;
    String teacherLatitude,teacherLongitude;
    private SimpleLocation location;
    String uniqueId,name,registerNumber;
    FirebaseUser mUser;
    RelativeLayout studentLayout;
    ScrollView teacherLayout;
    Boolean isStudent = false, isTeacher = false;
    //
    ConstraintLayout mainLayout;
    EditText text;
    Button gen_btn;
    ImageView image;
    DatabaseReference addtoDB;
    long time;
    TextView textToJoin;
    String text2Qr, formattedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mUser = mAuth.getCurrentUser();
        scan_btn = findViewById(R.id.scan_btn);
        submit_btn = findViewById(R.id.submit_btn);
        mAuth= FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = firebaseDatabase.getReference();
        progressBar=findViewById(R.id.readerProgress);
        location = new SimpleLocation(this);
        readerET = findViewById(R.id.readerET);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        longitude = location.getLongitude();
        lattitude = location.getLatitude();
        studentLayout = findViewById(R.id.layoutStudent);
        teacherLayout = findViewById(R.id.layoutTeacher);
        mainLayout = findViewById(R.id.layoutMain);
        databaseReference = firebaseDatabase.getReference();
        progressDialog = new ProgressDialog(this);
        //
        text = findViewById(R.id.text);
        gen_btn = findViewById(R.id.gen_btn);
        image = findViewById(R.id.image);
        mAuth = FirebaseAuth.getInstance();
        textToJoin = findViewById(R.id.textToJoin);
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        databaseReference = firebaseDatabase.getReference();
        sessionDBref = firebaseDatabase.getReference().child("sessions");
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        location = new SimpleLocation(this);
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }


        if (mUser!= null) {
        progressDialog.setMessage("Verifying");
        progressDialog.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("students").child(mUser.getUid()).exists()) {
                    showStudentActivity();
                   // isStudent = true;
                   // Intent intent = new Intent(MainActivity.this, ReaderActivity.class);
                   // startActivity(intent);
                   // progressDialog.dismiss();
                } else if (dataSnapshot.child("teachers").child(mUser.getUid()).exists()) {
                    showTeacherActivity();
                    //isTeacher = true;
                    //Intent intent = new Intent(MainActivity.this, GeneratorActivity.class);
                  //  startActivity(intent);
                    //progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


    }
        gen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text2Qr = text.getText().toString().trim();
                if(!text2Qr.isEmpty()){
                    time = System.currentTimeMillis();
                    formattedText = text2Qr + time;
                    textToJoin.setText(formattedText);
                    progressDialog.setMessage("Generating QRcode");
                    progressDialog.show();
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        addtoDB = firebaseDatabase.getReference().child("sessions").child(formattedText);

                        Map<String, String> dataToSave = new HashMap<>();

                        dataToSave.put("teacherName", name);
                        dataToSave.put("latitude", lattitude.toString());
                        dataToSave.put("longitude", longitude.toString());

                        addtoDB.setValue(dataToSave);
                        progressDialog.dismiss();
                        BitMatrix bitMatrix = multiFormatWriter.encode(formattedText, BarcodeFormat.QR_CODE, 200, 200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        image.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sessionKey = readerET.getText().toString();
                if(!sessionKey.isEmpty()){
                    progressDialog.show();
                    pushToDB(sessionKey);
                }
            }
        });

    }

    private void showStudentActivity() {
        mainLayout.setVisibility(View.GONE);
        studentLayout.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                registerNumber =dataSnapshot.child("students").child(mAuth.getUid()).child("register").getValue().toString();
                name = dataSnapshot.child("students").child(mAuth.getUid()).child("name").getValue().toString();
                progressBar.setVisibility(View.INVISIBLE);
                scan_btn.setEnabled(true);
                submit_btn.setEnabled(true);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(getApplicationContext(),lattitude.toString()+" "+longitude.toString(),Toast.LENGTH_SHORT).show();

}
    private void showTeacherActivity() {
        mainLayout.setVisibility(View.GONE);
        teacherLayout.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("teachers").child(mAuth.getUid()).child("name").getValue().toString();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.signout:
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {

                Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                uniqueId= result.getContents();
                pushToDB(uniqueId);


            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void pushToDB(String uniqueId) {
        sessionDBref = firebaseDatabase.getReference().child("sessions").child(uniqueId);
        sessionDBref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherLatitude= dataSnapshot.child("latitude").getValue().toString();
                teacherLongitude= dataSnapshot.child("longitude").getValue().toString();
                if(Math.abs(measure(Double.parseDouble(teacherLatitude),Double.parseDouble(teacherLongitude),lattitude,longitude))<=7){
                    dataStudentDRef= sessionDBref.child("attendedBy").child(mAuth.getUid());
                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("name", name);
                    dataToSave.put("register", registerNumber);
                    dataToSave.put("latitude", lattitude.toString());
                    dataToSave.put("longitude", longitude.toString());
                    dataStudentDRef.setValue(dataToSave);
                    progressDialog.dismiss();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Not in range",Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public double measure(double lat1,double lon1,double lat2,double lon2){
        double R = 6378.137;

        double dLat = (lat2 * Math.PI / 180) - (lat1 * Math.PI / 180);
        double dLon = (lon2 * Math.PI / 180) - (lon1 * Math.PI / 180);
        double a = (Math.sin(dLat/2)) * (Math.sin(dLat/2)) +(Math.cos(lat1)) *( Math.PI / (180)) * (Math.cos(lat2)) * (Math.PI / (180)) *(Math.sin(dLon/2)) *(Math.sin(dLon/2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return ( d * 10);

    }
}
