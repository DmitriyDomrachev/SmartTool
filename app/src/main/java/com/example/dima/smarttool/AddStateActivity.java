package com.example.dima.smarttool;

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
    String name, latlng;
    Long startTime, time;
    static int mediaI, systemI, hour, minute;
    Boolean wifi, bluetooth;
    static long milliseconds;
    SeekBar mediaSeekBar, systemSeekBar;
    AlarmManager alarmManager;


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
                latlng = "ufd";
                if (name.length() == 0)
                    Toast.makeText(getApplicationContext(), "enter nameEditText", Toast.LENGTH_SHORT).show();
                else {
                    sh.insert(name, wifi, bluetooth, startTime, mediaI, systemI, latlng);

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
                if(!conditionSwitch.isChecked()){
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                }
                else startActivity(new Intent(AddStateActivity.this, MapsActivity.class));


            }
        });


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