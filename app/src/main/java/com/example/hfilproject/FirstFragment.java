package com.example.hfilproject;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;


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
    ImageView tp;
    String token;

    Retrofit retrofit;

    int timeToFetchAddress;


    RelativeLayout typeTemp;
    int hours, minutes;
    String date;

    ArrayList<TempItem> tempReceived;
    TextView setTemp, tempDeg;

    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    long START_TIME_IN_MILLIS;
    private ImageView translate;
    private RadioButton hindi, english;
    private Button cancel;
    private Locale locale;
    Button msgButton;
    private UartService mService = null;
    private NotificationHelper notificationHelper;

    public FirstFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPrefs = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        loadLocale();
        rootView = inflater.inflate(R.layout.fragment_first, container, false);
        button = rootView.findViewById(R.id.notificationBell);
        originalAddress = rootView.findViewById(R.id.originalAddress);
        addresesHead = rootView.findViewById(R.id.addressHead);
        usernanme = rootView.findViewById(R.id.userNameFF);
        tempDeg = rootView.findViewById(R.id.tempDeg);

        setTemp = rootView.findViewById(R.id.tempOriginal);
        tp = rootView.findViewById(R.id.tp);




        token = sharedPrefs.getString("token", "");
        try {
            timeToFetchAddress = Integer.parseInt(sharedPrefs.getString("time", " "));
            Log.e("tt", timeToFetchAddress + "");
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "error in time fetching", Toast.LENGTH_LONG).show();
        }
        hours = sharedPrefs.getInt("hours", 0);
        minutes = sharedPrefs.getInt("minutes", 0);
        date = sharedPrefs.getString("dateSelected", "");


        SimpleDateFormat dateFormat = new SimpleDateFormat("hh");
        SimpleDateFormat dt = new SimpleDateFormat("hh:mm:ss");
        try {
            Date date1 = dateFormat.parse(sharedPrefs.getString("time", " "));
            if (date1 != null) {
                out = dt.format(date1);
                Log.e("Time", "" + out);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            Log.e("Error", "" + e);
        }


        String[] units = out.split(":"); //will break the string up into an array
        int minutes = Integer.parseInt(units[1]); //first element
        int seconds = Integer.parseInt(units[2]); //second element
        int hours = Integer.parseInt(units[0]);
        int duration = (3600 * hours + 60 * minutes + seconds) * 1000; //add up our values



        START_TIME_IN_MILLIS = duration;
        mTimeLeftInMillis = duration;
        Log.e("Time1", "" + duration);



        if (sharedPrefs.getBoolean("firstTime", false) == true) {
            editor.putString("hqAddress", "N/A");
            editor.putBoolean("firstTime", false);
            editor.commit();
        }

        if (!sharedPrefs.getString("hqAddress", "").equals("N/A")) {
            addresesHead.setVisibility(View.GONE);
            originalAddress.setText(sharedPrefs.getString("hqAddress", ""));

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

        translate = rootView.findViewById(R.id.translate);
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialog();
            }
        });

        if (sharedPrefs.getBoolean("DeviceConnected", false)) {
            UartFeature();
        }

        serviceInitialize();

        notificationHelper = new NotificationHelper(getContext());
        return rootView;


    }

    private void serviceInitialize() {
        Intent bindIntent = new Intent(getContext(), UartService.class);
        getActivity().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private void UartFeature() {
    }


    private void OpenDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.language_dialog);

        hindi = dialog.findViewById(R.id.radioButtonHindi);
        english = dialog.findViewById(R.id.radioButtonEnglish);
        cancel = dialog.findViewById(R.id.buttonCancel);

        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                english.setChecked(false);
                setLocale("hi");
                editor.putString("locale", "hi");
                editor.putBoolean("hindiSelected", true);
                editor.commit();
                dialog.dismiss();

            }

        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hindi.setChecked(false);
                setLocale("en");
                editor.putString("locale", "hi");
                editor.putBoolean("hindiSelected", true);
                editor.commit();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setLocale(String lang) {
        locale = new Locale(lang);
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration conf = resources.getConfiguration();
        conf.locale = locale;
        resources.updateConfiguration(conf, dm);
        editor.putString("My Language", lang);
        editor.apply();
        Fragment fragment = new FirstFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();


    }


    private void loadLocale() {
        SharedPreferences preferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        String language = preferences.getString("My Language", "");
        setLanguage(language);
    }


    private void setLanguage(String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getContext().getResources().updateConfiguration(config, getContext().getResources().getDisplayMetrics());
    }

    private void startTimer() {

        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountdownTime();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateButtons();
            }
        }.start();
        mTimerRunning = true;
        updateButtons();


    }

    private void updateButtons() {
        if (mTimerRunning) {
            addresesHead.setText("Location will be fetched");
            addresesHead.setClickable(false);
        } else {
            addresesHead.setText("Fetch Location");
            if (mTimeLeftInMillis < 1000) {
                addresesHead.setVisibility(View.GONE);
            }
            if (mTimeLeftInMillis < START_TIME_IN_MILLIS) {
                getGoogleApiClient();
            }
        }

    }

    private void updateCountdownTime() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        if (originalAddress.getText().equals("N/A")) {
            Toast.makeText(getContext(), "Your location will be fetched in " + timeLeftFormatted, Toast.LENGTH_LONG).show();
        }
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
                                Toast.makeText(getContext(), "Location permission was not granted ", Toast.LENGTH_LONG).show();
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

                                            editor.putString("hqAddress", fulladdress);

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

        setTemp.setText(String.format("%.2f", (sharedPrefs.getFloat("temp1", 0f))));
