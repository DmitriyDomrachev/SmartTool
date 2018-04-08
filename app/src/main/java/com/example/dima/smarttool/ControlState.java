package com.example.dima.smarttool;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.wifi.WifiManager;
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
    static Map<String, Integer> stateTimeMap = new HashMap<String, Integer>();
    Date date = new Date();
    WifiManager wifiManager ;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    boolean first = true;
    static Intent intent;
    static ArrayList<State> states;




    public void loadStates(ArrayList<State> states) {
        for (int i = 0; i < states.size(); i++) {
            State state1 = states.get(i);
            String time = TimeUnit.MILLISECONDS.toHours(state1.getStartTime()) + ":" + TimeUnit.MILLISECONDS.toMinutes(state1.getStartTime() - TimeUnit.MILLISECONDS.toHours(state1.getStartTime()) * 3600000);
            stateTimeMap.put(time, i);
            Log.d("time", "loadState " + time);

        }

    }

    public void scanBattery(int bat) {
        state.batteryState = bat;
    }

    public void scanSound(int volume) {
        state.soundState = 100 * volume / 7;
    }

    public void startScan(ArrayList<State> states) {
        this.states = new ArrayList<>(states);
        new Scanning().execute();
    }                                                                   //запуск потока сканирования

    private class Scanning extends AsyncTask<Void, Integer, Void> {
        protected void onPreExecute(int y) {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            while (true) {
                Log.d("test", "scan");
                state.wifiState = wifiManager.isWifiEnabled();
                state.bluetoothState = btAdapter.isEnabled();

                MainActivity.rewriteFragment();
                Calendar calendar = Calendar.getInstance();
                date.setTime(calendar.getTimeInMillis());
                String time = date.getHours() + ":" + date.getMinutes();
                if (stateTimeMap.get(time) != null)
                    setState(stateTimeMap.get(time));

                try {
                    Thread.sleep(1000);
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


    public int getBatteryState() {
        return state.batteryState;
    }

    public int getSoundState() {
        return state.soundState;
    }

    public void setState(int id) {
        State state = states.get(id);
        Log.d("time", "setState " +state.getName());
        Log.d("time", "wifi " +state.isWiFiState());
        Log.d("time", "bt " +state.isBluetoothState());
        wifiManager.setWifiEnabled(state.wifiState);
        if (state.isBluetoothState()) btAdapter.enable();
        else btAdapter.disable();


    }

    public State getState() {
        return state;
    }
}
