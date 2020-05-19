package com.example.hfilproject;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class FifthFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Notification> notifications;
    View rootView;
    private OnFragmentInteractionListener listener;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    String token4;
    Retrofit retrofit;
    String notif;
    TextView tv2;

    public FifthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_fifth, container, false);
        //  recyclerView = (RecyclerView) rootView.findViewById(R.id.notificationRecycler);
        // NotificationAdapter adapter = new NotificationAdapter(getContext(), notifications);
        //  LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        // recyclerView.setLayoutManager(layoutManager);
        // recyclerView.setAdapter(adapter);
     //   getNotification();

        tv2 = rootView.findViewById(R.id.textView2);
        return rootView;
    }

    private void getNotification() {
        sharedPrefs = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        token4 = sharedPrefs.getString("token", "");

        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();
        for_login login = retrofit.create(for_login.class);
        Call<GetNotification> call = login.getNoti(token4);
        call.enqueue(new Callback<GetNotification>() {
            @Override
            public void onResponse(Call<GetNotification> call, Response<GetNotification> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    GetNotification getNotification = response.body();
                    notif = getNotification.getNotification();
                    tv2.setText("" + notif);
                    Log.e("Success", "" + response.code());

                }
            }

            @Override
            public void onFailure(Call<GetNotification> call, Throwable t) {
                Toast.makeText(getContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error", "" + t.getMessage());
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    notifications = new ArrayList<>();
        //   notifications.add(new Notification("Warning", "11:00 AM", "" + notif));
        //   notifications.add(new Notification("Warning", "1:00 PM", "You have breached geo-fencing.Please stay in your quarantine place."));
        //   notifications.add(new Notification("Warning", "3:00 PM", "This device is not connected with C Watch. Please connect again."));

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
