package com.example.hfilproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 138;
    private List<AuthUI.IdpConfig> providers;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private Button loginButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton=findViewById(R.id.logInButton);
        progressBar=findViewById(R.id.login_progress);
        sharedPref=getSharedPreferences("app",MODE_PRIVATE);
        editor=sharedPref.edit();
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.tlk)
                .build(),RC_SIGN_IN);
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
