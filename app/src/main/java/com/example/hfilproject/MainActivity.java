package com.example.hfilproject;

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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
import com.google.android.material.tabs.TabLayout;
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
    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter ;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0 ;
    Button btnGetStarted;
    Animation btnAnim ;
    TextView tvSkip;


    //bluetooth scan
    private Button scan;

    //retrofit
    Retrofit retrofit;

    //Select Language Dialog
    RadioButton hindi, english;
    Button cancel;
    Boolean hindiSelected = false;
    Locale locale;
    private ImageView chngLang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        loadLocale();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

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


        scan = findViewById(R.id.scanBT);


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

            }
        });





        // when this activity is about to be launch we need to check if its openened before or not

        if (restorePrefData()) {

            Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class );
            startActivity(mainActivity);
            finish();


        }



        // ini views
        btnNext = findViewById(R.id.btn_next);
        loginButton = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_anim);
        tvSkip = findViewById(R.id.tv_skip);

        // fill list screen

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem(  getResources().getString(R.string.begin),getResources().getString(R.string.begin_title) + getResources().getString(R.string.begin_content),
               R.drawable.tlk));
        mList.add(new ScreenItem("User Registration", "All new users will have to regist" +
                "er at time of logging in. Basic details regarding  " +
                "your quarantine will be collected .",R.drawable.phonr));
        mList.add(new ScreenItem("Temperature Monitoring","C-Watch will continuously record" +
                " your body temperature to monitor your health " +
                "and you will be provided with records in real time .",R.drawable.temphome));
        mList.add(new ScreenItem("Geofencing & Monitoring","To ensure proper quar" +
                "antine rules are followed you will be geofenced and your location " +
                "will be monitored in real time to ensure you do not cross your quarantine location . ",R.drawable.locationhome));
        mList.add(new ScreenItem("Social Distancing Alert","To maintain socia" +
                "l distancing , we will constantly monitor and alert you in real time . ",R.drawable.sd));

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listner

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {

                    position++;
                    screenPager.setCurrentItem(position);


                }

                if (position == mList.size()-1) { // when we rech to the last screen

                    // TODO : show the GETSTARTED Button and hide the indicator and the next button

                    loaddLastScreen();


                }



            }
        });

        // tablayout add change listener


        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size()-1) {

                    loaddLastScreen();

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        // Get Started button click listener



        // skip button click listener

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });

        progressBar = findViewById(R.id.login_progress);
        sharedPref = getSharedPreferences("app", MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putBoolean("autoStart",false);
        editor.commit();
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
                //  Intent intent = new Intent(MainActivity.this, Bottt)


        });

        scan.setText(getResources().getString(R.string.scanBluetooth));

        chngLang = findViewById(R.id.languageChange);
        chngLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialog();
            }
        });


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
                editor.putBoolean("FirstTime", true);
                dialog.dismiss();

            }

        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hindi.setChecked(false);
                setLocale("en");
                editor.putString("locale", "en");

                editor.commit();
                editor.putBoolean("FirstTime", true);
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

        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);

        //

    }

    private void loadLocale() {

        SharedPreferences preferences = getSharedPreferences("app", Activity.MODE_PRIVATE);
        String language = preferences.getString("locale", "");
        setLanguage(language);
    }

    private void setLanguage(String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
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


    private boolean restorePrefData() {


        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;



    }

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.commit();


    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        scan.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        loginButton.setAnimation(btnAnim);
        scan.setAnimation(btnAnim);



    }


}


