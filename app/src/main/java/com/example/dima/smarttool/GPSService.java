package com.example.dima.smarttool;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.DB.NoteHelper;
import com.example.dima.smarttool.DB.StateHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class GPSService extends Service {
    static final long MIN_INTERVAL = 1000;                               // время обновления
    private static final String TAG = "mygps";
    private static final String NOTIFICATION_CHANNEL_ID = "note_notification_channel";
    static HashMap<Integer, State> stateGpsMap = new HashMap<>();
    static ArrayList<LatLng> stateGpsList = new ArrayList<LatLng>();
    static HashMap<Integer, Note> noteGpsMap = new HashMap<>();
    static ArrayList<LatLng> noteGpsList = new ArrayList<LatLng>();
    static String currentStateName, currentNoteName;
    static AudioManager audioManager;
    static NotificationManager notificationManager;
    static SharedPreferences prefs;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    WifiManager wifiManager;
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "широта:" + location.getLatitude());
            Log.d(TAG, "долгота:" + location.getLongitude());
            double lat = location.getLatitude();
            double lng = location.getLongitude();


            if (stateGpsMap.get(checkLatLngState(lat, lng)) != null)
                setState(stateGpsMap.get(checkLatLngState(lat, lng)));

            if (noteGpsMap.get(checkLatLngNote(lat, lng)) != null) {
                Note note = noteGpsMap.get(checkLatLngNote(lat, lng));
                Log.d(TAG, "try noteName: " + note.getName());
                Log.d(TAG, "pref noteName: " + prefs.getString("noteName", ""));

                if ((!Objects.equals(String.valueOf(prefs.getString("noteName", "")), note.getName()))) {
                    Intent notifyIntent = new Intent(getApplicationContext(), ShowNoteActivity.class);
                    notifyIntent.putExtra("name", note.getName());
                    notifyIntent.putExtra("note", note.getText());
                    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                            getApplicationContext(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setSmallIcon(R.drawable.note)
                            .setContentTitle("Note time")
                            .setContentText("Note: " + note.getName())
                            .setAutoCancel(true)
                            .setContentIntent(notifyPendingIntent);
                    notificationManager.notify(note.getId(), builder.build());

                    HistoryHelper hh = new HistoryHelper(getApplicationContext());
                    hh.insert("Заметка: " + note.getText() + "\nВремя включения: " + getTime());

                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("noteName", note.getName());
                    ed.apply();

                    Log.d(TAG, "noteName: " + note.getName());
                    Toast.makeText(getApplicationContext(), "setNote: " + note.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        }


        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public GPSService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        prefs = getApplicationContext().getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);

        notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500,
                    1000});

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }


        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_INTERVAL,
                100, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_INTERVAL,
                100, locationListener);
        StateHelper sh = new StateHelper(getApplicationContext());
        loadStates(sh.getAll());
        NoteHelper nh = new NoteHelper(getApplicationContext());
        loadNotes(nh.getAll());
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        Log.d(TAG, "createService");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "startService");

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "bindService");

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroyService");

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

        if ((!Objects.equals(String.valueOf(prefs.getString("stateName", "")), state.getName()))) {

            HistoryHelper hh = new HistoryHelper(getApplicationContext());
            hh.insert("Состояние: " + state.getName() + "\nВремя включения: " + getTime());
            Toast.makeText(getApplicationContext(), "set:" + state.getName(), Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(state.isWiFiState());

            if (state.isBluetoothState()) btAdapter.enable();
            else btAdapter.disable();

            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progInex(state.getMediaSoundState(),
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progInex(state.getSystemSoundState(),
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);

            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("stateName", state.getName());
            ed.apply();
            Log.d(TAG, "setState: " + state.getName());
        }

    }

    private int progInex(float in, int max) {
        in = in / 100;
        return (int) (max * in);
    }

    private int checkLatLngState(double lat, double lng) {

        for (int i = 0; i < stateGpsList.size(); i++) {
            if (Math.pow((stateGpsList.get(i).latitude - lat), 2) + Math.pow((stateGpsList.get(i).longitude - lng), 2) <= Math.pow(9.986137129513397E-4, 2)) {
                Log.d(TAG, "set " + i + ": " + stateGpsMap.get(i).getName());
                return i;
            }
            Log.d(TAG, Math.pow((lat - stateGpsList.get(i).latitude), 2) + Math.pow((lng - stateGpsList.get(i).latitude), 2) + "    " + Math.pow(9.986137129513397E-4, 2));
            Log.d(TAG, stateGpsList.get(i).latitude + "");
            Log.d(TAG, stateGpsList.get(i).longitude + "");

        }

        return 999;
    }

    private int checkLatLngNote(double lat, double lng) {

        for (int i = 0; i < noteGpsList.size(); i++) {
            if (Math.pow((noteGpsList.get(i).latitude - lat), 2) + Math.pow((noteGpsList.get(i).longitude - lng), 2) <= Math.pow(4.986137129513397E-4, 2)) {
                Log.d(TAG, "set " + i + ": " + noteGpsMap.get(i).getName());
                return i;
            }
            Log.d(TAG, Math.pow((lat - noteGpsList.get(i).latitude), 2) + Math.pow((lng - noteGpsList.get(i).latitude), 2) + "    " + Math.pow(4.986137129513397E-4, 2));
            Log.d(TAG, noteGpsList.get(i).latitude + "");
            Log.d(TAG, noteGpsList.get(i).longitude + "");
        }
        return 999;
    }


    private String getTime() {                                                          // используйте метод для вывода текущего времени
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        date.setTime(calendar.getTimeInMillis());
        return date.getHours() + ":" + date.getMinutes();
    }

}
