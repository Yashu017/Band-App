package com.example.hfilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogIn extends AppCompatActivity {

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private EditText name, age, phoneNumber, address,dateS;
    private Button submitProfile;
    private ProgressBar progressBar;
    private RadioButton hq, iw;
    private LinearLayout iwL;
    private boolean editProfile;
    private boolean A=true;
    private int REQUEST_CODE_LOCATION_PERMISSION=1;
    private ResultReceiver resultReceiver;
    private double latitude,longitude;
    List<Address> addressList;
    Geocoder geocoder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        submitProfile = findViewById(R.id.submit_profile);
        sharedPrefs = getSharedPreferences("app", MODE_PRIVATE);
        editor = sharedPrefs.edit();


        String date_n = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z", Locale.getDefault()).format(new Date());
        dateS=findViewById(R.id.startDate);
        dateS.setText(date_n );
        dateS.setEnabled(false);


        getUI();
        phoneNumber.setText(sharedPrefs.getString("phoneNumber", ""));
        phoneNumber.setEnabled(false);
        address.setEnabled(false);

        if(A==true)
        {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(LogIn.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);

            }else {

                getCurrentLocation();
            }


        }




        hq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("Home Quarantine", true);
                editor.commit();
            }
        });
        iw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("Isolation Ward ", true);
                editor.commit();
            }
        });

        submitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPfl();
            }
        });






    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_LOCATION_PERMISSION&& grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                               getCurrentLocation();

            }
            else
            {
                Toast.makeText(this,"Access Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void getCurrentLocation() {

        final LocationRequest locationRequest=new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(LogIn.this)
                .requestLocationUpdates(locationRequest,new LocationCallback(){

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(LogIn.this)
                                .removeLocationUpdates(this);
                        if(locationResult!=null && locationResult.getLocations().size()>0)
                        {
                            int latestLoc=locationResult.getLocations().size()-1;

                            latitude=locationResult.getLocations().get(latestLoc).getLatitude();
                             longitude=locationResult.getLocations().get(latestLoc).getLongitude();



                        }

                        locInWords();


                    }
                }, Looper.getMainLooper());





    }

    private void locInWords() {

        geocoder=new Geocoder(this, Locale.getDefault());
        try {
            addressList=geocoder.getFromLocation(latitude,longitude,REQUEST_CODE_LOCATION_PERMISSION);
            String address1=addressList.get(0).getAddressLine(0);
            String area=addressList.get(0).getLocality();
            String city=addressList.get(0).getAdminArea();
            String country=addressList.get(0).getCountryName();
            String postalCode=addressList.get(0).getPostalCode();
            String fulladdress= address1 + ", " + area+", " + city + ", " + country + ", "+ postalCode;
            address.setText(fulladdress);
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }






    }


    private void submitPfl() {
        Intent intent = new Intent(LogIn.this,BottomNavActivity.class);
        startActivity(intent);
        finish();

    }

    private void getUI() {

        name=findViewById(R.id.nameL);
        phoneNumber=findViewById(R.id.mobileL);
        age=findViewById(R.id.ageL);
        address=findViewById(R.id.addressL);
        submitProfile=findViewById(R.id.submit_profile);
        progressBar = findViewById(R.id.profile_progress);
        hq = findViewById(R.id.hq_l);
        iw = findViewById(R.id.iw_l);

        iwL=findViewById(R.id.iwl);



    }
}
