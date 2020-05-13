package com.example.hfilproject;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
public class FirstFragment extends Fragment {

    ImageButton button;
    View rootView;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    TextView usernanme, latestUpd, originalAddress;
    Double latitude, longitude;
    ImageView tp;
    String token;
    int temp;
    Retrofit retrofit;

    String sendToken;

    int status;
    String sendTokenBle;
    String token1;
    int geoStatus;

    int connected;
    Button typeTemp;

    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPrefs = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        token = sharedPrefs.getString("token", "");
        temp = sharedPrefs.getInt("temperature", 0);
        connected = sharedPrefs.getInt("Connection Status", 0);
        geoStatus = sharedPrefs.getInt("geoStatus", 0);

        //  Toast.makeText(getContext(), ""+temp, Toast.LENGTH_SHORT).show();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_first, container, false);
        button = rootView.findViewById(R.id.notificationBell);
        latestUpd = rootView.findViewById(R.id.latestUpd);
        originalAddress = rootView.findViewById(R.id.originalAddress);
        usernanme = rootView.findViewById(R.id.userNameFF);

        tp = rootView.findViewById(R.id.tp);
        /*
        tp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendTemperature();
            }
        });

         */
        usernanme.setText(sharedPrefs.getString("name", ""));
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


        if (sharedPrefs.getString("quarantineType", "").equals("N/A")) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 60000);
        } else {
            originalAddress.setText(sharedPrefs.getString("address", ""));
        }
        SendNotification();
/*
        final Handler handler = new Handler();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent serviceIntent = new Intent(getContext(), LocationIntentService.class);
                        serviceIntent.putExtra("inputExtra", "Temperature");
                        ContextCompat.startForegroundService(getContext(), serviceIntent);

                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 20 * 1000);
*/

       typeTemp = rootView.findViewById(R.id.typeTemp);
       typeTemp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(),ViewTemperature.class);
               startActivity(intent);
           }
       });
        return rootView;


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void SendNotification() {
        int categoryType = 0;
        String connectionStatus = "Bluetooth Disconnected";
        String geofenceStatus = "Geo fence breached.";
        token1 = sharedPrefs.getString("token", "");

        if (connected != 1) {
            OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpbuilder.addInterceptor(logging);

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                    .addConverterFactory(GsonConverterFactory.create());

            retrofit = builder.build();
            for_login login = retrofit.create(for_login.class);
            Map<String, Object> params = new HashMap<>();
            params.put("notification", connectionStatus);
            params.put("category", categoryType);
            Call<UserNotification> call = login.userNotify(token1, params);
            call.enqueue(new Callback<UserNotification>() {
                @Override
                public void onResponse(Call<UserNotification> call, Response<UserNotification> response) {
                    String error;
                    if (response.isSuccessful() && response.code() == 200) {
                        if (response.body().getErrorCode() != null) {
                            error = response.body().getErrorCode();
                            if (error.equals("2")) {
                                Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        sendTokenBle = response.body().getToken();
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserNotification> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("error", "" + t.getMessage());
                }
            });
        }

        if (geoStatus != 1) {
            OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpbuilder.addInterceptor(logging);

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                    .addConverterFactory(GsonConverterFactory.create());

            retrofit = builder.build();
            for_login login = retrofit.create(for_login.class);
            Map<String, Object> params = new HashMap<>();
            params.put("notification", geofenceStatus);
            params.put("category", categoryType);
            Call<UserNotification> call = login.userNotify(token1, params);
            call.enqueue(new Callback<UserNotification>() {
                @Override
                public void onResponse(Call<UserNotification> call, Response<UserNotification> response) {
                    String error;
                    if (response.isSuccessful() && response.code() == 200) {
                        if (response.body().getErrorCode() != null) {
                            error = response.body().getErrorCode();
                            if (error.equals("2")) {
                                Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        sendTokenBle = response.body().getToken();
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserNotification> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("error", "" + t.getMessage());
                }
            });
        }


    }

    private void SendTemperature() {
        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        for_login login = retrofit.create(for_login.class);
        Map<String, Object> params = new HashMap<>();
        params.put("temperature", temp);

        Call<UserTemp> tempCall = login.userTemp(token, params);
        tempCall.enqueue(new Callback<UserTemp>() {
            @Override
            public void onResponse(Call<UserTemp> call, Response<UserTemp> response) {
                String error;
                if (response.isSuccessful() && response.code() == 200) {
                    if (response.body().getErrorCode() != null) {
                        error = response.body().getErrorCode();
                        if (error.equals("2")) {
                            Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    sendToken = response.body().getToken();
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserTemp> call, Throwable t) {
                Toast.makeText(getContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error", "" + t.getMessage());
            }
        });

    }

}

