package com.example.dima.smarttool;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {

        intent = new Intent(context, Scanning.class);
        context.startService(intent);
        Log.d("alarm", "receive");

    }
}
