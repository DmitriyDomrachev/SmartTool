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

import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.fragment.TimePickerFragment;

public class AddStateActivity extends AppCompatActivity {
    EditText name;
    Button save, close;
    Switch wifi, bluetooth;
    static TextView setTime;
    String nameS;
    Long startTimeL;
    static int mediaI, systemI;
    Boolean wifiB,  bluetoothB;
    static long milliseconds;
    SeekBar media, system;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_state);
        setTime = findViewById(R.id.addStateSetTimeTextView);
        name = findViewById(R.id.addStateNameEditText);
        wifi = findViewById(R.id.addStateWiFiSwitch);
        bluetooth = findViewById(R.id.addStateBluetoothSwitch);
        save = findViewById(R.id.addStateSaveButton);
        close = findViewById(R.id.addStateCloseButton);
        media = findViewById(R.id.addStateMediaSoundSeekBar);
        system = findViewById(R.id.addStateSystemSoundSeekBar);


        final StateHelper sh = new StateHelper(getApplicationContext());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiB = wifi.isChecked();
                bluetoothB = bluetooth.isChecked();
                nameS=String.valueOf(name.getText());
                startTimeL= milliseconds;
                mediaI = media.getProgress();
                systemI = system.getProgress();


                sh.insert(nameS,wifiB, bluetoothB, startTimeL, mediaI, systemI);
                startActivity(new Intent(AddStateActivity.this,MainActivity.class));
                Log.d("DB", "add: "+sh.getAll().toString());
                stopService(new Intent(AddStateActivity.this, Scanning.class));
                startService(new Intent(AddStateActivity.this, Scanning.class));


            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddStateActivity.this,MainActivity.class));

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

    public static void setTime(int hour, int minute){
     setTime.setText(hour+":"+minute);
        milliseconds = hour * 3_600_000 + minute * 60_000;

    }
}