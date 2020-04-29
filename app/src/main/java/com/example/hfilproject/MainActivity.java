package com.example.hfilproject;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.hfilproject.Adapter.AdapterBegin;
import com.example.hfilproject.BLE.BLE_Activity;
import com.example.hfilproject.Model.ModelBegin;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //firebase signup
    private static final int RC_SIGN_IN = 138;
    private List<AuthUI.IdpConfig> providers;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private Button loginButton;

    //start screen
    private ViewPager viewPager;
    AdapterBegin adapter;
    List<ModelBegin> modelBegin;
    Integer[] color = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();


    //bluetooth scan
    private Button scan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().hide();



        scan = findViewById(R.id.btnScan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intenti = new Intent(MainActivity.this, BLE_Activity.class);
                startActivity(intenti);
            }
        });


//startScreen
        modelBegin = new ArrayList<>();
        modelBegin.add(new ModelBegin(R.drawable.tlk, "Let's Begin ", " We will guide you to use this app . " +
                "Please slide to the right for login and more details of getting interacted with us."));
        modelBegin.add(new ModelBegin(R.drawable.bt1, " Connect Your Band With Mobile's Bluetooth.", "Tap on the button provided below and click on allow permission ." +
                "Please connect the your respective watch and enter the password provided to you "));
        modelBegin.add(new ModelBegin(R.drawable.phonr, "Mobile Number Verification",
                "Please tap on the login button and enter your mobile number as well as verification code sent to you"));
        modelBegin.add(new ModelBegin(R.drawable.ph1, "SignUp Form", "You will be directed to signUp form then ,please enter all the asked details and then press on but" +
                "ton provided there"));

        adapter = new AdapterBegin(modelBegin, this);
        viewPager = findViewById(R.id.Pager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        Integer[] colorTemp = {getResources().getColor(R.color.color1),
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
                editor.commit();
                Log.d("phoneNumber", user.getPhoneNumber());
                Log.d("UserId", user.getUid());
                Intent intent = new Intent(this, LogIn.class);
                intent.putExtra("editProfile", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                loginButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }


    }
}


