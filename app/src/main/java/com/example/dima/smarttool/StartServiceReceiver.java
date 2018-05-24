package com.example.dima.smarttool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class StartServiceReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, GPSService.class));
        } else {
            context.startService(new Intent(context, GPSService.class));
        }
    }
}
