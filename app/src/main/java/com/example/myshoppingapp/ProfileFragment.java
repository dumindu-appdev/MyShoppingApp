package com.example.myshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private View view;
    private EditText txtname,txtemail,txtphone,txtaddess;
    private Button btnsignout,btnedit,btnorders;
    private FirebaseAuth authentication;
    private FirebaseUser user;
    private DatabaseReference dbRef;
    private ProgressDialog pd;
    private final String TAG = "ProfileFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        btnsignout = view.findViewById(R.id.btnsignout);
        txtname = view.findViewById(R.id.pro_name);
        txtemail = view.findViewById(R.id.pro_email);
        txtphone = view.findViewById(R.id.pro_phone);
        txtaddess = view.findViewById(R.id.pro_address);
        btnedit = view.findViewById(R.id.pro_btnedit);
        btnorders = view.findViewById(R.id.pro_btnorders);
        authentication = FirebaseAuth.getInstance();
        user = authentication.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(getContext());

        loadProfile(user);

        btnsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                //getFragmentManager().beginTransaction().replace(R.id.frag_container,new HomeFragment()).commit();
            }
        });

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = btnedit.getText().toString();
                if (label.equals("EDIT PROFILE")){
                    CTRL_MGT(true);
                    btnedit.setText("SAVE PROFILE");
                }
                else if (label.equals("SAVE PROFILE")){
                    saveProfile(user);
                }
            }
        });

        btnorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().add(R.id.frag_container,new OrderHistoryFragment()).commit();
            }
        });

        return view;
    }

    private void saveProfile(FirebaseUser user) {
        String name = txtname.getText().toString();
        String email = txtemail.getText().toString();
        String phone = txtphone.getText().toString();
        String address = txtaddess.getText().toString();
        String uid = user.getUid();

        if (name.equals("") || email.equals("") || phone.equals("") || address.equals("")){
            Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
        else{
            Customer cust = new Customer();
            cust.setCust_id(uid);
            cust.setCust_name(name);
            cust.setCust_email(email);
            cust.setCust_phone(phone);
            cust.setCust_address(address);

            dbRef.child("Customer").child(uid).setValue(cust, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error==null){
                        CTRL_MGT(false);
                        btnedit.setText("EDIT PROFILE");
                        Toast.makeText(getContext(), "Successfully saved", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Error, try again...", Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"Record Update Error: "+error.getMessage());
                    }
                }
            });
        }
    }

    private void loadProfile(FirebaseUser user) {
        pd.setMessage("Loading...");
        pd.show();
        String uid = user.getUid();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Customer cust = snapshot.child("Customer").child(uid).getValue(Customer.class);
                txtname.setText(cust.getCust_name());
                txtemail.setText(cust.getCust_email());
                txtphone.setText(cust.getCust_phone());
                txtaddess.setText(cust.getCust_address());

                CTRL_MGT(false);
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,"DB Error: "+error.getMessage().toString());
            }
        });
    }

    private void CTRL_MGT(boolean status){
        txtname.setEnabled(status);
        txtemail.setEnabled(status);
        txtphone.setEnabled(status);
        txtaddess.setEnabled(status);
    }
}