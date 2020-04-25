package com.example.hfilproject;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<TempLog> tempLogs;
    View rootView;
    private OnFragmentInteractionListener listener;


    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_third, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.tempRecycler);
        TempAdapter tempAdapter = new TempAdapter(getContext(), tempLogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(tempAdapter);



        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tempLogs = new ArrayList<>();
        tempLogs.add(new TempLog("36 \u00B0C", "98 \u00B0F", "Normal", "Today,", "12:00 PM"));
        tempLogs.add(new TempLog("37 \u00B0C", "98.6 \u00B0F", "Normal", "Today,", "1:00 PM"));
        tempLogs.add(new TempLog("38 \u00B0C", "100.4 \u00B0F", "Normal", "Today,", "2:00 PM"));
        tempLogs.add(new TempLog("36 \u00B0C", "98 \u00B0F", "Normal", "Today,", "3:00 PM"));
        tempLogs.add(new TempLog("37 \u00B0C", "98.6 \u00B0F", "Normal", "Today,", "4:00 PM"));
        tempLogs.add(new TempLog("36 \u00B0C", "98 \u00B0F", "Normal", "Today,", "5:00 PM"));
        tempLogs.add(new TempLog("38 \u00B0C", "100.4 \u00B0F", "Normal", "Today,", "6:00 PM"));

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
