package com.example.myshoppingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {
    private Button btnadd,btnsignout,btnstock;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        btnadd = findViewById(R.id.admin_product_add);
        btnstock = findViewById(R.id.admin_product_stock);
        btnsignout = findViewById(R.id.admin_signout);
        auth = FirebaseAuth.getInstance();

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this,AddProductActivity.class));
            }
        });

        btnstock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this,AddStockActivity.class));
            }
        });

        btnsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(AdminHomeActivity.this,MainActivity.class));
                finish();
            }
        });
    }
}