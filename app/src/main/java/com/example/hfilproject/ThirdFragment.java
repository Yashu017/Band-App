package com.example.hfilproject;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
public class ThirdFragment extends Fragment {
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;
    private   List<TempLog> tempLogs;
    View rootView;
    private OnFragmentInteractionListener listener;
    TextView wait;
    ArrayList<String> dates;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    int status;

    float celsius, fharenheit;
    String token;
    Retrofit retrofit;
    public static final  String TAG = ThirdFragment.class.getName();
    private ArrayList<TempItem> arrayList = null;
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
        animationView = (LottieAnimationView) rootView.findViewById(R.id.animation_view3);
        wait=rootView.findViewById(R.id.wait3);

        sharedPrefs = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        TempAdapter tempAdapter = new TempAdapter(getContext(), tempLogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(tempAdapter);

        GetTemperature();

        return rootView;
    }

    private void GetTemperature() {
        animationView.playAnimation();
        sharedPrefs = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        token = sharedPrefs.getString("token", "");

        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();
        for_login login = retrofit.create(for_login.class);
        Call<GetTemp> call  = login.getTemp(token);
        call.enqueue(new Callback<GetTemp>() {
            @Override
            public void onResponse(Call<GetTemp> call, Response<GetTemp> response) {
                if (response.isSuccessful()){
                    Log.e(TAG,""+response.code());
                    GetTemp getTemp = response.body();
                    arrayList = getTemp.getTemperature();
                    if (arrayList!=null){
                        Collections.reverse(arrayList);
                        for (TempItem item:arrayList){
                            float temp = item.getTemperature();
                            long time = item.getTime();
                            time=time+(5*60*60*1000)+(30*60*1000);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, hh:mma");
                            Date date = new Date(time);
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String formattedTime = simpleDateFormat.format(date);
                            float celsius = (temp-32)*1.8f;
                            tempLogs.add(new TempLog(celsius,temp,"Normal","Today",formattedTime));
                        }
                        TempAdapter tempAdapter = new TempAdapter(getContext(),tempLogs);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        wait.setVisibility(View.GONE);
                        animationView.pauseAnimation();
                        animationView.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(tempAdapter);

                    }else{
                        tempLogs.add(new TempLog(0,32,"No log Recorded","--","--"));
                        TempAdapter tempAdapter = new TempAdapter(getContext(),tempLogs);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        wait.setVisibility(View.GONE);
                        animationView.pauseAnimation();
                        animationView.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(tempAdapter);

                    }

                }
            }

            @Override
            public void onFailure(Call<GetTemp> call, Throwable t) {
                Toast.makeText(getContext(), "Failed" + "No or weak internet connection", Toast.LENGTH_SHORT).show();
                Log.e("error", "" + t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {


        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           tempLogs = new ArrayList<>();
        // tempLogs.add(new TempLog("36.0 \u00B0C", "98.0 \u00B0F", "Normal", "Today,", "12:00 PM"));
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
