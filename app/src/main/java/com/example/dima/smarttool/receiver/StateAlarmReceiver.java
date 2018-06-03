package com.example.dima.smarttool.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.R;
import com.example.dima.smarttool.State;
import com.example.dima.smarttool.activity.ShowActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.dima.smarttool.GeoService.STATE_NOTIFICATION_CHANNEL_ID;
import static com.example.dima.smarttool.GeoService.STATE_SOUND_NOTIFICATION_CHANNEL_ID;
import static com.example.dima.smarttool.fragment.SettingFragment.NOTIF_STATE_SETTING;
import static com.example.dima.smarttool.fragment.SettingFragment.SOUND_NOTIF_STATE_SETTING;


public class StateAlarmReceiver extends BroadcastReceiver {
    static Map<String, State> stateTimeMap = new HashMap<String, State>();
    //храниение значиний времени старта и номера состояния
    static AudioManager audioManager;
    static SharedPreferences prefs;
    String name;
    State state;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    WifiManager wifiManager;
    static NotificationManager notificationManager;


    @Override
    public void onReceive(Context context, Intent intent) {

        name = intent.getStringExtra("nameEditText");
        Log.d("alarm", "receive");
        StateHelper sh = new StateHelper(context);
        // инициализация помощника управления состояниямив базе данных
        loadStates(sh.getAll());
        prefs = context.getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);




        if (stateTimeMap.get(name) != null) {
            this.state = stateTimeMap.get(name);
            setState(state);
            SharedPreferences prefs = context.getSharedPreferences("myPrefs",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("stateName", state.getName());
            ed.apply();
            HistoryHelper hh = new HistoryHelper(context);
            hh.insert( context.getResources().getString(R.string.state) + ": " + state.getName()
                    + "\n" + context.getResources().getString(R.string.start_at)+ ": " + getDate());

            Log.d("alarm", "setState: " + name);
            if (prefs.getBoolean(NOTIF_STATE_SETTING, true))
                sendNotification(context);

        }

    }


    public void loadStates(ArrayList<State> states) {
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            stateTimeMap.put(state.getName(), state);
            Log.d("timeS", "loadState: " + state.getName() + " " + state.getMediaSoundState());
        }

    }

    private void setState(State state) {
        wifiManager.setWifiEnabled(state.isWiFiState());
        if (state.isBluetoothState()) btAdapter.enable();
        else btAdapter.disable();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, numToPercent(state.getMediaSoundState(),
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, numToPercent(state.getSystemSoundState(),
                audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);
        Log.d("timeS", "setState: " + state.getName());

    }

    private int numToPercent(float in, int max) {
        in = in / 100;
        return (int) (max * in);
    }



    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormat.format(new Date());
    }

    private void sendNotification(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(STATE_NOTIFICATION_CHANNEL_ID,
                    "State notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel 1");
            notificationChannel.enableLights(true);
            notificationChannel.setSound(null, null);
            notificationChannel.enableVibration(false);


            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);

            notificationChannel = new NotificationChannel(STATE_SOUND_NOTIFICATION_CHANNEL_ID,
                    "State notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel 2");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(false);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);


            }
        }


        Intent notifyIntent = new Intent(context, ShowActivity.class);
        notifyIntent.putExtra("type", "State");
        notifyIntent.putExtra("name", state.getName());
        notifyIntent.putExtra("lat", state.getLat());
        notifyIntent.putExtra("lng", state.getLng());
        notifyIntent.putExtra("time", state.getStartTime());
        notifyIntent.putExtra("text", "Wifi: " + state.isWiFiState()
                + "\nBluetooth: " + state.isBluetoothState() + "\nMedia: " + state.getMediaSoundState()
                + "%\nSystem: " + state.getSystemSoundState() + "%");
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (prefs.getBoolean(SOUND_NOTIF_STATE_SETTING, false)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, STATE_SOUND_NOTIFICATION_CHANNEL_ID)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.list)
                    .setContentTitle("Установлен профиль")
                    .setContentText(state.getName())
                    .setAutoCancel(true)
                    .setContentIntent(notifyPendingIntent);
            assert notificationManager != null;
            notificationManager.notify(state.getId(), builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, STATE_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.list)
                    .setContentTitle("Установлен профиль")
                    .setContentText(state.getName())
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setContentIntent(notifyPendingIntent);
            assert notificationManager != null;
            notificationManager.notify(state.getId(), builder.build());
        }

    }


}
