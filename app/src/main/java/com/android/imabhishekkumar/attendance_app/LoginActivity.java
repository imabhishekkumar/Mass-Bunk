package com.android.imabhishekkumar.attendance_app;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    String email, password;
    EditText emailET, passwordET;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser mUser;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    Button loginbtn;
    Boolean isStudent = false, isTeacher = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        emailET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        loginbtn = findViewById(R.id.loginbutton);
        progressDialog = new ProgressDialog(this);
        mUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        progressDialog = new ProgressDialog(this);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Verifying");
                progressDialog.show();
                loginUser();
            }
        });



    }

    private void updateUI() {
        mUser = mAuth.getCurrentUser();
        progressDialog.setMessage("Verifying");
        progressDialog.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("students").child(mUser.getUid()).exists()) {
                    isStudent = true;
                    Intent intent = new Intent(LoginActivity.this, ReaderActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                } else if (dataSnapshot.child("teachers").child(mUser.getUid()).exists()) {
                    isTeacher = true;
                    Intent intent = new Intent(LoginActivity.this, GeneratorActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            updateUI();
        }
    }

    private void loginUser() {

        mAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("OnComplete", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("onFailure", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


}
