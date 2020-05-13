package com.example.hfilproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hfilproject.BLE.BLE_Activity;

public class ViewTemperature extends AppCompatActivity {

    Button connect;
    TextView rvData, tvCelsius, tvFahrenheit;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    String ReceiveData;
    float tempValue;
    String celsius;
    String fahrenheit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtemperature);
        connect = findViewById(R.id.connectBtn);
        rvData = findViewById(R.id.dataText);
        tvCelsius = findViewById(R.id.tempCelsius);
        tvFahrenheit = findViewById(R.id.tempFahrenheit);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTemperature.this, BLE_Activity.class);
                startActivity(intent);

            }
        });


        sharedPrefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        ReceiveData = sharedPrefs.getString("ReceiveData", "");
        rvData.setText(ReceiveData);

        tempValue = sharedPrefs.getFloat("tempValue", 0);

        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%.02f", tempValue));
        celsius = builder.toString();

        tvCelsius.setText(celsius + "°C");


        tempValue = tempValue * 1.8f + 32f;
        final StringBuilder builder2 = new StringBuilder();
        builder2.append(String.format("%.02f", tempValue));
        fahrenheit = builder2.toString();
        tvFahrenheit.setText(fahrenheit + "°F");


    }
}
