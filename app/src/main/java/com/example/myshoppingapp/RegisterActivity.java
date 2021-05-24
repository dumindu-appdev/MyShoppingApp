package com.example.myshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtname,txtemail,txtphone,txtpassword;
    private Button btnregister;
    private TextView tvregister;
    FirebaseAuth authentication;
    DatabaseReference db_ref;
    private final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txtname = findViewById(R.id.txtname);
        txtemail = findViewById(R.id.txtemail);
        txtphone = findViewById(R.id.txtphone);
        txtpassword = findViewById(R.id.txtpass);
        btnregister = findViewById(R.id.btnregister);
        tvregister = findViewById(R.id.tvregister);

        authentication = FirebaseAuth.getInstance();

        tvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name,email,phone,password;
                name =  txtname.getText().toString();
                email = txtemail.getText().toString();
                phone = txtphone.getText().toString();
                password = txtpassword.getText().toString();

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    createUser(email, password, name, phone);
                }
            }
        });

    }

    private void createUser(String email, String password,String name,String phone) {
        authentication.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.i("RegisterActivity","Successfully registered");
                    String id = authentication.getCurrentUser().getUid();
                    createCustomer(id,name,email,phone);
                }
                else{
                    Toast.makeText(RegisterActivity.this, "User registration failure...", Toast.LENGTH_SHORT).show();
                    Exception e = task.getException();
                    Log.e("RegisterActivity",e.getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Registration failed, try again...", Toast.LENGTH_SHORT).show();
                Log.e("RegisterActivity",e.getMessage());
            }
        });
    }

    private void createCustomer(String id, String name, String email, String phone) {
        db_ref = FirebaseDatabase.getInstance().getReference();
        Customer cust = new Customer(id,name,"",email,phone);
        db_ref.child("Customer").child(id).setValue(cust);
        Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
        finish();
    }
}
