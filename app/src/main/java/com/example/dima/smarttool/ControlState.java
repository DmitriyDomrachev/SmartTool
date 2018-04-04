package com.example.dima.smarttool;

import android.bluetooth.BluetoothAdapter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
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
    ConnectivityManager connMgr;  // mobile и bluetooth
    ArrayList<State> states;
    static Map<String, Integer> stateTimeMap = new HashMap<String, Integer>();
    Date date = new Date();
    WifiManager wifiManager ;
    BluetoothAdapter btAdapter;

    Method dataConnSwitchmethod;
    Class telephonyManagerClass;
    Object ITelephonyStub;
    Class ITelephonyClass;
    TelephonyManager telephonyManager;


    public void setStates(ArrayList<State> states) {
        this.states = states;
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            String time = TimeUnit.MILLISECONDS.toHours(state.getStartTime()) + ":" + TimeUnit.MILLISECONDS.toMinutes(state.getStartTime() - TimeUnit.MILLISECONDS.toHours(state.getStartTime()) * 3600000);
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
                state.wifiState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                state.bluetoothState = btAdapter.isEnabled();
                state.mobileState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
                MainActivity.rewriteFragment();

                Calendar calendar = Calendar.getInstance();
                date.setTime(calendar.getTimeInMillis());
                String time = date.getHours() + ":" + date.getMinutes();
                if (stateTimeMap.get(time) != null)
                    setState(stateTimeMap.get(time));

                try {
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
        State state1 = states.get(id);
        Log.d("time", "setState " +state1.getName());
        Log.d("time", "wifi " +state1.isWiFiState());
        Log.d("time", "mobile " +state1.isMobileState());
        Log.d("time", "bt " +state1.isBluetoothState());

        wifiManager.setWifiEnabled(state1.wifiState);
        if (state1.isBluetoothState()) btAdapter.enable();
        else btAdapter.disable();


    }




}
