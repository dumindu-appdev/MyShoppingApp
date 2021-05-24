package com.example.myshoppingapp;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class HomeFragment extends Fragment {
    private  View view;
    private CardView cardView1,cardView2,cardView3,cardView4;
    private FragmentManager frag_man;
    private FragmentTransaction frag_tra;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_home, container, false);
        cardView1 = view.findViewById(R.id.card_view1);
        cardView2 = view.findViewById(R.id.card_view2);
        cardView3 = view.findViewById(R.id.card_view3);
        cardView4 = view.findViewById(R.id.card_view4);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProductViewFragment("Sri Lankan Foods","Sri Lankan"));
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProductViewFragment("Indian Foods","Indian"));
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProductViewFragment("Chinese Foods","Chinese"));
            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProductViewFragment("Italian Foods","Italian"));
            }
        });
        return view;
    }

    private void loadFragment(Fragment fragment) {
        frag_man  = getFragmentManager();
        frag_tra = frag_man.beginTransaction();
        frag_tra.add(R.id.frag_container,fragment);
        frag_tra.commit();
    }
}