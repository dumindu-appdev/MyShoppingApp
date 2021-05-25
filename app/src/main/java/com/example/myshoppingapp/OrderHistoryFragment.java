package com.example.myshoppingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class OrderHistoryFragment extends Fragment {

    private TextView txtback;
    private Fragment cfrag;

    public OrderHistoryFragment() {
        // Required empty public constructor
        this.cfrag = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_order_history, container, false);
        txtback = view.findViewById(R.id.orderhist_back);

        txtback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(cfrag).commit();
            }
        });
        return view;
    }
}