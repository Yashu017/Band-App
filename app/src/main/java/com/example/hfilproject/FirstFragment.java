package com.example.hfilproject;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

import static com.example.hfilproject.App.CHANNEL_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {
    TextView addresesHead;
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

    int timeToFetchAddress;
    int t;

    Button typeTemp;
    int hours, minutes;
    String date;
    String token2;
    int tempReceived;
    TextView setTemp;

    String token3;
    String locReceived;


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

         timeToFetchAddress=Integer.parseInt(sharedPrefs.getString("time"," "));
         Log.e("tt",timeToFetchAddress+"");

        hours = sharedPrefs.getInt("hours", 0);
        minutes = sharedPrefs.getInt("minutes", 0);
        date = sharedPrefs.getString("dateSelected", "");



//         t=Integer.parseInt(timeToFetchAddress);
//         Log.e("t",t+"");
        //  Toast.makeText(getContext(), ""+temp, Toast.LENGTH_SHORT).show();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_first, container, false);
        button = rootView.findViewById(R.id.notificationBell);
        latestUpd = rootView.findViewById(R.id.latestUpd);
        originalAddress = rootView.findViewById(R.id.originalAddress);
        addresesHead = rootView.findViewById(R.id.addressHead);
        usernanme = rootView.findViewById(R.id.userNameFF);

        setTemp = rootView.findViewById(R.id.tempOriginal);
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


        if (!sharedPrefs.getString("time", "").equals( "0" )) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                @Override
                public void run() {

                   getGoogleApiClient();

                }
            }, 1000*60*60*timeToFetchAddress);
        } else {
            addresesHead.setVisibility(View.GONE);
            originalAddress.setText(sharedPrefs.getString("address", ""));

        }
        // SendNotification();
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
                Intent intent = new Intent(getContext(), HTActivity.class);
                startActivity(intent);
            }
        });

        //  SendReminder();

        GetTemperature();
        Get_Location();
        return rootView;


    }
    GoogleApiClient mGoogleApiClient;
    private void getGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

                            if (ActivityCompat.checkSelfPermission(getActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Log.e(getClass().getName(), "Location permission not granted");
                                return;
                            }

                            Task task = mFusedLocationClient.getLastLocation();

                            task.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        double latitude1 = location.getLatitude();
                                        double longitude1 = location.getLongitude();

                                      Geocoder  geocoder = new Geocoder(getContext(), Locale.getDefault());
                                        try {
                                            List<Address> addressList = geocoder.getFromLocation(latitude1, longitude1,1);
                                            String address1 = addressList.get(0).getAddressLine(0);
                                            String area = addressList.get(0).getLocality();
                                           String city = addressList.get(0).getAdminArea();
                                           // country = addressList.get(0).getCountryName();
                                           // postalCode = addressList.get(0).getPostalCode();
                                           String fulladdress = address1 + ",\n " + area + ",\n " ;
                                            Log.e("location", "" + fulladdress);
                                            originalAddress.setText(fulladdress);
                                        } catch (Exception e) {
                                            e.printStackTrace();

                                        }

                                    }
                                    else
                                    {
                                        originalAddress.setText("Unable to fetch location.");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.e(getClass().getName(), "onConnectionSuspended() ");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.e(getClass().getName(), "Get location failure : " + connectionResult.getErrorMessage());
                        }
                    })
                    .build();
        }
        mGoogleApiClient.connect();
    }



    private void Get_Location() {
        token3 = sharedPrefs.getString("token", "");
        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();
        for_login login = retrofit.create(for_login.class);
        Call<GetLocation> call = login.getLoc(token3);
        call.enqueue(new Callback<GetLocation>() {
            @Override
            public void onResponse(Call<GetLocation> call, Response<GetLocation> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    GetLocation getLocation = response.body();
                    locReceived = getLocation.getLocation();
                    Log.e("Success", "" + response.code());

                }
            }

            @Override
            public void onFailure(Call<GetLocation> call, Throwable t) {
                Toast.makeText(getContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error", "" + t.getMessage());
            }
        });

    }

    private void GetTemperature() {
        token2 = sharedPrefs.getString("token", "");
        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();
        for_login login = retrofit.create(for_login.class);
        Call<GetTemp> call = login.getTemp(token2);
        call.enqueue(new Callback<GetTemp>() {
            @Override
            public void onResponse(Call<GetTemp> call, Response<GetTemp> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    GetTemp getTemp = response.body();
                    tempReceived = getTemp.getTemperature();
                    Log.e("Success", "" + response.code());
                    setTemp.setText("" + tempReceived);
                }
            }

            @Override
            public void onFailure(Call<GetTemp> call, Throwable t) {
                Toast.makeText(getContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error", "" + t.getMessage());
            }
        });
    }

    private void SendReminder() {

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String currentDate = sdf.format(new Date());
        if (date == currentDate) {
            if (hours == hour && minutes == min) {

                Toast.makeText(getContext(), "" + hour, Toast.LENGTH_SHORT).show();

                /*
                Intent i = new Intent(getContext(), FirstFragment.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent pd = PendingIntent.getActivity(getContext(), 2, i, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                        .setContentTitle("Reminder")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                        .setContentText("Please update your current location.")
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setContentIntent(pd)
                        .setAutoCancel(true);
                NotificationManagerCompat manager = NotificationManagerCompat.from(getContext());
                manager.notify(1, builder.build());
                */
            }
        }

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

