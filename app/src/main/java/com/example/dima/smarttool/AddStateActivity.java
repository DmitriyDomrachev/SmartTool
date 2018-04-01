package com.example.dima.smarttool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddStateActivity extends AppCompatActivity {
    EditText name, startTime, wifi, mobile, bluetooth;
    Button save, close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_state);
        name = findViewById(R.id.addStateNameEditText);
        startTime = findViewById(R.id.addStateTimeEditText);
        wifi = findViewById(R.id.addStateWifiEditText);
        mobile = findViewById(R.id.addStateMobileEditText);
        bluetooth = findViewById(R.id.addStateBluetoothEditText);
        save = findViewById(R.id.addStateSaveButton);
        close = findViewById(R.id.addStateCloseButton);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}