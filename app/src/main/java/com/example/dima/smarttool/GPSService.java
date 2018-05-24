package com.example.dima.smarttool;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.DB.NoteHelper;
import com.example.dima.smarttool.DB.StateHelper;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static com.example.dima.smarttool.fragment.SettingFragment.NOTIF_STATE_SETTING;
import static com.example.dima.smarttool.fragment.SettingFragment.SOUND_NOTIF_NOTE_SETTING;
import static com.example.dima.smarttool.fragment.SettingFragment.SOUND_NOTIF_STATE_SETTING;

public class GPSService extends Service {
    public static final String TAG = "mygps", WIFI_PREFS = "currentWiFi",
            BLUETOOTH_PREFS = "currentBluetooth", MEDIA_PREFS = "currentMedia",
            SYSTEM_PREFS = "currentSYSTEM", STATE_NOTIFICATION_CHANNEL_ID = "state_notification_channel",
            NOTE_NOTIFICATION_CHANNEL_ID = "note_notification_channel",
            FOREGROUND_NOTIFICATION_CHANNEL_ID = "foreground_notification_channel",
    NOTE_SOUND_NOTIFICATION_CHANNEL_ID = "note_sound_notification_channel",
            STATE_SOUND_NOTIFICATION_CHANNEL_ID = "state_sound_notification_channel", CURRENT_STATE = "currentState";
    static HashMap<Integer, State> stateGpsMap = new HashMap<>();
    static HashMap<Integer, Note> noteGpsMap = new HashMap<>();
    static ArrayList<LatLng> stateGpsList = new ArrayList<LatLng>();
    static ArrayList<LatLng> noteGpsList = new ArrayList<LatLng>();
    static AudioManager audioManager;
    static NotificationManager notificationManager;
    static SharedPreferences prefs;
    static boolean isCurrentStateSetted;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    WifiManager wifiManager;

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private LocationManager mLocationManager = null;

    public GPSService() {
    }



    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification();     //запуск сервиса в foreground

