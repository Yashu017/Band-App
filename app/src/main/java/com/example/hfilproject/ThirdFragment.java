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


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


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
    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> dates;

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
        barChart = (BarChart) rootView.findViewById(R.id.BarChart);
        ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
        barEntryArrayList.add(new BarEntry(98f, 0));
        barEntryArrayList.add(new BarEntry(99f, 1));
        barEntryArrayList.add(new BarEntry(100.2f, 2));
        barEntryArrayList.add(new BarEntry(101f, 3));
        barEntryArrayList.add(new BarEntry(102f, 4));
        barEntryArrayList.add(new BarEntry(103f, 5));
        barEntryArrayList.add(new BarEntry(103f, 5));
        ArrayList<String> dates = new ArrayList<>();
        dates.add("20 April");
        dates.add("21 April");
        dates.add("22 April");
        dates.add("23 April");
        dates.add("24 April");
        dates.add("25 April");
        dates.add("26 April");

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Temp Recorded");
        barDataSet.setColor(R.color.md_deep_purple_500);
        BarData barData = new BarData(dates, barDataSet);
        barChart.setData(barData);
        barChart.setDescription("");
        barChart.animateY(2000);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tempLogs = new ArrayList<>();
        tempLogs.add(new TempLog("36.0 \u00B0C", "98.0 \u00B0F", "Normal", "Today,", "12:00 PM"));
        tempLogs.add(new TempLog("37.0 \u00B0C", "98.6 \u00B0F", "Normal", "Today,", "1:00 PM"));
        tempLogs.add(new TempLog("38.0 \u00B0C", "100.4 \u00B0F", "Normal", "Today,", "2:00 PM"));
        tempLogs.add(new TempLog("36.0 \u00B0C", "98.0 \u00B0F", "Normal", "Today,", "3:00 PM"));
        tempLogs.add(new TempLog("37.0 \u00B0C", "98.6 \u00B0F", "Normal", "Today,", "4:00 PM"));
        tempLogs.add(new TempLog("36.0 \u00B0C", "98.0 \u00B0F", "Normal", "Today,", "5:00 PM"));
        tempLogs.add(new TempLog("38.0 \u00B0C", "100.4 \u00B0F", "Normal", "Today,", "6:00 PM"));

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
