package com.example.hfilproject;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hfilproject.Model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
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

public class LogIn extends AppCompatActivity {
    private SharedPreferences sharedPrefs;
    public static Retrofit retrofit;
    private SharedPreferences.Editor editor;
    private EditText name, age, phoneNumber, address, time;

   private EditText bluetoothId;
    private Button submitProfile;
    private ProgressBar progressBar;
    private RadioButton hq, iw;
    private LinearLayout hq_l, iw_l;
    String token;
    String sendToken;
    private boolean editProfile;
    private boolean A = true;
    private int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ResultReceiver resultReceiver;
    private double latitude, longitude;
    String t;
    List<Address> addressList;
    Geocoder geocoder;


    String fulladdress, quarnType;
    String status = "1";


    Boolean ok;
    RelativeLayout timeRl;
    int temp = 0;


    private static final String TAG = "LogIn";
    String address1;
    String area;
    String city;
    String postalCode;
    String country;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        submitProfile = findViewById(R.id.submit_profile);
        sharedPrefs = getSharedPreferences("app", MODE_PRIVATE);
        editor = sharedPrefs.edit();


//        String date_n = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z", Locale.getDefault()).format(new Date());
//        dateS=findViewById(R.id.startDate);
//        dateS.setText(date_n );
//        dateS.setEnabled(false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getUI();
        phoneNumber.setText(sharedPrefs.getString("phoneNumber", ""));
        phoneNumber.setEnabled(false);
        address.setEnabled(false);


        if (Build.VERSION.SDK_INT >= 21) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LogIn.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);

            } else {

                getCurrentLocation();
            }


        }


        hq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("Home Quarantine", true);
                editor.commit();
                Toast.makeText(LogIn.this, "Your address will be fetched once you reach home", Toast.LENGTH_LONG).show();
                address.setText("N/A");
                quarnType = "Personal Place";
                Toast.makeText(LogIn.this, quarnType, Toast.LENGTH_SHORT).show();
                iw.setChecked(false);
                timeRl.setVisibility(View.VISIBLE);
                temp = 1;


            }
        });
        iw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("Isolation Ward ", true);
                editor.commit();
                hq.setChecked(false);
                quarnType = "Government Place";
                Toast.makeText(LogIn.this, quarnType, Toast.LENGTH_SHORT).show();
                timeRl.setVisibility(View.GONE);
                address.setText(address1 + ".");
                temp=0;

            }
        });



        submitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (temp == 1) {
                    editor.putString("time", (time.getText().toString()));
                    Log.e("time", time.getText().toString());
                    editor.commit();
                } else {
                    time.setText("0");
                    editor.putString("time", "0");
                    Log.e("time", time.getText().toString());
                    editor.commit();
                }


                if (!name.getText().toString().isEmpty() && !address.getText().toString().isEmpty() && !phoneNumber.getText().toString().isEmpty()
                        && !age.getText().toString().isEmpty() && !time.getText().toString().isEmpty() && !bluetoothId.getText().toString().isEmpty()) {
                    Intent intent = new Intent();
                    editProfile = false;
                    if (intent.hasExtra("editProfile")) {
                        editProfile = getIntent().getExtras().getBoolean("editProfile");
                        Log.e("status", "" + editProfile);
                    }

                    submitProfile(
                            name.getText().toString(),
                            phoneNumber.getText().toString(),
                            age.getText().toString(),
                            address.getText().toString(),
                            bluetoothId.getText().toString(), quarnType, status, editProfile

                    );


                } else {
                    Toast.makeText(LogIn.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }


            }
        });


        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().hide();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#673AB7")));


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();

            } else {
                Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getCurrentLocation() {

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(LogIn.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(LogIn.this)
                                .removeLocationUpdates(this);


                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLoc = locationResult.getLocations().size() - 1;

                            latitude = locationResult.getLocations().get(latestLoc).getLatitude();
                            longitude = locationResult.getLocations().get(latestLoc).getLongitude();

                            locInWords();
                        }


                    }
                }, Looper.getMainLooper());


    }

    private void locInWords() {

        geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, REQUEST_CODE_LOCATION_PERMISSION);
            address1 = addressList.get(0).getAddressLine(0);
            area = addressList.get(0).getLocality();
            city = addressList.get(0).getAdminArea();
            country = addressList.get(0).getCountryName();
            postalCode = addressList.get(0).getPostalCode();
            fulladdress = address1 + ".";
            address.setText(fulladdress);
            editor.putString("Updated Location", fulladdress);
            editor.commit();
            Log.e("location", "" + fulladdress);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    private void submitProfile(String name, String phone, String age, String address, String bt, String qt, String val, Boolean editProfile) {

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
        params.put("name", name);
        params.put("phoneNumber", phone);
        params.put("age", age);
        params.put("address", address);
        params.put("quarantineType", qt);
        params.put("bluetoothId", bt);
        params.put("status", val);
        Call<User> call = login.createAccount(token, params);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                String error1;
                String res = "";
                String res1;
                try {
                    if (response.isSuccessful() && response.code() == 200) {
//                    res=new Gson().toJson(response.body());
//
                        if (response.body().getErrorCode() != null) {
                            error1 = response.body().getErrorCode();
                            Log.e("error", error1 + "");

                            if (error1.equals("0")) {
                                Toast.makeText(LogIn.this, "User already exist.Please try with another credentials", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            } else if (error1.equals("1")) {
                                Toast.makeText(LogIn.this, "Your Bluetooth Device is already registered.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            ok = true;
                            Toast.makeText(LogIn.this, res + "", Toast.LENGTH_LONG).show();
                            Log.e("response", res + "");
                            sendToken = response.body().gettoken();
                            Log.e("tk", sendToken);

                            if (!editProfile) {
                                editor.putString("token", sendToken);
                                editor.apply();
                            }

                            if (ok == true) {
                                editor.putString("name", name);
                                editor.putString("address", address);
                                editor.putString("age", age);
                                editor.putString("quarantineType", qt);
                                editor.putBoolean("profileStatus", true);
                                editor.commit();
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LogIn.this, "ok_pref", Toast.LENGTH_SHORT).show();
                                if (editProfile) {
                                    finish();
                                } else {
                                    Intent i = new Intent(LogIn.this, BottomNavActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LogIn.this, "activity passed", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }
                    } else {
                        res = response.errorBody().string();
                        Toast.makeText(LogIn.this, res, Toast.LENGTH_LONG).show();
                        Log.e("res", res);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<User> call, Throwable t) {


            }
        });

    }

    private void getUI() {


        name = findViewById(R.id.nameL);
        phoneNumber = findViewById(R.id.mobileL);
        age = findViewById(R.id.ageL);
        address = findViewById(R.id.addressL);
        submitProfile = findViewById(R.id.submit_profile);
        progressBar = findViewById(R.id.profile_progress);
        hq = findViewById(R.id.hq_yes);
        iw = findViewById(R.id.ic_yes);
        bluetoothId = findViewById(R.id.bluetoothId);
        iw_l = findViewById(R.id.ic);
        hq_l = findViewById(R.id.hq);
        time = findViewById(R.id.startTime);
        timeRl = findViewById(R.id.timeRL);


    }


}


