package com.android.imabhishekkumar.attendance_app;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;



public class ReaderActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    private Button scan_btn;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, sessionDBref,dataStudentDRef;
    LocationManager locationManager;
    String lattitude, longitude,teacherLatitude,teacherLongitude;

    String uniqueId,name,registerNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        scan_btn = findViewById(R.id.scan_btn);

        mAuth= FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = firebaseDatabase.getReference();

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
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
            startActivity(new Intent(ReaderActivity.this,LoginActivity.class));
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
                final String a[];
                a= getLocation();
                Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                uniqueId= result.getContents();
                sessionDBref = firebaseDatabase.getReference().child("sessions").child(uniqueId);
                sessionDBref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        teacherLatitude= dataSnapshot.child("latitude").getValue().toString();
                        teacherLongitude= dataSnapshot.child("longitude").getValue().toString();
                        if(Math.abs(measure(Double.parseDouble(teacherLatitude),Double.parseDouble(teacherLongitude),Double.parseDouble(a[0]),Double.parseDouble(a[1])))<=7){
                            dataStudentDRef= sessionDBref.child("attendedBy").child(mAuth.getUid());
                            Map<String, String> dataToSave = new HashMap<>();
                            dataToSave.put("name", name);
                            dataToSave.put("register", registerNumber);
                            dataToSave.put("latitude", a[0]);
                            dataToSave.put("longitude", a[1]);
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
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        registerNumber =dataSnapshot.child("students").child(mAuth.getUid()).child("register").getValue().toString();
                        name = dataSnapshot.child("students").child(mAuth.getUid()).child("name").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    public String[] getLocation() {
        String loc[] = new String[2];
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(ReaderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (ReaderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ReaderActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {


            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            android.location.Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            android.location.Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                loc[0] = lattitude;
                loc[1] = longitude;

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                loc[0] = lattitude;
                loc[1] = longitude;


            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                loc[0] = lattitude;
                loc[1] = longitude;

            } else {

                Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();
                loc[0] = "0";
                loc[1] = "0";
            }


        }
        return loc;
    }

    public double measure(double lat1,double lon1,double lat2,double lon2){
        double R = 6378.137;

        double dLat = (lat2 * Math.PI / 180) - (lat1 * Math.PI / 180);
        double dLon = (lon2 * Math.PI / 180) - (lon1 * Math.PI / 180);
        double a = (Math.sin(dLat/2)) * (Math.sin(dLat/2)) +(Math.cos(lat1)) *( Math.PI / (180)) * (Math.cos(lat2)) * (Math.PI / (180)) *(Math.sin(dLon/2)) *(Math.sin(dLon/2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return ( d * 1000); // meters

    }
}
