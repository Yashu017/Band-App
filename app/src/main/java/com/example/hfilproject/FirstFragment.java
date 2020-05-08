package com.example.hfilproject;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {

    ImageButton button;
    View rootView;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    TextView usernanme,latestUpd,originalAddress;
    Double latitude,longitude;




    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPrefs = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_first, container, false);
        button = rootView.findViewById(R.id.notificationBell);
        latestUpd=rootView.findViewById(R.id.latestUpd);
        originalAddress=rootView.findViewById(R.id.originalAddress);
        usernanme=rootView.findViewById(R.id.userNameFF);
        usernanme.setText(sharedPrefs.getString("name",""));
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

        latestUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://www.mohfw.gov.in/"));
                startActivity(viewIntent);
            }
        });


        if(sharedPrefs.getString("quarantineType","").equals("N/A"))
        {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 60000);
        }

        else
        {
            originalAddress.setText(sharedPrefs.getString("address",""));
        }

        return rootView;

    }

}
