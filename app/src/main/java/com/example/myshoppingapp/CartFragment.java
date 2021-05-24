package com.example.myshoppingapp;

import android.accessibilityservice.GestureDescription;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;


public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private static TextView grandtot;
    private Button btncout;
    private DatabaseReference dbRef;
    private FirebaseAuth auth;
    private CartAdapter cAdapter;
    private ArrayList<Cart> cList;
    private ProgressDialog pd;
    public static ArrayList<Cart> selectedItems;
    private final String TAG = "CartFragment";

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.rec_cart);
        grandtot = view.findViewById(R.id.cart_view_total);
        btncout = view.findViewById(R.id.cart_view_btn_cout);
        dbRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        cList = new ArrayList<>();
        cAdapter = new CartAdapter(cList,getContext());
        recyclerView.setAdapter(cAdapter);
        pd = new ProgressDialog(getContext());
        selectedItems = new ArrayList<>();

        populateAdapter(uid);

        btncout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedItems.size()==0){
                    Toast.makeText(getContext(), "Please select an item to goto the checkout", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Goto Payments
                    getFragmentManager().beginTransaction().replace(R.id.frag_container,new PaymentFragment(selectedItems)).commit();
                }
            }
        });

        return view;
    }


    public static void  updateGrandTotal(String value){
        grandtot.setText(value);
    }

    private void populateAdapter(String uid) {
        pd.setMessage("Loading cart items...");
        pd.show();
        dbRef.child("Cart").child(uid).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                   Cart cart = dataSnapshot.getValue(Cart.class);
                   cList.add(cart);
                }
                cAdapter.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}

