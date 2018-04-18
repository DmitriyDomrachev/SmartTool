package com.example.dima.smarttool;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.dima.smarttool.DB.StateHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class GPSService extends Service {
    public GPSService() {
    }

    static HashMap<Integer, State> stateGpsMap = new HashMap<>();
    static ArrayList<LatLng> stateGpsList = new ArrayList<LatLng>();
    static final long MIN_INTERVAL = 60 * 1000;                               // время обновления
    private static final String TAG = "mygps";
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    WifiManager wifiManager;
    static AudioManager audioManager;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {


        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_INTERVAL, 100, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_INTERVAL, 100, locationListener);
        StateHelper sh = new StateHelper(getApplicationContext());
        loadStates(sh.getAll());

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

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "широта:" + location.getLatitude());
            Log.d(TAG, "долгота:" + location.getLongitude());
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            if (stateGpsMap.get(checkLatLng(lat, lng)) != null)
                setState(stateGpsMap.get(checkLatLng(lat, lng)));


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


    private void loadStates(ArrayList<State> states) {
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            LatLng latLng = new LatLng(state.getLat(), state.getLng());
            stateGpsList.add(latLng);
            stateGpsMap.put(i, state);
            Log.d(TAG, "loadState: " + state.getName());
        }

    }

    private void setState(State state) {
        wifiManager.setWifiEnabled(state.isWiFiState());
        if (state.isBluetoothState()) btAdapter.enable();
        else btAdapter.disable();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progInex(state.getMediaSoundState(),
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progInex(state.getSystemSoundState(),
                audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);
        Log.d(TAG, "setState: " + state.getName());

    }

    private int progInex(float in, int max) {
        in = in / 100;
        return (int) (max * in);
    }

    private int checkLatLng(double lat, double lng) {

        for (int i = 0; i < stateGpsList.size(); i++) {
            if (Math.pow((stateGpsList.get(i).latitude - lat), 2) + Math.pow((stateGpsList.get(i).longitude - lng), 2) <= Math.pow(4.986137129513397E-4, 2)) {
                Log.d(TAG, "set " + i + ": " + stateGpsMap.get(i).getName());
                return i;
            }
            Log.d(TAG, Math.pow((lat - stateGpsList.get(i).latitude), 2) + Math.pow((lng - stateGpsList.get(i).latitude), 2) + "    " + Math.pow(4.986137129513397E-4, 2));
            Log.d(TAG, stateGpsList.get(i).latitude + "");
            Log.d(TAG, stateGpsList.get(i).longitude + "");

        }

        return 999;
    }


}
