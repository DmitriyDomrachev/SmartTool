package com.example.dima.smarttool.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.dima.smarttool.GeoService;

//import com.example.dima.smarttool.GPSService;

public class StartServiceReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, GeoService.class));
        } else {
            context.startService(new Intent(context, GeoService.class));
        }
    }
}
