package com.example.hfilproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LogIn extends AppCompatActivity {

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private EditText name, age, phoneNumber, address;
    private Button submitProfile;
    private ProgressBar progressBar;
    private RadioButton hq, iw;
    private LinearLayout hq_l,iw_l;
    private boolean editProfile;
    private boolean A=true;
    private int REQUEST_CODE_LOCATION_PERMISSION=1;
    private ResultReceiver resultReceiver;
    private double latitude,longitude;
    List<Address> addressList;
    Geocoder geocoder;
    String fulladdress,quarnType;




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
                iw_l.setVisibility(View.GONE);
                Toast.makeText(LogIn.this,"Your address will be fetched once you reach home",Toast.LENGTH_LONG).show();
                address.setText("N/A");
                quarnType="Personal Place";
                Toast.makeText(LogIn.this,quarnType,Toast.LENGTH_SHORT).show();




            }
        });
        iw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("Isolation Ward ", true);
                editor.commit();
                hq_l.setVisibility(View.GONE);
                address.setText(fulladdress);
                quarnType="Government Place";
                Toast.makeText(LogIn.this,quarnType,Toast.LENGTH_SHORT).show();
            }
        });

//        if(hq.isChecked())
//        {
//
//            Toast.makeText(this,"Hello",Toast.LENGTH_SHORT).show();
//
//            Log.e("Tag","Ayush");
//
//            address.setText("adress will be fetched once you reach home");
//
//        }







        submitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
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
             fulladdress= address1 + ", " + area+", " + city + ", " + country + ", "+ postalCode;

        }
        catch (Exception e)
        {
            e.printStackTrace();

        }






    }
    String r;

    private void submitPfl() {


        if (!name.getText().toString().isEmpty() && !age.getText().toString().isEmpty() &&
                !phoneNumber.getText().toString().isEmpty() && !address.getText().toString().isEmpty()) {
            Intent intent = new Intent();
            editProfile = false;
            if (intent.hasExtra("editProfile")) {
                editProfile = getIntent().getExtras().getBoolean("editProfile");
                Log.e("status", "" + editProfile);
            }

            RequestQueue requestQueue = Volley.newRequestQueue(LogIn.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://api-c19.ap-south-1.elasticbeanstalk.com/"+ "api/auth/signup",
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            int errorCode = 1;
                            String token;
                            final JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                errorCode = (int) jsonObject.get("errorCode");
                                if (!editProfile) {
                                    token = (String) jsonObject.get("token");
                                    editor.putString("token", token);
                                    editor.apply();
                                }
                                Toast.makeText(LogIn.this, "token" + sharedPrefs.getString("token", ""), Toast.LENGTH_LONG).show();


                            Toast.makeText(LogIn.this, response, Toast.LENGTH_LONG).show();
                            Log.e("tag",response);
                            if (errorCode != 0 && errorCode != 1) {
//                                Toast.makeText(ProfileNew.this, "error code" + errorCode, Toast.LENGTH_SHORT).show();
                                editor.putString("name", name.getText().toString());
                                editor.putString("age", age.getText().toString());
                                editor.putString("address", address.getText().toString());
                                editor.putString("quarantineType", quarnType);
                                editor.putBoolean("profileStatus", true);
                                editor.commit();
                                progressBar.setVisibility(View.GONE);
                                if (editProfile) {
                                    finish();
                                } else {
                                    Intent i = new Intent(LogIn.this, BottomNavActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            } else if(errorCode==0){
                                Toast.makeText(LogIn.this, "User Exists" + response, Toast.LENGTH_SHORT).show();
                                Log.e("error", response);
                                progressBar.setVisibility(View.GONE);
                            }
                            else if (errorCode==1)
                            {
                                Toast.makeText(LogIn.this, "Device Registered" + response, Toast.LENGTH_SHORT).show();
                                Log.e("error", response);
                                progressBar.setVisibility(View.GONE);
                            }
                            r=response;

                        }catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("VOLLEY", error.toString());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LogIn.this, error.toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(LogIn.this, r, Toast.LENGTH_LONG).show();
                    Log.e("resp",r+ " ");

                }

        }) {

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }


                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("access-token", "" + sharedPrefs.getString("token", ""));
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Name", name.getText().toString());
                    params.put("phoneNumber",phoneNumber.getText().toString());
                    params.put("age", age.getText().toString());
                    params.put("address", address.getText().toString());
                    params.put("quarantineType", quarnType);
                    params.put("bluetoothId", "N/A");
                    params.put("status","1");
                    return params;
                }

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } else
            Toast.makeText(LogIn.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

    }

        private void getUI () {

            name = findViewById(R.id.nameL);
            phoneNumber = findViewById(R.id.mobileL);
            age = findViewById(R.id.ageL);
            address = findViewById(R.id.addressL);
            submitProfile = findViewById(R.id.submit_profile);
            progressBar = findViewById(R.id.profile_progress);
            hq = findViewById(R.id.hq_yes);
            iw = findViewById(R.id.ic_yes);

            iw_l = findViewById(R.id.ic);
            hq_l = findViewById(R.id.hq);


        }
    }