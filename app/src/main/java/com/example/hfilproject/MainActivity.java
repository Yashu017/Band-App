package com.example.hfilproject;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.hfilproject.Adapter.AdapterBegin;
import com.example.hfilproject.Model.ModelBegin;
import com.example.hfilproject.Model.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
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

public class MainActivity extends AppCompatActivity {

    //firebase signup
    private static final int RC_SIGN_IN = 138;
    private List<AuthUI.IdpConfig> providers;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private Button loginButton;

    private GoogleApiClient googleApiClient;

    //start screen
    private ViewPager viewPager;
    AdapterBegin adapter;
    List<ModelBegin> modelBegin;
    Integer[] color = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();


    //bluetooth scan
    private Button scan;

    //retrofit
    Retrofit retrofit;

    //Select Language Dialog
    RadioButton hindi, english;
    Button cancel;
    Boolean hindiSelected = false;
    Locale locale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // loadLocale();
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().hide();

        LocationRequest request = new LocationRequest().setFastestInterval(1500).setInterval(30000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder;
        builder = new LocationSettingsRequest.Builder().addLocationRequest(request);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    task.getResult(ApiException.class);
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED:
                            ResolvableApiException Rexception = (ResolvableApiException) e;
                            try {
                                Rexception.startResolutionForResult(MainActivity.this, 8989);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            } catch (ClassCastException ex) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;

                    }
                }

            }
        });

        scan = findViewById(R.id.btnScan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intenti = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intenti);
            }
        });


//startScreen
        modelBegin = new ArrayList<>();
        modelBegin.add(new ModelBegin(R.drawable.tlk, getResources().getString(R.string.begin), getResources().getString(R.string.begin_title) + getResources().getString(R.string.begin_content)));
        modelBegin.add(new ModelBegin(R.drawable.bt1, getResources().getString(R.string.connect_heading), getResources().getString(R.string.connect_title) +
                getResources().getString(R.string.connect_content)));
        modelBegin.add(new ModelBegin(R.drawable.phonr, getResources().getString(R.string.mobile), getResources().getString(R.string.mobile_title)));
        modelBegin.add(new ModelBegin(R.drawable.ph1, getResources().getString(R.string.signup), getResources().getString(R.string.sign_content)));

        adapter = new AdapterBegin(modelBegin, this);
        viewPager = findViewById(R.id.Pager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        Integer[] colorTemp = {getResources().getColor(R.color.color4),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)};
        color = colorTemp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapter.getCount() - 1) && position < color.length - 1) {
                    viewPager.setBackgroundColor(
                            (Integer) argbEvaluator.evaluate(
                                    positionOffset, color[position], color[position + 1]
                            )
                    );
                } else {
                    viewPager.setBackgroundColor(color[color.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        loginButton = findViewById(R.id.logInButton);
        progressBar = findViewById(R.id.login_progress);
        sharedPref = getSharedPreferences("app", MODE_PRIVATE);
        editor = sharedPref.edit();
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.tlk)
                        .build(), RC_SIGN_IN);
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.GONE);

            }
        });

        scan.setText(getResources().getString(R.string.scanBluetooth));
        OpenDialog();


    }

    private void OpenDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
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


            }

        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hindi.setChecked(false);
                setLocale("en");
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
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        //  getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //  SharedPreferences.Editor editor = getSharedPreferences("app", MODE_PRIVATE).edit();
        //  editor.putString("My Language", lang);
        //   editor.apply();
    }

    private void loadLocale() {
        SharedPreferences preferences = getSharedPreferences("app", Activity.MODE_PRIVATE);
        String language = preferences.getString("My Language", "");
        setLocale(language);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                editor.putBoolean("loginStatus", true);
                editor.putString("phoneNumber", user.getPhoneNumber());
                editor.putString("firebaseId", user.getUid());
                // editor.putString("time","0");
                editor.putBoolean("firstTime", true);
                editor.putBoolean("firstTimeMap", true);
                editor.commit();
                Log.d("phoneNumber", user.getPhoneNumber());
                Log.d("UserId", user.getUid());

//                Intent intent = new Intent(this, LogIn.class);
//                intent.putExtra("editProfile", false);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
                getUser(user.getPhoneNumber());
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                loginButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }


    }

    private void getUser(String phoneNumber) {

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
        params.put("phoneNumber", phoneNumber);
        Call<User> call = login.ReInstall(params);
        call.enqueue(new Callback<User>() {
            @Override

            public void onResponse(Call<User> call, Response<User> response) {
                String name, address, bluetoothId, quarantineType, age, token, error1;
                if (response.isSuccessful() && response.code() == 200) {


                    if (response.body().getErrorCode() != null) {
                        Intent intent = new Intent(MainActivity.this, LogIn.class);
                        intent.putExtra("editProfile", false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        name = response.body().getName();
                        age = response.body().getAge();
                        address = response.body().getAddress();
                        quarantineType = response.body().getQuarantineType();
                        bluetoothId = response.body().getBluetoothId();
                        token = response.body().get_id();
                        Log.e("tok", token);
                        editor.putString("name", name);
                        editor.putString("age", age);
                        editor.putString("address", address);
                        if (sharedPref.getString("address", "").equals("N/A")) {
                            editor.putString("time", "0");
                            editor.commit();
                        } else {
                            editor.putString("time", "1");
                            editor.commit();
                        }
                        editor.putString("bluetoothId", bluetoothId);
                        editor.putString("qt", quarantineType);
                        editor.putString("token", token + "");
                        editor.putBoolean("profileStatus", true);
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, BottomNavActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }


}


