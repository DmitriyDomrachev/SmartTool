package com.example.dima.smarttool;

import android.bluetooth.BluetoothAdapter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by dima on 10.03.2018.
 */

public class ControlState extends MainActivity {
    private State state = new State();
    public BluetoothAdapter btAdapter;
    ConnectivityManager connMgr;  // mobile и bluetooth
    ArrayList<State> states;
    Map<String, Integer> stateTimeMap = new HashMap<String, Integer>();
    Date date = new Date();


    public void setStates(ArrayList<State> states) {
        this.states = states;
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            String time = TimeUnit.MILLISECONDS.toHours(state.getStartTime()) + ":" + TimeUnit.MILLISECONDS.toMinutes(state.getStartTime() - TimeUnit.MILLISECONDS.toHours(state.getStartTime()) * 3600000);
            stateTimeMap.put(time, state.id);
            Log.d("time", "setState " + time);

        }

    }

    public void scanBattery(int bat) {
        state.batteryState = bat;
    }

    public void scanSound(int volume) {
        state.soundState = 100 * volume / 7;


    }

    public void startScan(ArrayList states) {
        this.states = new ArrayList<>(states);
        new Scanning().execute();
    }                                                                   //запуск потока сканирования

    private class Scanning extends AsyncTask<Void, Integer, Void> {
        protected void onPreExecute(int y) {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            while (true) {
                state.wifiState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                state.bluetoothState = btAdapter.isEnabled();
                state.mobileState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
                MainActivity.rewriteFragment();

                try {
                    Calendar calendar = Calendar.getInstance();
                    date.setTime(calendar.getTimeInMillis());
                    String time = date.getHours() + ":" + date.getMinutes();
                    if (stateTimeMap.get(time) != null)
                        setState(stateTimeMap.get(time));
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        protected void onPostExecute(Void image) {

        }
    }  // поток постоянного сканирования устройства

    public boolean isWiFiState() {
        return state.wifiState;
    }

    public boolean isBluetoothState() {

        return state.bluetoothState;
    }

    public boolean isMobileState() {
        return state.mobileState;
    }

    public int getBatteryState() {
        return state.batteryState;
    }

    public int getSoundState() {
        return state.soundState;
    }

    public void setState(int id) {
        Log.d("time", "setState" + id);
//        State state = states.get(2);
//        WifiManager wifiManager = (WifiManager) MainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//        wifiManager.setWifiEnabled(state.wifiState);
        if (state.isBluetoothState()) btAdapter.enable();
        else btAdapter.disable();


    }


}
