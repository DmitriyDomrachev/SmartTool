package com.example.dima.smarttool.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.dima.smarttool.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ShowActivity extends AppCompatActivity {
    Toolbar toolbar;
    Intent intent;
    TextView name, text;
    double lat = 0, lng = 0;
    long time = 0;
    static String finalHour;
    static String finalMinute;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_condition_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((lat != 0 || lng != 0) && time == 999999999) {
            //если запуск по gps открывается гугл карта
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + lat + "," + lng + "("
                    + String.valueOf(name.getText()) + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else if (time != 0 && time != 999999999) {
            // /если запуск по времени открывается диалоговое окно c временем запуска
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
            builder.setTitle(String.valueOf(name.getText()))
                    .setMessage(getString(R.string.start_at)+" "+ finalHour + ":" + finalMinute)
                    .setIcon(R.drawable.alarm)
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
            builder.setTitle(String.valueOf(name.getText()))
                    .setMessage(getString(R.string.dont_have_condition))
                    .setIcon(R.drawable.hand)
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Log.d("showActivity", "Create lat: " + lat + "    lng: " + lng);

        intent = getIntent();
        toolbar = findViewById(R.id.showToolbar);
        setSupportActionBar(toolbar);

        if (Objects.equals(intent.getStringExtra("type"), "State"))
            toolbar.setTitle( getString(R.string.state));
        else
            toolbar.setTitle( getString(R.string.note));
        name = findViewById(R.id.showNameText);
        text = findViewById(R.id.showText);
        name.setText(intent.getStringExtra("name"));
        text.setText(intent.getStringExtra("text"));
        lat += intent.getDoubleExtra("lat", 0);
        lng += intent.getDoubleExtra("lng", 0);
        time += intent.getLongExtra("time", 0);
        int hourI = (int) TimeUnit.MILLISECONDS.toHours(time);
        String hour = String.valueOf(TimeUnit.MILLISECONDS.toHours(time));
        String minute = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(time - hourI * 3600000));
        if (Objects.equals(minute, "0")) {
            minute = "00";
        }
        if (Objects.equals(hour, "0")) {
            hour = "00";
        }

        finalHour = hour;
        finalMinute = minute;



    }


}

