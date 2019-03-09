package com.android.imabhishekkumar.attendance_app;


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



public class LoginActivity extends AppCompatActivity {
    String  email, password;
    EditText emailET, passwordET;
    FirebaseAuth mAuth;
    Button loginbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth= FirebaseAuth.getInstance();
        emailET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
       loginbtn = findViewById(R.id.loginbutton);
       loginbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               loginUser();
           }
       });


    }

   /* @Override
    protected void onStart() {
        super.onStart();
        if(mAuth!=null)
        { startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }*/

    private void loginUser() {

        mAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("OnComplete", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
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
