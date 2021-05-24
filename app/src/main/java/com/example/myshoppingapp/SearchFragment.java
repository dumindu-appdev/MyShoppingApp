package com.example.myshoppingapp;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private EditText txtsearch;
    private Button btnsearch;
    private RecyclerView rv_search;
    private ProductAdapter pAdapter;
    private ArrayList<Product> pList;
    private ArrayList<Product> allProdList;
    private DatabaseReference dbRef;
    private ProgressDialog pd;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        txtsearch = view.findViewById(R.id.search_text);
        btnsearch = view.findViewById(R.id.search_btn_search);
        rv_search = view.findViewById(R.id.rv_search_products);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        rv_search.setLayoutManager(gridLayoutManager);
        allProdList = new ArrayList<>();
        pList = new ArrayList<>();
        pAdapter = new ProductAdapter(pList,getContext());
        rv_search.setAdapter(pAdapter);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Products");
        pd =  new ProgressDialog(getContext());

        //To fill all the products to the allProdList
        getAllProducts();

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtsearch.getText().toString().equals(""))
                    searchProducts(txtsearch.getText().toString());
                else
                    Toast.makeText(getContext(), "Please enter the search text", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void getAllProducts() {
        pd.setMessage("Loading...");
        pd.show();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Product prod = ds.getValue(Product.class);
                    allProdList.add(prod);
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchProducts(String name) {
        pd.setMessage("Searching");
        pd.show();
        pList.clear();
        boolean result = false;
        for (Product prod : allProdList){
            if (prod.getProd_name().toLowerCase().contains(name.toLowerCase())){
                pList.add(prod);
                result = true;
            }
            pAdapter.notifyDataSetChanged();
            pd.dismiss();
        }
        if(result==false){
            Toast.makeText(getContext(), "No results found for :"+name, Toast.LENGTH_SHORT).show();
            pList.clear();
        }
    }
}