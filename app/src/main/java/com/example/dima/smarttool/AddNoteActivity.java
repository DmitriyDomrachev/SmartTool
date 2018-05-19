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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.smarttool.DB.NoteHelper;
import com.example.dima.smarttool.fragment.TimePickerFragment;

import java.util.Calendar;
import java.util.Random;

import static com.example.dima.smarttool.fragment.NoteFragment.FIRST_START_NOTES;

public class AddNoteActivity extends AppCompatActivity {

    static TextView setConditionTextView;
    static int hour, minute, condition = 0;
    static long milliseconds = 999999999;
    static double lat, lng;
    EditText nameEditText, textEditText;
    Button saveBtn;
    String name, text;
    Long startTime, time;
    AlarmManager alarmManager;
    Spinner conditionSpinner;

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
        lng = 0;
        conditionSpinner = findViewById(R.id.addNoteConditionSpinner);
        nameEditText = findViewById(R.id.addNoteNameEditText);
        textEditText = findViewById(R.id.addNoteTextEditText);
        textEditText.setMovementMethod(new ScrollingMovementMethod());
        saveBtn = findViewById(R.id.addNoteSaveButton);

        SharedPreferences prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = prefs.edit();

        final NoteHelper nh = new NoteHelper(getApplicationContext());

        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.startCondition, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(adapter);
        //создание аддаптеров для spinner

        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        startActivityForResult(new Intent(AddNoteActivity.this,
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


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                text = textEditText.getText().toString();
                startTime = milliseconds;
                ed.putBoolean(FIRST_START_NOTES, false);
                ed.apply();
                Log.d("addNote", "condition = " + condition);
                if (name.length() == 0)
                    Toast.makeText(getApplicationContext(), "Введите имя", Toast.LENGTH_SHORT).show();
                else {
                    if (condition == 0)
                        nh.insert(name, text, 999999999, 0, 0);
                        // сохраниние без условия запуска

                    else if (condition == 1)
                        nh.insert(name, text, 999999999, lat, lng);
                        // сохраниние сохранение с запуском по GPS

                    else if (condition == 2) {
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



    }

    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            lat = data.getDoubleExtra("lat", 0);
            lng = data.getDoubleExtra("lng", 0);
            setConditionTextView.setText("Страт по GPS");
        }
    }

}
