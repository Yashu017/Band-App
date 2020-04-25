package com.example.hfilproject;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FourthFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<BandDetail> bandDetails;
    private OnFragmentInteractionListener listener;
    View rootView;

    public FourthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_fourth, container, false);
        /*
        recyclerView = (RecyclerView) rootView.findViewById(R.id.bandRecycler);
        BandDetailAdapter bandDetailAdapter = new BandDetailAdapter(getContext(), bandDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(bandDetailAdapter);


         */
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bandDetails = new ArrayList<>();
        bandDetails.add(new BandDetail("First Description Is Here", R.drawable.cold));
        bandDetails.add(new BandDetail("Second Description Is Here", R.drawable.cold));
        bandDetails.add(new BandDetail("Third Description Is Here", R.drawable.cold));
        bandDetails.add(new BandDetail("Fourth Description Is Here", R.drawable.cold));

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

