package com.example.dima.smarttool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ShowActivity extends AppCompatActivity {
    Intent intent;
    TextView name, text;
    Button condition;
    double lat = 0, lng = 0;
    long time = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Log.d("showActivity", "Create lat: " + lat + "    lng: " + lng);

        intent = getIntent();
        name = findViewById(R.id.showNameText);
        text = findViewById(R.id.showText);
        condition = findViewById(R.id.showConditionButton);
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

        final String finalHour = hour;
        final String finalMinute = minute;
        condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("showActivity", "lat: " + lat + "    lng: " + lng);
                if ((lat != 0 || lng != 0) && time == 999999999) {              //если запуск по gps открывается гугл карта
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + lat + "," + lng + "("
                            + String.valueOf(name.getText()) + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } else if (time != 0 && time != 999999999) {                      //если запуск по времени открывается диалоговое окно c временем запуска
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
                    builder.setTitle(String.valueOf(name.getText()))
                            .setMessage("Включение в " + finalHour + ":" + finalMinute)
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
                            .setMessage("Нет условий для автоматического включения")
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


            }
        });


    }


}


//56.90146805056319
//60.613915175199516