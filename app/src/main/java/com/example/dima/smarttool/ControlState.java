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

        state.WiFiState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

    }

    public void scanBluetooth() {
        state.BluetoothState = btAdapter.isEnabled();

    }

    public void scanMobile() {
        state.MobileState = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    }

    public void scanBattery(int bat) {
       state.BatteryState = bat;
    }

    public void scanSound(int volume) {
        state.SoundState = 100*volume/7;


    }

    public void addState(boolean wifiST, boolean btST, boolean mbST, int batST, int soundST) {
        state.addState(wifiST, btST, mbST, batST, soundST);
    }

    public void setState(int numState) {
        state = state.getState(numState);
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
        return state.WiFiState;
    }

    public boolean isBluetoothState() {

        return state.BluetoothState;
    }

    public boolean isMobileState() {
        return state.MobileState;
    }




    public int getBatteryState() {
        return state.BatteryState;
    }

    public int getSoundState() {
        return state.SoundState;
    }


}
