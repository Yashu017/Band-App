package com.example.hfilproject;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ThirdFragment.OnFragmentInteractionListener, FourthFragment.OnFragmentInteractionListener
,FifthFragment.OnFragmentInteractionListener{
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.item1);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().hide();


        BluetoothAdapter mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        if(!mBtAdapter.isEnabled()) {
            mBtAdapter.enable();
        }



        sharedPref = getSharedPreferences("app", MODE_PRIVATE);
        editor = sharedPref.edit();


        LocationRequest request=new LocationRequest().setFastestInterval(1500).setInterval(30000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder;
        builder=new  LocationSettingsRequest.Builder().addLocationRequest(request);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    task.getResult(ApiException.class);
                } catch (ApiException e) {
                    switch (e.getStatusCode())
                    {
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED:
                            ResolvableApiException Rexception=(ResolvableApiException) e;
                            try {
                                Rexception.startResolutionForResult(BottomNavActivity.this,8989);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }catch (ClassCastException ex)
                            {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;

                    }
                }

            }
        });

        // Checking whether user has logged in or not
        if (sharedPref.getBoolean("loginStatus", false) == false) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();

        }

        //Checking whether user has created profile or not
        else if (sharedPref.getBoolean("profileStatus", false) == false) {
            Intent i = new Intent(this, LogIn.class);
            editor.putBoolean("firstTime",true);
            editor.putBoolean("firstTimeMap",true);
            editor.commit();
            startActivity(i);
            finish();

        }





    }

    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();
    FourthFragment fourthFragment = new FourthFragment();


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item1:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.fragment_container, firstFragment).commit();
                return true;
            case R.id.item2:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.fragment_container, secondFragment).commit();

                return true;
            case R.id.item3:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.fragment_container, thirdFragment).commit();
                return true;
            case R.id.item4:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.fragment_container, fourthFragment).commit();
                return true;


            default:
                //   getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).addToBackStack(null).replace(R.id.fragment_container, firstFragment).commit();
                return true;
        }

    }

    @Override
    public void onClicked() {

    }


}
