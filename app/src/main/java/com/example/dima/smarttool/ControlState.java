package com.example.dima.smarttool;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by dima on 10.03.2018.
 */

public class ControlState extends MainActivity {
    private State state;
    static boolean WiFiStateScan, BluetoothStateScan, MobileStateScan, WiFiStateSelect, BluetoothStateSelect, MobileStateSelect;
    static int SoundStateScan, BatteryStateSelect, SoundStateSelect, BatteryStateScan;
    public WifiManager wifiManager;
    public BluetoothAdapter btAdapter;




    /*public State getScanState ( ){
        State st = new State(WiFiStateScan, BluetoothStateScan, MobileStateScan, BatteryStateScan, SoundStateScan);
        return st;

    }*/

    public void scanWiFi() {
        this.WiFiStateScan = wifiManager.isWifiEnabled();
        Log.d("test", "wifiscan: " + WiFiStateScan);

    }

    public void scanBluetooth() {
        BluetoothStateScan = btAdapter.isEnabled();
        Log.d("test", "btscan: " + BluetoothStateScan);

    }

    public void scanMobile() {
        MobileStateScan = true;
    }

    public void scanBattery() {
        Log.d("test1", "bat"+ getBatteryLevel());
        BatteryStateScan = (int)getBatteryLevel();
    }

    public void scanSound() {
        SoundStateScan = 100;
    }

    public void addState(boolean wifiST, boolean btST, boolean mbST, int batST, int soundST) {
        state.addState(wifiST, btST, mbST, batST, soundST);
    }

    public void setState(int numState) {
        state.getState(numState);
        WiFiStateSelect = state.WiFiState;
        BluetoothStateSelect = state.BluetoothState;
        MobileStateScan = state.MobileState;
        BatteryStateScan = state.BatteryState;
        SoundStateScan = state.BatteryState;
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
                Log.d("test", "поток, fullscan");
                scanWiFi();
                scanBluetooth();
                scanMobile();
                scanBattery();
                scanSound();
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

    public boolean isWiFiStateScan() {
        return WiFiStateScan;
    }

    public boolean isBluetoothStateScan() {

        return BluetoothStateScan;
    }

    public boolean isMobileStateScan() {
        return MobileStateScan;
    }

    public boolean isWiFiStateSelect() {
        return WiFiStateSelect;
    }

    public boolean isBluetoothStateSelect() {
        return BluetoothStateSelect;
    }

    public boolean isMobileStateSelect() {
        return MobileStateSelect;
    }

    public int getBatteryStateScan() {
        return BatteryStateScan;
    }

    public int getSoundStateScan() {
        return SoundStateScan;
    }

    public int getBatteryStateSelect() {
        return BatteryStateSelect;
    }

    public int getSoundStateSelect() {
        return SoundStateSelect;
    }
}
