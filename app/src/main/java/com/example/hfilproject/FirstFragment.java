package com.example.hfilproject;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    Button addresesHead;
    Button button;
    View rootView;
    String out;
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

    RelativeLayout typeTemp;
    int hours, minutes;
    String date;
    String token2;
    float tempReceived;
    TextView setTemp;

    String token3;
    String locReceived;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;



    public FirstFragment() {
        // Required empty public constructor

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPrefs = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        rootView = inflater.inflate(R.layout.fragment_first, container, false);
        button = rootView.findViewById(R.id.notificationBell);
        originalAddress = rootView.findViewById(R.id.originalAddress);
        addresesHead = rootView.findViewById(R.id.addressHead);
        usernanme = rootView.findViewById(R.id.userNameFF);

        setTemp = rootView.findViewById(R.id.tempOriginal);
        tp = rootView.findViewById(R.id.tp);

        token = sharedPrefs.getString("token", "");
        temp = sharedPrefs.getInt("temperature", 0);
        connected = sharedPrefs.getInt("Connection Status", 0);
        geoStatus = sharedPrefs.getInt("geoStatus", 0);

        timeToFetchAddress = Integer.parseInt(sharedPrefs.getString("time", " "));
        Log.e("tt", timeToFetchAddress + "");

        hours = sharedPrefs.getInt("hours", 0);
        minutes = sharedPrefs.getInt("minutes", 0);
        date = sharedPrefs.getString("dateSelected", "");


        SimpleDateFormat dateFormat= new SimpleDateFormat("hh");
        SimpleDateFormat dt=new SimpleDateFormat("hh:mm:ss");
        try {
            Date date1 = dateFormat.parse(sharedPrefs.getString("time", " "));
             out= dt.format(date1);
            Log.e("Time", ""+out);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            Log.e("Error", ""+e);
        }

        String[] units = out.split(":"); //will break the string up into an array
        int minutes = Integer.parseInt(units[1]); //first element
        int seconds = Integer.parseInt(units[2]); //second element
        int hours=Integer.parseInt(units[0]);
        int duration = (3600*hours+60 * minutes + seconds)*1000; //add up our values


        mTimeLeftInMillis=duration;
        Log.e("Time1", ""+duration);
        if(sharedPrefs.getBoolean("firstTime",false) == true){
            editor.putString("hqAddress","N/A");
            editor.putBoolean("firstTime",false);
            editor.commit();
        }

        if(!sharedPrefs.getString("hqAddress","").equals("N/A"))
        {
            addresesHead.setVisibility(View.GONE);
            originalAddress.setText(sharedPrefs.getString("hqAddress",""));

        }

        if (!sharedPrefs.getString("time", "").equals("0")) {
            addresesHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTimer();
                }
            });
        } else {
            addresesHead.setVisibility(View.GONE);
            originalAddress.setText(sharedPrefs.getString("address", ""));

        }







//         t=Integer.parseInt(timeToFetchAddress);
//         Log.e("t",t+"");
        //  Toast.makeText(getContext(), ""+temp, Toast.LENGTH_SHORT).show();

        // Inflate the layout for this fragment

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

        return rootView;


    }

    private void startTimer() {
        Toast.makeText(getContext(),"Timer has started ",Toast.LENGTH_LONG).show();

        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
                int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                addresesHead.setVisibility(View.GONE);


            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                addresesHead.setVisibility(View.GONE);
                getGoogleApiClient();
            }
        }.start();
        mTimerRunning = true;
        addresesHead.setClickable(false);


    }

    GoogleApiClient mGoogleApiClient;

    private void getGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

                            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Log.e(getClass().getName(), "Location permission not granted");
                                Toast.makeText(getContext(),"Location permission was not granted ",Toast.LENGTH_LONG).show();
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

                                        editor.putFloat("lat",(float) latitude1);
                                        editor.putFloat("long",(float) longitude1);
                                        editor.commit();

                                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                        try {
                                            List<Address> addressList = geocoder.getFromLocation(latitude1, longitude1, 1);
                                            String address1 = addressList.get(0).getAddressLine(0);
                                            String area = addressList.get(0).getLocality();
                                            String city = addressList.get(0).getAdminArea();
                                            // country = addressList.get(0).getCountryName();
                                            // postalCode = addressList.get(0).getPostalCode();
                                            String fulladdress = address1 + ". ";
                                            Log.e("location", "" + fulladdress);
                                            originalAddress.setText(fulladdress);
                                            addresesHead.setVisibility(View.GONE);
                                            editor.putString("hqAddress",fulladdress);
                                            editor.putBoolean("done",true);
                                            editor.commit();

                                        } catch (Exception e) {
                                            e.printStackTrace();

                                        }

                                    } else {
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


    private void GetTemperature() {
        token= sharedPrefs.getString("token", "");

        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpbuilder.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();
        for_login login = retrofit.create(for_login.class);
        Call<GetTemp> call = login.getTemp(token);
        call.enqueue(new Callback<GetTemp>() {
            @Override
            public void onResponse(Call<GetTemp> call, Response<GetTemp> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    GetTemp getTemp = response.body();
                    if(getTemp!=null)
                    {
                    tempReceived = getTemp.getTemperature();
                    Log.e("Success", "" + response.code());
                    Toast.makeText(getContext(), "Temperature received from server.", Toast.LENGTH_SHORT).show();
                    setTemp.setText(String.format("%.2f", tempReceived));
                }}
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
    @Override
    public void onStart() {
        super.onStart();
        mTimerRunning = sharedPrefs.getBoolean("timerRunning", false);
        if (mTimerRunning) {
            mEndTime = sharedPrefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                getGoogleApiClient();
                addresesHead.setVisibility(View.GONE);
            }
            else
            {
                startTimer();
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }


}