//        token = sharedPrefs.getString("token", "");
//
//        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        okhttpbuilder.addInterceptor(logging);
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl("http://api-c19.ap-south-1.elasticbeanstalk.com/")
//                .addConverterFactory(GsonConverterFactory.create());
//
//        retrofit = builder.build();
//        for_login login = retrofit.create(for_login.class);
//        Call<GetTemp> call = login.getTemp(token);
//        call.enqueue(new Callback<GetTemp>() {
//            @Override
//            public void onResponse(Call<GetTemp> call, Response<GetTemp> response) {
//                if (response.isSuccessful() && response.code() == 200) {
//                    GetTemp getTemp = response.body();
//                    if (getTemp != null) {
//                        tempReceived = getTemp.getTemperature();
//                        if(tempReceived<45&&tempReceived!=0)
//                        {
//                            tempDeg.setText(getString(R.string.recent_tempC));
//                        }
//                        else if(tempReceived>45 && tempReceived!=0)
//                        {
//                            tempDeg.setText(getString(R.string.recent_temp));
//                        }
//                    }
//                    Log.e("Success", "" + response.code());
//
//                    //    Toast.makeText(getContext(), "Temperature received from server.", Toast.LENGTH_SHORT).show();
//                    setTemp.setText(String.format("%.2f", tempReceived));
//
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetTemp> call, Throwable t) {
//                Toast.makeText(getContext(), "Failed" + " : Weak or No Internet", Toast.LENGTH_SHORT).show();
//                Log.e("error", "" + t.getMessage());
//            }
//        });
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

    @Override
    public void onStart() {
        super.onStart();
        if (!sharedPrefs.getString("hqAddress", "").equals("N/A")) {
            addresesHead.setVisibility(View.GONE);
            originalAddress.setText(sharedPrefs.getString("hqAddress", ""));
        }
        mTimeLeftInMillis = sharedPrefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = sharedPrefs.getBoolean("timerRunning", false);
        updateCountdownTime();
        updateButtons();
        if (mTimerRunning) {
            mEndTime = sharedPrefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountdownTime();
                updateButtons();
            } else {
                startTimer();
            }
        }
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d("firstfragment", "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e("firstfragment", "Unable to initialize Bluetooth");

            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
        }
    };


    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;

            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                            notificationHelper.SendNotification("New Message",text,HTActivity.class);
                        } catch (Exception e) {
                            Log.e("FirstFragment", e.toString());
                        }
                    }
                });
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e("FirstFragment", ignore.toString());
        }
    }
}


