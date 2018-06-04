package com.uraldroid.dima.smarttool.activity;

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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.uraldroid.dima.smarttool.DB.NoteHelper;
import com.uraldroid.dima.smarttool.GeoService;
import com.uraldroid.dima.smarttool.R;
import com.uraldroid.dima.smarttool.fragment.TimePickerFragment;
import com.uraldroid.dima.smarttool.receiver.NoteAlarmReceiver;

import java.util.Calendar;
import java.util.Random;

import static com.uraldroid.dima.smarttool.fragment.NoteFragment.FIRST_START_NOTES;

//import com.example.dima.smarttool.GPSService;

public class AddNoteActivity extends AppCompatActivity {

    static int hour, minute, condition = 0;
    static long milliseconds = 999999999;
    static double lat, lng;
    EditText nameEditText, textEditText;
    String name, text;
    Long startTime, time;
    AlarmManager alarmManager;
    Spinner conditionSpinner;

    public static void setTime(int hour, int minute) {
        milliseconds = hour * 3_600_000 + minute * 60_000;
    }

    public static void setHour(int hour) {
        AddNoteActivity.hour = hour;
    }

    public static void setMinute(int minute) {
        AddNoteActivity.minute = minute;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_save_menu, menu);
        return true;
    }
    //Установка широты и долготы

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {

            SharedPreferences prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
            final SharedPreferences.Editor ed = prefs.edit();

            final NoteHelper nh = new NoteHelper(getApplicationContext());

            name = nameEditText.getText().toString();
            text = textEditText.getText().toString();
            startTime = milliseconds;
            ed.putBoolean(FIRST_START_NOTES, false);
            ed.apply();
            Log.d("addNote", "condition = " + condition);
            if (name.length() == 0)
                Toast.makeText(getApplicationContext(), getString(R.string.input_name), Toast.LENGTH_SHORT).show();
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
                stopService(new Intent(AddNoteActivity.this, GeoService.class));
                startService(new Intent(AddNoteActivity.this, GeoService.class));
                finish();
            }


        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        MobileAds.initialize(this, "ca-app-pub-3330723388651797~2636638991");
        AdView mAdView = findViewById(R.id.addNoteAdView);
        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Toolbar toolbar = findViewById(R.id.noteToolbar);
        setSupportActionBar(toolbar);


        hour = 0;
        minute = 0;
        milliseconds = 0;
        lat = 0;
        lng = 0;
        conditionSpinner = findViewById(R.id.addNoteConditionSpinner);
        nameEditText = findViewById(R.id.addNoteNameEditText);
        textEditText = findViewById(R.id.addNoteTextEditText);
        textEditText.setMovementMethod(new ScrollingMovementMethod());

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
                        startActivityForResult(new Intent(AddNoteActivity.this,
                                MapsActivity.class), 1);
                        condition = 1;
                        break;
                    case 2:
                        DialogFragment newFragment = new TimePickerFragment();
                        newFragment.show(getFragmentManager(), "timePicker");
                        condition = 2;
                        break;
                    default:
                        Log.d("addNote", "condition = " + condition);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Установка условий

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            lat = data.getDoubleExtra("lat", 0);
            lng = data.getDoubleExtra("lng", 0);
        }
    }

    public void onBackPressed() {
        finish();
    }
}