        prefs = getApplicationContext().getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);

        notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(NOTIFICATION_SERVICE);

        LocationManager locationManager
                = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, getMinTime(), getMinDistance(),
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, getMinTime(), getMinDistance(),
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        //установка отслеживания местоположения


        StateHelper sh = new StateHelper(getApplicationContext());
        loadStates(sh.getAll());
        NoteHelper nh = new NoteHelper(getApplicationContext());
        loadNotes(nh.getAll());
        //загрузка заметок и напоминаний
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        Log.d(TAG, "createService");
        Log.d(TAG, "getMinDistance: " + getMinDistance());
        Log.d(TAG, "getMinTime: " + getMinTime());

        return Service.START_STICKY;
    }



    private void loadStates(ArrayList<State> states) {
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            LatLng latLng = new LatLng(state.getLat(), state.getLng());
            stateGpsList.add(latLng);
            stateGpsMap.put(i, state);
            Log.d(TAG, "loadState: " + state.getName());
        }

    }

    public void loadNotes(ArrayList<Note> notes) {
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            LatLng latLng = new LatLng(note.getLat(), note.getLng());
            noteGpsList.add(latLng);
            noteGpsMap.put(i, note);
            Log.d(TAG, "loadNote: " + note.getName());
        }

    }

    private void setState(State state) {


        HistoryHelper hh = new HistoryHelper(getApplicationContext());
        hh.insert("Состояние: " + state.getName() + "\nВремя включения: " + getDate());
        wifiManager.setWifiEnabled(state.isWiFiState());

        if (state.isBluetoothState()) btAdapter.enable();
        else btAdapter.disable();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, numToPercent(state.getMediaSoundState(),
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, numToPercent(state.getSystemSoundState(),
                audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);

        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("stateName", state.getName());
        ed.apply();
        Log.d(TAG, "setState: " + state.getName());
        if (prefs.getBoolean(NOTIF_STATE_SETTING, true))
            sendNotification(getApplicationContext(), state);


    }

    private int numToPercent(float in, int max) {
        in = in / 100;
        return (int) (max * in);
    }

    private int checkLatLngState(double lat, double lng) {

        for (int i = 0; i < stateGpsList.size(); i++) {
            if (Math.pow((stateGpsList.get(i).latitude - lat), 2) +
                    Math.pow((stateGpsList.get(i).longitude - lng), 2) <= Math.pow(16.986137129513397E-4, 2)) {
                Log.d(TAG, "set " + i + ": " + stateGpsMap.get(i).getName());
                return i;
            }
            Log.d(TAG, Math.pow((lat - stateGpsList.get(i).latitude), 2)
                    + Math.pow((lng - stateGpsList.get(i).latitude), 2)
                    + "    " + Math.pow(16.986137129513397E-4, 2));

            Log.d(TAG, stateGpsList.get(i).latitude + "");
            Log.d(TAG, stateGpsList.get(i).longitude + "");

        }

        return 999;
    }
    //поиск состояния по координатам

    private int checkLatLngNote(double lat, double lng) {

        for (int i = 0; i < noteGpsList.size(); i++) {
            if (Math.pow((noteGpsList.get(i).latitude - lat), 2)
                    + Math.pow((noteGpsList.get(i).longitude - lng), 2) <= Math.pow(16.986137129513397E-4, 2)) {
                Log.d(TAG, "set " + i + ": " + noteGpsMap.get(i).getName());
                return i;
            }
            Log.d(TAG, Math.pow((lat - noteGpsList.get(i).latitude), 2)
                    + Math.pow((lng - noteGpsList.get(i).latitude), 2)
                    + "    " + Math.pow(16.986137129513397E-4, 2));

            Log.d(TAG, "check" + noteGpsList.get(i).latitude + "");
            Log.d(TAG, "check" + noteGpsList.get(i).longitude + "");
        }
        return 999;
    }
    //поиск напоминания по координатам


    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormat.format(new Date());
    }

    private State getLastState() {
        boolean wifi = prefs.getBoolean(WIFI_PREFS, false);
        boolean bluetooth = prefs.getBoolean(BLUETOOTH_PREFS, false);
        int media = prefs.getInt(MEDIA_PREFS, 0);
        int system = prefs.getInt(SYSTEM_PREFS, 0);
        return new State(wifi, bluetooth, media, system, "Пользовательский");
    }

    private void setLastState(State lastState) {
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean(WIFI_PREFS, lastState.isWiFiState());
        ed.putBoolean(BLUETOOTH_PREFS, lastState.isWiFiState());
        ed.putInt(MEDIA_PREFS, lastState.getMediaSoundState());
        ed.putInt(SYSTEM_PREFS, lastState.getSystemSoundState());
        ed.apply();
    }

    private State getCurrentState() {
        return new State(wifiManager.isWifiEnabled(), btAdapter.isEnabled(),
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
                audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
    }

    private long getMinDistance() {
        switch (prefs.getInt("distanceLocationSetting", 1)) {
            case 0:
                return 10L;
            case 1:
                return 20L;
            case 2:
                return 50L;
            case 3:
                return 100L;
            case 4:
                return 200l;
            case 5:
                return 500l;
            default:
                return 0;
        }
    }

    private long getMinTime() {
        Log.d(TAG, "getTime: " + prefs.getInt("timeLocationSetting", 0));
        switch (prefs.getInt("timeLocationSetting", 0)) {

            case 0:
                return 60 * 1000L;
            case 1:
                return 5 * 60 * 1000L;
            case 2:
                return 10 * 60 * 1000L;
            case 3:
                return 20 * 60 * 1000L;
            case 4:
                return 40 * 60 * 1000L;
            case 5:
                return 60 * 60 * 1000L;
            default:
                return 1;


        }

    }

    private void sendNotification(Context context, State state) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(STATE_NOTIFICATION_CHANNEL_ID,
                    "State notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel 1");
            notificationChannel.enableLights(true);
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

    private void sendNotification(Context context, Note note) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTE_NOTIFICATION_CHANNEL_ID,
                    "Note notifications", NotificationManager.IMPORTANCE_MIN);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel 01");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);

            notificationChannel = new NotificationChannel(NOTE_SOUND_NOTIFICATION_CHANNEL_ID,
                    "Note notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel 02");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);

        }


        Intent notifyIntent = new Intent(context, ShowActivity.class);
        notifyIntent.putExtra("type", "Note");
        notifyIntent.putExtra("name", note.getName());
        notifyIntent.putExtra("lat", note.getLat());
        notifyIntent.putExtra("lng", note.getLng());
        notifyIntent.putExtra("time", note.getStartTime());
        notifyIntent.putExtra("text", note.getText());
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );




        if (prefs.getBoolean(SOUND_NOTIF_NOTE_SETTING, false)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, STATE_SOUND_NOTIFICATION_CHANNEL_ID)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.list)
                    .setContentTitle("Установлен профиль")
                    .setContentText(note.getName())
                    .setAutoCancel(true)
                    .setContentIntent(notifyPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            assert notificationManager != null;
            notificationManager.notify(note.getId(), builder.build());


        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, STATE_NOTIFICATION_CHANNEL_ID)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.list)
                    .setContentTitle("Установлен профиль")
                    .setContentText(note.getName())
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setAutoCancel(true)
                    .setContentIntent(notifyPendingIntent);

            assert notificationManager != null;
            notificationManager.notify(note.getId(), builder.build());


        }



    }

    public void sendNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(FOREGROUND_NOTIFICATION_CHANNEL_ID,
                    "Foreground notifications", NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription("Foreground channel");
            notificationChannel.enableLights(true);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);



        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), FOREGROUND_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notificatoin_icon)
                .setContentTitle("SmartTool")
                .setContentText("Отслеживание местоположения")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        startForeground(231,builder.build());


    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext()
                    .getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {


            double lat = location.getLatitude();
            double lng = location.getLongitude();


            isCurrentStateSetted = prefs.getBoolean(CURRENT_STATE, true);
            if (stateGpsMap.get(checkLatLngState(lat, lng)) != null && checkLatLngState(lat, lng) != 999) {
                State state = stateGpsMap.get(checkLatLngState(lat, lng));
                if ((!Objects.equals(String.valueOf(prefs.getString("stateName", "")), state.getName()))) {
                    setLastState(getCurrentState());
                    setState(state);
                    isCurrentStateSetted = false;
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putBoolean(CURRENT_STATE, isCurrentStateSetted);
                    ed.apply();
                }
            } else if (checkLatLngState(lat, lng) == 999 && !isCurrentStateSetted) {
                setState(getLastState());
                isCurrentStateSetted = true;
                SharedPreferences.Editor ed = prefs.edit();
                ed.putBoolean(CURRENT_STATE, isCurrentStateSetted);
                ed.apply();
            }

            if (noteGpsMap.get(checkLatLngNote(lat, lng)) != null) {
                Note note = noteGpsMap.get(checkLatLngNote(lat, lng));
                Log.d(TAG, "try noteName: " + note.getName());
                Log.d(TAG, "pref noteName: " + prefs.getString("noteName", ""));

                if ((!Objects.equals(String.valueOf(prefs.getString("noteName", "")), note.getName()))) {
                    sendNotification(getApplicationContext(), note);
                    HistoryHelper hh = new HistoryHelper(getApplicationContext());
                    hh.insert("Напоминание: " + note.getName() + "\nВремя включения: " + getDate());
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("noteName", note.getName());
                    ed.apply();
                    Log.d(TAG, "noteName: " + note.getName());
                    Toast.makeText(getApplicationContext(), "setNote: " + note.getName(), Toast.LENGTH_SHORT).show();
                }
            }
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    //слушатель изменений местоположения
}
