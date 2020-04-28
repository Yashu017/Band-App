package com.example.hfilproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {

    ImageButton button;
    View rootView;

    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_first, container, false);
        button = rootView.findViewById(R.id.notificationBell);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FifthFragment fifthFragment = new FifthFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fifthFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return rootView;

    }

}
