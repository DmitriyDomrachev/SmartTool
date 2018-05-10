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
import android.content.pm.PackageManager;
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
import android.support.v4.content.ContextCompat;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;

public class GPSService extends Service {
    private static final String TAG = "mygps";
    private static final String NOTIFICATION_CHANNEL_ID = "note_notification_channel";
    static long minInterval, minDistance;                               // время обновления
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
    private static final String[] READ_ACCESS_FINE = new String[]{READ_CONTACTS, ACCESS_FINE_LOCATION};
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
//                    Intent notifyIntent = new Intent(getApplicationContext(), ShowActivity.class);
//                    notifyIntent.putExtra("name", note.getName());
//                    notifyIntent.putExtra("text", note.getText());
//                    notifyIntent.putExtra("lat", note.getLat());
//                    notifyIntent.putExtra("lng", note.getLng());
//                    notifyIntent.putExtra("time", note.getStartTime());
//                    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    PendingIntent notifyPendingIntent = PendingIntent.getActivity(
//                            getApplicationContext(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
//                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                            .setSmallIcon(R.drawable.note)
//                            .setContentTitle("Note time")
//                            .setContentText("Note: " + note.getName())
//                            .setAutoCancel(true)
//                            .setContentIntent(notifyPendingIntent);
//                    notificationManager.notify(note.getId(), builder.build());

                    sendNotification(getApplicationContext(), note);

                    HistoryHelper hh = new HistoryHelper(getApplicationContext());


                    hh.insert("Заметка: " + note.getText() + "\nВремя включения: " + getDate());

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


        LocationManager locationManager
                = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert locationManager != null;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, getMinTime(),
                    getMinDistance(), locationListener);
        else  onDestroy();


        StateHelper sh = new StateHelper(getApplicationContext());
        loadStates(sh.getAll());
        NoteHelper nh = new NoteHelper(getApplicationContext());
        loadNotes(nh.getAll());
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        Log.d(TAG, "createService");
        Log.d(TAG, "getMinDistance: " + getMinDistance());
        Log.d(TAG, "getMinTime: " + getMinTime());
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
            hh.insert("Состояние: " + state.getName() + "\nВремя включения: " + getDate());
            Toast.makeText(getApplicationContext(), "set:" + state.getName(), Toast.LENGTH_SHORT).show();
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
        }

    }

    private int numToPercent(float in, int max) {
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

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormat.format(new Date());
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

        switch (prefs.getInt("timeLocationSetting", 1)) {


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
                return 0;


        }

    }

    private void sendNotification(Context context, State state) {


        Intent notifyIntent = new Intent(context, ShowActivity.class);
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.list)
                .setContentTitle("Note time")
                .setContentText("Note: " + state.getName())
                .setAutoCancel(true)
                .setContentIntent(notifyPendingIntent);
        assert notificationManager != null;
        notificationManager.notify(state.getId(), builder.build());
    }

    private void sendNotification(Context context, Note note) {

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

        Intent notifyIntent = new Intent(context, ShowActivity.class);
        notifyIntent.putExtra("name", note.getName());
        notifyIntent.putExtra("lat", note.getLat());
        notifyIntent.putExtra("lng", note.getLng());
        notifyIntent.putExtra("time", note.getStartTime());
        notifyIntent.putExtra("text", note.getText());
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.note)
                .setContentTitle("Note time")
                .setContentText("Note: " + note.getName())
                .setAutoCancel(true)
                .setContentIntent(notifyPendingIntent);
        assert notificationManager != null;
        notificationManager.notify(note.getId(), builder.build());
    }
}
