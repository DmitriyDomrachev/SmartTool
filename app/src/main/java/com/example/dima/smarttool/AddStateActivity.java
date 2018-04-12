package com.example.dima.smarttool;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.fragment.TimePickerFragment;

import java.util.Date;

public class AddStateActivity extends AppCompatActivity {
    EditText name;
    Button save, close;
    Switch wifi, bluetooth;
    static TextView setTime;
    String nameS, latlng;
    Long startTimeL;
    static int mediaI, systemI;
    Boolean wifiB, bluetoothB;
    static long milliseconds;
    SeekBar media, system;
    boolean newState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        newState = intent.getStringExtra("name") == null;
        setContentView(R.layout.activity_add_state);
        setTime = findViewById(R.id.addStateSetTimeTextView);
        name = findViewById(R.id.addStateNameEditText);
        wifi = findViewById(R.id.addStateWiFiSwitch);
        bluetooth = findViewById(R.id.addStateBluetoothSwitch);
        save = findViewById(R.id.addStateSaveButton);
        close = findViewById(R.id.addStateCloseButton);
        media = findViewById(R.id.addStateMediaSoundSeekBar);
        system = findViewById(R.id.addStateSystemSoundSeekBar);
        if (!newState) {
            name.setText(intent.getStringExtra("name"));
            wifi.setChecked(intent.getBooleanExtra("wifi", false));
            bluetooth.setChecked(intent.getBooleanExtra("bluetooth", false));
            setTime.setText(getTime(intent.getLongExtra("starttime", 0l)));
            media.setProgress(intent.getIntExtra("media", 0));
            system.setProgress(intent.getIntExtra("system", 0));

        }


        final StateHelper sh = new StateHelper(getApplicationContext());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiB = wifi.isChecked();
                bluetoothB = bluetooth.isChecked();
                nameS = (name.getText()).toString();
                startTimeL = milliseconds;
                mediaI = media.getProgress();
                systemI = system.getProgress();
                if (nameS.length() == 0)
                    Toast.makeText(getApplicationContext(), "enter name", Toast.LENGTH_SHORT).show();
                else {
                    if (newState && nameS.length() > 0)
                        sh.insert(nameS, wifiB, bluetoothB, startTimeL, mediaI, systemI, latlng);
                    else
                        sh.updateState(String.valueOf(intent.getIntExtra("id", 0)), nameS, startTimeL, wifiB, bluetoothB, mediaI, systemI, latlng);
                    startActivity(new Intent(AddStateActivity.this, MainActivity.class));
                    Log.d("DB", "add: " + sh.getAll().toString());
                    stopService(new Intent(AddStateActivity.this, Scanning.class));
                    startService(new Intent(AddStateActivity.this, Scanning.class));
                    finish();
                }


            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddStateActivity.this, MainActivity.class));
                finish();


            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });


    }

    public static void setTime(int hour, int minute) {
        setTime.setText(hour + ":" + minute);
        milliseconds = hour * 3_600_000 + minute * 60_000;

    }

    public String getTime(long milliseconds) {
        Date date = new Date();
        date.setTime(milliseconds);
        return String.valueOf(milliseconds / 3_600_000) + ":" + String.valueOf(milliseconds % 3_600_000 / 60_000);
    }
}