package com.example.myshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText txtemail,txtpassword;
    private Button btnlogin;
    private FirebaseAuth authentication;
    private TextView tvlink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtemail = findViewById(R.id.txtemail);
        txtpassword = findViewById(R.id.txtpassword);
        btnlogin = findViewById(R.id.btnlogin);
        authentication = FirebaseAuth.getInstance();
        tvlink = findViewById(R.id.tvlink);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,password;
                email = txtemail.getText().toString();
                password = txtpassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_LONG).show();
                }
                else{
                    validateUser(email,password);
                }

            }
        });

        tvlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent int_reg = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(int_reg);
                finish();
            }
        });
    }

    private void validateUser(String email, String password) {
        authentication.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Valid User", Toast.LENGTH_SHORT).show();
                if(email.equals("admin@efoods.lk")){
                    startActivity(new Intent(LoginActivity.this,AdminHomeActivity.class));
                }
                else{
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Invalid username or password, please try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
}