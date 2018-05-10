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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.smarttool.DB.NoteHelper;
import com.example.dima.smarttool.fragment.TimePickerFragment;

import java.util.Calendar;
import java.util.Random;

public class AddNoteActivity extends AppCompatActivity {

    static Switch conditionSwitch;
    static TextView conditionTextView, setConditionTextView;
    static int hour, minute;
    static long milliseconds = 999999999;
    static double lat, lng;
    EditText nameEditText, textEditText;
    Button saveBtn;
    String name, text;
    Long startTime, time;
    AlarmManager alarmManager;

    public static void setTime(int hour, int minute) {
        setConditionTextView.setText(hour + ":" + minute);
        milliseconds = hour * 3_600_000 + minute * 60_000;


    }

    public static void setHour(int hour) {
        AddNoteActivity.hour = hour;
    }

    public static void setMinute(int minute) {
        AddNoteActivity.minute = minute;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        hour = 0;
        minute = 0;
        milliseconds = 0;
        lat = 0;
        lng  = 0;
        conditionTextView = findViewById(R.id.addNoteConditionTextView);
        setConditionTextView = findViewById(R.id.addNoteSetTextView);
        nameEditText = findViewById(R.id.addNoteNameEditText);
        textEditText = findViewById(R.id.addNoteTextEditText);
        conditionSwitch = findViewById(R.id.addNoteConditionSwitch);
        saveBtn = findViewById(R.id.addNoteSaveButton);

        final NoteHelper nh = new NoteHelper(getApplicationContext());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                text = textEditText.getText().toString();
                startTime = milliseconds;

                if (name.length() == 0)
                    Toast.makeText(getApplicationContext(), "Введите имя", Toast.LENGTH_SHORT).show();
                else {
                    if (startTime == 0 && lat == 0 && lng == 0)
                        nh.insert(name, text, 999999999, 0, 0);
                        // сохраниние без условия запуска
                    else if (lat != 0) {
                        nh.insert(name, text, 999999999, lat, lng);
                        // сохраниние сохранение с запуском по GPS
                    } else {
                        nh.insert(name, text, startTime, 0, 0);
                        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(AddNoteActivity.this, NoteAlarmReceiver.class);
                        intent.putExtra("nameEditText", name);
                        PendingIntent pendingIntent;
                        Random r = new Random();
                        pendingIntent = PendingIntent.getBroadcast(AddNoteActivity.this, r.nextInt(),
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


                    Log.d("DB", "add: " + nh.getAll().toString());
                    stopService(new Intent(AddNoteActivity.this, GPSService.class));
                    startService(new Intent(AddNoteActivity.this, GPSService.class));
                    finish();
                }
            }
        });

        conditionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    conditionTextView.setText("GPS");
                else conditionTextView.setText("Время");
            }
        });


        setConditionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!conditionSwitch.isChecked()) {
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                } else {
                    startActivityForResult(new Intent(AddNoteActivity.this, MapsActivity.class), 1);
                }


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
        }
    }

}
