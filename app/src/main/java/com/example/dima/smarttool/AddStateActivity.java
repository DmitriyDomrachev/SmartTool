package com.example.dima.smarttool;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.fragment.TimePickerFragment;

import java.util.Calendar;
import java.util.Random;

public class AddStateActivity extends AppCompatActivity {
    EditText nameEditText;
    Button saveBtn, closeBtn;
    static Switch wifiSwitch, bluetoothSwitch, conditionSwitch;
    static TextView conditionTextView, setConditionTextView;
    String name;
    Long startTime, time;
    static int mediaI, systemI, hour, minute;
    Boolean wifi, bluetooth;
    static long milliseconds;
    SeekBar mediaSeekBar, systemSeekBar;
    AlarmManager alarmManager;
    static double lat, lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        setContentView(R.layout.activity_add_state);
        conditionTextView = findViewById(R.id.addStateConditionTextView);
        setConditionTextView = findViewById(R.id.addStateSetTextView);
        nameEditText = findViewById(R.id.addStateNameEditText);
        wifiSwitch = findViewById(R.id.addStateWiFiSwitch);
        bluetoothSwitch = findViewById(R.id.addStateBluetoothSwitch);
        conditionSwitch = findViewById(R.id.addStateConditionSwitch);
        saveBtn = findViewById(R.id.addStateSaveButton);
        closeBtn = findViewById(R.id.addStateCloseButton);
        mediaSeekBar = findViewById(R.id.addStateMediaSoundSeekBar);
        systemSeekBar = findViewById(R.id.addStateSystemSoundSeekBar);

        final StateHelper sh = new StateHelper(getApplicationContext());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifi = wifiSwitch.isChecked();
                bluetooth = bluetoothSwitch.isChecked();
                name = (nameEditText.getText()).toString();
                startTime = milliseconds;
                mediaI = mediaSeekBar.getProgress();
                systemI = systemSeekBar.getProgress();

                if (name.length() == 0)
                    Toast.makeText(getApplicationContext(), "enter nameEditText", Toast.LENGTH_SHORT).show();
                else {
                    if (lat != 0)
                        sh.insert(name, wifi, bluetooth, startTime, mediaI, systemI, lat, lng);
                    if (lat == 0) {
                        sh.insert(name, wifi, bluetooth, startTime, mediaI, systemI, 0, 0);
                        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(AddStateActivity.this, MyReceiver.class);
                        intent.putExtra("nameEditText", name);
                        PendingIntent pendingIntent;
                        Random r = new Random();
                        pendingIntent = PendingIntent.getBroadcast(AddStateActivity.this, r.nextInt(),
                                intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
                        if (System.currentTimeMillis() > time) {
                            if (calendar.AM_PM == 0)
                                time = time + (1000 * 60 * 60 * 12);
                            else
                                time = time + (1000 * 60 * 60 * 24);
                        }
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
                    }

                    Log.d("DB", "add: " + sh.getAll().toString());
                        startActivity(new Intent(AddStateActivity.this, MainActivity.class));
                        finish();
                }
            }
        });

        conditionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    conditionTextView.setText("GPS");
                else conditionTextView.setText("TIME");
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddStateActivity.this, MainActivity.class));
                finish();


            }
        });

        setConditionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!conditionSwitch.isChecked()) {
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                } else {
                    startActivityForResult(new Intent(AddStateActivity.this, MapsActivity.class), 1);
                }


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            lat = data.getDoubleExtra("lat", 0);
            lng = data.getDoubleExtra("lng", 0);
        }
    }

    public static void setTime(int hour, int minute) {
        setConditionTextView.setText(hour + ":" + minute);
        milliseconds = hour * 3_600_000 + minute * 60_000;

    }

    public static void setHour(int hour) {
        AddStateActivity.hour = hour;
    }

    public static void setMinute(int minute) {
        AddStateActivity.minute = minute;
    }

}