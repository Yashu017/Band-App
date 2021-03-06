package com.example.hfilproject;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
public class FifthFragment extends Fragment {

    private RecyclerView recyclerView;
    private LottieAnimationView animationView;
    private List<Notification> notificationList;
    View rootView;
    private OnFragmentInteractionListener listener;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    String token4;
    Retrofit retrofit;
    TextView tv2;
    Button back;
    TextView wait;

    private ArrayList<NotificationItem> arrayList = null;



    public FifthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_fifth, container, false);
        animationView = (LottieAnimationView) rootView.findViewById(R.id.animation_view);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.notificationRecycler);
        wait=rootView.findViewById(R.id.wait);
        back = rootView.findViewById(R.id.backNoti);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FirstFragment name = new FirstFragment();
                fragmentTransaction.replace(R.id.fragment_container, name);
                fragmentTransaction.commit();
            }
        });
getNotification();





        tv2 = rootView.findViewById(R.id.textView2);
        return rootView;
    }


    private void getNotification() {
animationView.playAnimation();
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
                if (response.isSuccessful()) {

                    Log.e("Response", "" + response.message());
                    Log.e("Content", "" + response.body().getNotification());
                    GetNotification notification = response.body();

                    arrayList = notification.getNotification();



                    if (arrayList != null) {
                        Collections.reverse(arrayList);
                        int length = arrayList.size();
                        Log.e("Length", "" + length);
                        for (NotificationItem item : arrayList) {

                            String msg = item.getNotification();
                            long time = item.getTime();
                            time=time+(5*60*60*1000)+(30*60*1000);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d");
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mma");
                            Date date = new Date(time);
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String formattedTime = simpleDateFormat.format(date);
                            String formattedTime1 = simpleDateFormat1.format(date);
                            Log.e("Msg", "" + msg);
                                notificationList.add(new Notification("", "" + formattedTime+"." , "" +formattedTime1,""+ msg));
                        }

                        NotificationAdapter adapter = new NotificationAdapter(getContext(), notificationList);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        wait.setVisibility(View.GONE);
                        animationView.pauseAnimation();
                        animationView.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        notificationList.add(new Notification("Info", "00::00", "N/A","" + "No new Notification"));
                        NotificationAdapter adapter = new NotificationAdapter(getContext(), notificationList);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                        wait.setVisibility(View.GONE);
                        animationView.pauseAnimation();
                        animationView.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetNotification> call, Throwable t) {
                Toast.makeText(getContext(), "Weak or No Internet", Toast.LENGTH_SHORT).show();
                Log.e("error", "" + t.getMessage());
            }
        });


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationList = new ArrayList<>();

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
