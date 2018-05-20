package com.example.dima.smarttool;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.fragment.TimePickerFragment;

import java.util.Calendar;
import java.util.Random;

import static com.example.dima.smarttool.fragment.StateFragment.FIRST_START_STATES;

public class AddStateActivity extends AppCompatActivity {
    static Switch wifiSwitch, bluetoothSwitch;
    static int mediaI, systemI, hour, minute, condition = 0;
    static long milliseconds = 999999999;
    static double lat, lng;
    EditText nameEditText;
    String name;
    Long startTime, time;
    Boolean wifi, bluetooth;
    SeekBar mediaSeekBar, systemSeekBar;
    AlarmManager alarmManager;
    Spinner conditionSpinner;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_save) {

            SharedPreferences prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
            final SharedPreferences.Editor ed = prefs.edit();
            final StateHelper sh = new StateHelper(getApplicationContext());

            wifi = wifiSwitch.isChecked();
            bluetooth = bluetoothSwitch.isChecked();
            name = (nameEditText.getText()).toString();
            startTime = milliseconds;
            mediaI = mediaSeekBar.getProgress();
            systemI = systemSeekBar.getProgress();
            ed.putBoolean(FIRST_START_STATES, false);
            ed.apply();

            if (name.length() == 0) {
                Toast.makeText(getApplicationContext(), "Введите имя", Toast.LENGTH_SHORT).show();
            } else {
                if (condition == 0) {
                    sh.insert(name, wifi, bluetooth, 999999999, mediaI, systemI, 0, 0);
                    Log.d("addState","condition= "+condition);
                    // сохраниние без условия запуска
                }else if (condition == 1) {
                    sh.insert(name, wifi, bluetooth, 999999999, mediaI, systemI, lat, lng);
                    Log.d("addState","condition= "+condition);

                    // сохраниние сохранение с запуском по GPS
                }else if (condition == 2) {
                    Log.d("addState", "add time");
                    sh.insert(name, wifi, bluetooth, startTime, mediaI, systemI, 0, 0);
                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(AddStateActivity.this, StateAlarmReceiver.class);
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
                    // сохраниние сохранение с запуском по времени
                }

                Log.d("DB", "add: " + sh.getAll().toString());
                stopService(new Intent(AddStateActivity.this, GPSService.class));
                startService(new Intent(AddStateActivity.this, GPSService.class));
                finish();
            }




        }
        return true;
    }

    public static void setTime(int hour, int minute) {
        milliseconds = hour * 3_600_000 + minute * 60_000;

    }

    public static void setHour(int hour) {
        AddStateActivity.hour = hour;
    }

    public static void setMinute(int minute) {
        AddStateActivity.minute = minute;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_state);
        Toolbar toolbar = (Toolbar) findViewById(R.id.stateToolbar);
        setSupportActionBar(toolbar);

        hour = 0;
        minute = 0;
        milliseconds = 0;
        lat = 0;
        lng = 0;

        conditionSpinner = findViewById(R.id.addStateConditionSpinner);
        nameEditText = findViewById(R.id.addStateNameEditText);
        wifiSwitch = findViewById(R.id.addStateWiFiSwitch);
        bluetoothSwitch = findViewById(R.id.addStateBluetoothSwitch);
        mediaSeekBar = findViewById(R.id.addStateMediaSoundSeekBar);
        systemSeekBar = findViewById(R.id.addStateSystemSoundSeekBar);

        SharedPreferences prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = prefs.edit();

        final StateHelper sh = new StateHelper(getApplicationContext());

        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.startCondition, R.layout.spinner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(adapter);
        //создание аддаптеров для spinner

        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        condition = 0;
                        break;
                    case 1:
                        startActivityForResult(new Intent(AddStateActivity.this,
                                MapsActivity.class), 1);
                        condition = 1;
                        break;
                    case 2:
                        DialogFragment newFragment = new TimePickerFragment();
                        newFragment.show(getFragmentManager(), "timePicker");
                        condition = 2;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            lat = data.getDoubleExtra("lat", 0);
            lng = data.getDoubleExtra("lng", 0);
            Log.d("addState", "add LatLng: " + lat + "   " + lng);
        }
    }

}