package com.example.hfilproject;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FifthFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Notification> notifications;
    View rootView;
    private OnFragmentInteractionListener listener;

    public FifthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_fifth, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.notificationRecycler);
        NotificationAdapter adapter = new NotificationAdapter(getContext(), notifications);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifications = new ArrayList<>();
        notifications.add(new Notification("Warning", "11:00 AM", "This device is not connected with C Watch. Please connect again."));
        notifications.add(new Notification("Warning", "1:00 PM", "You have breached geo-fencing.Please stay in your quarantine place."));
        notifications.add(new Notification("Warning", "3:00 PM", "This device is not connected with C Watch. Please connect again."));

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
