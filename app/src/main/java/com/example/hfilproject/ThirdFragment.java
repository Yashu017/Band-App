package com.example.hfilproject;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment {

    private RecyclerView recyclerView;
    static  List<TempLog> tempLogs;
    View rootView;
    private OnFragmentInteractionListener listener;

    ArrayList<String> dates;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    int status;

    float celsius, fharenheit;

    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_third, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.tempRecycler);
        //  TempAdapter tempAdapter = new TempAdapter(getContext(), tempLogs);
        // recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // recyclerView.setAdapter(tempAdapter);
        sharedPrefs = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        TempAdapter tempAdapter = new TempAdapter(getContext(), tempLogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(tempAdapter);



        return rootView;
    }

    @Override
    public void onResume() {


        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           tempLogs = new ArrayList<>();
         tempLogs.add(new TempLog("36.0 \u00B0C", "98.0 \u00B0F", "Normal", "Today,", "12:00 PM"));
        // tempLogs.add(new TempLog("37.0 \u00B0C", "98.6 \u00B0F", "Normal", "Today,", "1:00 PM"));
        // tempLogs.add(new TempLog("38.0 \u00B0C", "100.4 \u00B0F", "Normal", "Today,", "2:00 PM"));
        // tempLogs.add(new TempLog("36.0 \u00B0C", "98.0 \u00B0F", "Normal", "Today,", "3:00 PM"));
        // tempLogs.add(new TempLog("37.0 \u00B0C", "98.6 \u00B0F", "Normal", "Today,", "4:00 PM"));
        //tempLogs.add(new TempLog("36.0 \u00B0C", "98.0 \u00B0F", "Normal", "Today,", "5:00 PM"));
        // tempLogs.add(new TempLog("38.0 \u00B0C", "100.4 \u00B0F", "Normal", "Today,", "6:00 PM"));

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener {
        void onClicked();
    }
}
