package com.example.dima.smarttool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.example.dima.smarttool.DB.StateHelper;

public class AddStateActivity extends AppCompatActivity {
    EditText name, startTime;
    Button save, close;
    Switch wifi, mobile, bluetooth;

    String nameS;
    Long startTimeL;
    Boolean wifiB, mobileB, bluetoothB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_state);
        name = findViewById(R.id.addStateNameEditText);
        startTime = findViewById(R.id.addStateTimeEditText);
        wifi = findViewById(R.id.addStateWiFiSwitch);
        mobile = findViewById(R.id.addStateMobileSwitch);
        bluetooth = findViewById(R.id.addStateBluetoothSwitch);
        save = findViewById(R.id.addStateSaveButton);
        close = findViewById(R.id.addStateCloseButton);



        final StateHelper sh = new StateHelper(getApplicationContext());



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiB = wifi.isChecked();
                mobileB = mobile.isChecked();
                bluetoothB = bluetooth.isChecked();
                nameS=String.valueOf(name.getText());
                startTimeL=Long.parseLong(String.valueOf(startTime.getText()));
                sh.insert(nameS,wifiB, mobileB,bluetoothB,startTimeL);
                startActivity(new Intent(AddStateActivity.this,MainActivity.class));
                Log.d("DB", "add: "+sh.getAll().toString());

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddStateActivity.this,MainActivity.class));

            }
        });


    }
}