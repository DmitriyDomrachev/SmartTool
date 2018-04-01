package com.example.dima.smarttool;

import android.bluetooth.BluetoothAdapter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

/**
 * Created by dima on 10.03.2018.
 */

public class ControlState extends MainActivity {
    private State state = new State();
    public BluetoothAdapter btAdapter;
    ConnectivityManager connMgr;


    public void scanWiFi() {

        state.wifiState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

    }

    public void scanBluetooth() {
        state.bluetoothState = btAdapter.isEnabled();

    }

    public void scanMobile() {
        state.mobileState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    }

    public void scanBattery(int bat) {
        state.batteryState = bat;
    }

    public void scanSound(int volume) {
        state.soundState = 100 * volume / 7;


    }

    public void startScan() {
        new Scanning().execute();
    }  //запуск потока сканирования

    private class Scanning extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            while (true) {
                scanWiFi();
                scanBluetooth();
                scanMobile();
                MainActivity.rewriteFragment();
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

    public boolean isMobileState() {
        return state.mobileState;
    }

    public int getBatteryState() {
        return state.batteryState;
    }

    public int getSoundState() {
        return state.soundState;
    }



}
